package com.empire.employeefinder.service.impl;

import com.empire.employeefinder.dto.response.EmployeeResponseDto;
import com.empire.employeefinder.dto.response.SelectionResponseDto;
import com.empire.employeefinder.exception.*;
import com.empire.employeefinder.kafka.model.SelectionMessage;
import com.empire.employeefinder.kafka.service.KafkaProducerService;
import com.empire.employeefinder.mapper.EmployeeMapper;
import com.empire.employeefinder.mapper.SelectionMapper;
import com.empire.employeefinder.model.Company;
import com.empire.employeefinder.model.Employee;
import com.empire.employeefinder.model.Selection;
import com.empire.employeefinder.model.User;
import com.empire.employeefinder.model.enums.Status;
import com.empire.employeefinder.repository.EmployeeRepository;
import com.empire.employeefinder.repository.SelectionRepository;
import com.empire.employeefinder.repository.UserRepository;
import com.empire.employeefinder.service.EmailService;
import com.empire.employeefinder.service.EmployeeService;
import com.empire.employeefinder.service.SelectionService;
import com.empire.employeefinder.utils.AbstractSearchUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class SelectionServiceImpl extends AbstractSearchUtil<Employee> implements SelectionService {

    private static final String SELECTION_NOT_FOUND = "Selection with id %s not found";
    private static final String EMPLOYEE_NOT_FOUND = "Employee with id %s not found";
    private static final String USER_NOT_FOUND = "User with %s %s not found";

    private final SelectionRepository selectionRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final SelectionMapper selectionMapper;
    private final EmployeeMapper employeeMapper;
    private final EmployeeService employeeService;
    private final KafkaProducerService kafkaProducerService;
    private final EmailService emailService;

    @Value("${topic.selection}")
    private String topicSelection;

    @Override
    @Transactional
    public SelectionResponseDto submitFinalSelection(String login) {
        User user = findUserByLogin(login);
        Company company = user.getCompany();

        Selection selection = selectionRepository.findByCompanyIdAndSubmittedFalse(company.getId())
                .orElseThrow(() -> new SelectionNotFoundException("No active selection found for this company."));

        selection.setSubmitted(true);

        selectionRepository.save(selection);

        sendSelectionMessage(selection);

        selection.getChosenCandidates().forEach(employee -> {
            changeStatusIfNotSelectedElsewhere(employee, user.getCompany().getId(), login);
        });

        SelectionResponseDto responseDto = selectionMapper.toResponse(selection);

        CompletableFuture.runAsync(() -> emailService.sendSelectionDetails(responseDto));

        return responseDto;
    }

    @Override
    @Transactional
    public SelectionResponseDto addEmployeeToSelection(String login, Long employeeId) {
        User user = findUserByLogin(login);
        Employee employee = findEmployeeById(employeeId);
        Company company = user.getCompany();

        boolean alreadyChosen = selectionRepository.existsByCompanyIdAndChosenCandidatesContains(
                company.getId(), employee);
        if (alreadyChosen) {
            throw new CandidateAlreadyExistsException();
        }

        Selection selection = selectionRepository.findByCompanyIdAndSubmittedFalse(company.getId())
                .orElseGet(() -> {
                    Selection newSelection = new Selection(company);
                    return selectionRepository.save(newSelection);
                });

        selection.addEmployeeToChosenCandidates(employee);

        selectionRepository.save(selection);

        return toSortedResponse(selection);
    }

    @Override
    @Transactional
    public SelectionResponseDto removeEmployeeFromSelection(String login, Long employeeId) {
        User user = findUserByLogin(login);
        Employee employee = findEmployeeById(employeeId);

        Selection selection = selectionRepository.findByCompanyIdAndSubmittedFalse(user.getCompany().getId())
                .orElseThrow(() -> new SelectionNotFoundException("No selection found for this company."));

        if (!selection.getChosenCandidates().contains(employee)) {
            throw new EmployeeNotFoundException("This candidate is not on the list");
        }

        selection.removeEmployeeFromChosenCandidates(employee);
        selectionRepository.save(selection);

        return toSortedResponse(selection);
    }

    @Override
    @Transactional
    public void clearSelection(String login) {
        User user = findUserByLogin(login);
        Long companyId = user.getCompany().getId();

        selectionRepository.findByCompanyIdAndSubmittedFalse(companyId)
                .ifPresent(selection -> {
                    selection.getChosenCandidates().clear();
                    selectionRepository.save(selection);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public SelectionResponseDto getCurrentSelection(String login) {
        return selectionRepository.findByCompanyIdAndSubmittedFalse(findUserByLogin(login).getCompany().getId())
                .map(this::toSortedResponse)
                .orElse(null);
    }

    @Override
    public SelectionResponseDto getById(Long selectionId, String searchField,
                                        String parameter, String sortField, String sortDirection,
                                        int pageSize, int pageNumber) {
        validateFieldAndParameter(searchField, parameter);

        Selection selection = findSelectionById(selectionId);
        List<Long> candidateIds = selection.getChosenCandidates().stream()
                .map(Employee::getId)
                .toList();

        if (candidateIds.isEmpty()) {
            return selectionMapper.toResponse(selection, List.of());
        }

        Specification<Employee> spec = buildSearchSpecification(searchField, parameter)
                .and((root, query, cb) -> root.get("id").in(candidateIds));

        Page<Employee> page = employeeRepository.findAll(spec, createPageRequest(pageNumber, pageSize, sortField, sortDirection));

        List<EmployeeResponseDto> candidates = page.getContent().stream()
                .map(employeeMapper::toResponse)
                .toList();

        return selectionMapper.toResponse(selection, candidates);
    }

    @Override
    protected Map<String, Function<String, Specification<Employee>>> getFieldSpecs() {
        return Map.ofEntries(
                Map.entry("id", val -> (r, q, cb) -> cb.like(r.get("id").as(String.class), val)),
                Map.entry("name", val -> (r, q, cb) -> cb.like(cb.lower(r.get("name")), val.toLowerCase() + "%")),
                Map.entry("jobPosition", val -> (r, q, cb) -> cb.like(cb.lower(r.get("jobPosition").get("name")), val.toLowerCase() + "%")),
                Map.entry("gender", val -> (r, q, cb) -> cb.like(cb.lower(r.get("gender").as(String.class)), val.toLowerCase() + "%")),
                Map.entry("location", val -> (r, q, cb) -> cb.like(cb.lower(r.get("location").as(String.class)), val.toLowerCase() + "%")),
                Map.entry("jobType", val -> (r, q, cb) -> cb.like(cb.lower(r.get("jobType").get("name")), val.toLowerCase() + "%"))
        );
    }

    @Override
    protected String mapSortField(String field) {
        return switch (field) {
            case "jobType" -> "jobType.name";
            case "jobPosition" -> "jobPosition.name";
            default -> field;
        };
    }

    @Override
    protected void validateFieldAndParameter(String searchField, String parameter) {
        String field = Optional.ofNullable(searchField).filter(f -> !f.isBlank()).orElse("default");

        if (!"default".equals(field) && !getFieldSpecs().containsKey(field)) {
            throw new IllegalArgumentException("Invalid search field: " + field);
        }

        if (parameter != null && !parameter.isEmpty()) {
            String normalPattern = "[a-zA-Z0-9.]+";

            if (Stream.of(normalPattern).noneMatch(p -> Pattern.matches(p, parameter))) {
                throw new WrongSearchParameterException("Search should be in latin letters or figures");
            }
        }
    }

    private Selection findSelectionById(Long id) {
        return selectionRepository.findById(id)
                .orElseThrow(() -> new SelectionNotFoundException(String.format(SELECTION_NOT_FOUND, id)));
    }

    private User findUserByLogin(String login) {
        return userRepository.findByEmail(login)
                .orElseThrow(() -> new UserNotFoundException(String.format(USER_NOT_FOUND, "login", login)));
    }

    private Employee findEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(String.format(EMPLOYEE_NOT_FOUND, id)));
    }

    private void changeStatusIfNotSelectedElsewhere(Employee employee, Long currentCompanyId, String login) {
        boolean isSelectedElsewhere = selectionRepository.existsByChosenCandidates_IdAndSubmittedFalseAndCompany_IdNot(
                employee.getId(), currentCompanyId
        );
        if (!isSelectedElsewhere) {
            employeeService.changeEmployeeStatus(employee.getId(), Status.SELECTED, login);
        }
    }

    private SelectionResponseDto toSortedResponse(Selection selection) {
        SelectionResponseDto dto = selectionMapper.toResponse(selection);
        Optional.ofNullable(dto.getCandidates())
                .ifPresent(candidates -> candidates.sort(Comparator.comparing(EmployeeResponseDto::getName)));
        return dto;
    }

    private void sendSelectionMessage(Selection selection) {
        List<String> candidateNames = selection.getChosenCandidates()
                .stream()
                .map(Employee::getName)
                .toList();

        SelectionMessage message = SelectionMessage.builder()
                .companyName(selection.getCompany().getCompanyName())
                .regNumber(selection.getCompany().getRegNumber())
                .candidates(candidateNames)
                .build();

        kafkaProducerService.sendMessage(message, topicSelection);
        log.info("Selection message sent from company: {}", selection.getCompany().getCompanyName());
    }
}
