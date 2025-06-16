package com.empire.employeefinder.service.impl;

import com.empire.employeefinder.dto.request.EmployeeFilterRequestDto;
import com.empire.employeefinder.dto.request.EmployeeRequestDto;
import com.empire.employeefinder.dto.response.EmployeeResponseDto;
import com.empire.employeefinder.exception.*;
import com.empire.employeefinder.mapper.EmployeeMapper;
import com.empire.employeefinder.model.Employee;
import com.empire.employeefinder.model.JobPosition;
import com.empire.employeefinder.model.JobType;
import com.empire.employeefinder.model.enums.Gender;
import com.empire.employeefinder.model.enums.Location;
import com.empire.employeefinder.model.enums.Status;
import com.empire.employeefinder.repository.EmployeeRepository;
import com.empire.employeefinder.repository.JobPositionRepository;
import com.empire.employeefinder.repository.JobTypeRepository;
import com.empire.employeefinder.service.EmployeeService;
import com.empire.employeefinder.service.ResumeService;
import com.empire.employeefinder.utils.AbstractSearchUtil;
import com.empire.employeefinder.utils.DtoUtil;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeServiceImpl extends AbstractSearchUtil<Employee> implements EmployeeService {

    private static final Map<String, Comparator<EmployeeResponseDto>> COMPARATORS = Map.ofEntries(
            Map.entry("id", Comparator.comparing(EmployeeResponseDto::getId)),
            Map.entry("name", Comparator.comparing(EmployeeResponseDto::getName, String.CASE_INSENSITIVE_ORDER)),
            Map.entry("jobPosition", Comparator.comparing(EmployeeResponseDto::getJobPosition, String.CASE_INSENSITIVE_ORDER)),
            Map.entry("gender", Comparator.comparing(e -> e.getGender().name())),
            Map.entry("location", Comparator.comparing(e -> e.getLocation().name())),
            Map.entry("jobType", Comparator.comparing(EmployeeResponseDto::getJobType, String.CASE_INSENSITIVE_ORDER)),
            Map.entry("experienceYears", Comparator.comparing(EmployeeResponseDto::getExperienceYears)),
            Map.entry("expectedSalary", Comparator.comparing(EmployeeResponseDto::getExpectedSalary)),
            Map.entry("availabilityDate", Comparator.comparing(EmployeeResponseDto::getAvailabilityDate)),
            Map.entry("educationLevel", Comparator.comparing(e -> e.getEducationLevel().name())),
            Map.entry("age", Comparator.comparing(EmployeeResponseDto::getAge)),
            Map.entry("status", Comparator.comparing(e -> e.getStatus().name()))
    );

    private static final String EMPLOYEE_NOT_FOUND = "Employee with id %s not found";
    private static final String JOB_TYPE_NOT_FOUND = "Job type with id %s not found";
    private static final String JOB_POSITION_NOT_FOUND = "Job position with id %s not found";

    private final EmployeeRepository employeeRepository;
    private final JobTypeRepository jobTypeRepository;
    private final JobPositionRepository jobPositionRepository;
    private final ResumeService resumeService;
    private final EmployeeMapper employeeMapper;
    private final ModelMapper mapper;

    @Override
    @Transactional
    public EmployeeResponseDto add(EmployeeRequestDto employeeRequestDto, Principal principal) throws IOException {
        if (employeeRequestDto.getResume() == null || employeeRequestDto.getResume().isEmpty()) {
            throw new ResumeFileNotFoundException();
        }

        String login = principal.getName();
        List<JobType> allJobTypes = jobTypeRepository.findAll();
        List<JobPosition> allJobPositions = jobPositionRepository.findAll();

        Employee employee = employeeMapper.toEntity(employeeRequestDto, allJobTypes, allJobPositions);
        employee.setStatus(Status.NEW);

        employeeRepository.save(employee);
        resumeService.uploadResumeAndAttach(employee, employeeRequestDto.getResume());

        log.info("New employee " + employee.getName() + " has been created by user: " + login);

        return employeeMapper.toResponse(employee);
    }

    @Override
    @Transactional
    public EmployeeResponseDto update(Long employeeId, EmployeeRequestDto employeeRequestDto, Principal principal) throws IOException {
        Employee employee = findEmployeeById(employeeId);
        String login = principal.getName();
        List<JobType> allJobTypes = jobTypeRepository.findAll();
        List<JobPosition> allJobPositions = jobPositionRepository.findAll();

        Employee updatedEmployee = employeeMapper.toEntity(employeeRequestDto, allJobTypes, allJobPositions);
        mapper.map(updatedEmployee, employee);

        employeeRepository.save(employee);

        if (employeeRequestDto.getResume() != null && !employeeRequestDto.getResume().isEmpty()) {
            resumeService.replaceResume(employee, employeeRequestDto.getResume());
        }

        log.info("Employee " + employee.getName() + " has been updated by user: " + login);

        return employeeMapper.toResponse(employee);
    }

    @Override
    @Transactional
    public void changeEmployeeStatus(Long employeeId, Status newStatus, String login) {
        Employee employee = findEmployeeById(employeeId);

        employee.setStatus(newStatus);
        employeeRepository.save(employee);
        log.info("Employee status has been changed to : " + newStatus + " by user: " + login);
    }

    @Override
    public EmployeeResponseDto getById(Long employeeId) {
        return employeeMapper.toResponse(findEmployeeById(employeeId));
    }

    @Override
    @Transactional
    public void delete(Long employeeId, Principal principal) {
        String login = principal.getName();

        resumeService.deleteResume(employeeId);
        employeeRepository.deleteById(employeeId);
        log.info("Employee has been removed by user: " + login);
    }

    @Override
    public List<EmployeeResponseDto> filterEmployees(EmployeeFilterRequestDto dto, String sortField, String sortDirection,
                                                     int pageSize, int pageNumber, String searchField, String parameter) {

        List<Employee> filteredEmployees = employeeRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (dto.getName() != null)
                predicates.add(cb.like(cb.lower(root.get("name")), dto.getName().toLowerCase() + "%"));
            if (dto.getGender() != null)
                predicates.add(cb.equal(root.get("gender"), dto.getGender()));
            if (dto.getLocation() != null)
                predicates.add(cb.equal(root.get("location"), dto.getLocation()));
            if (dto.getJobTypeId() != null)
                predicates.add(cb.equal(root.get("jobType").get("id"), dto.getJobTypeId()));
            if (dto.getJobPositionId() != null)
                predicates.add(cb.equal(root.get("jobPosition").get("id"), dto.getJobPositionId()));
            if (dto.getExperienceYears() != null)
                predicates.add(cb.equal(root.get("experienceYears"), dto.getExperienceYears()));
            if (dto.getExpectedSalary() != null)
                predicates.add(cb.equal(root.get("expectedSalary"), dto.getExpectedSalary()));
            if (dto.getAvailabilityDate() != null)
                predicates.add(cb.lessThanOrEqualTo(root.get("availabilityDate"), dto.getAvailabilityDate()));
            if (dto.getEducationLevel() != null)
                predicates.add(cb.equal(root.get("educationLevel"), dto.getEducationLevel()));
            if (dto.getAge() != null)
                predicates.add(cb.equal(root.get("age"), dto.getAge()));

            return cb.and(predicates.toArray(new Predicate[0]));
        });

        List<Employee> filteredEmployeesWithStatusNewAndSelected = filteredEmployees.stream()
                .filter(e -> e.getStatus() == Status.NEW || e.getStatus() == Status.SELECTED)
                .filter(e -> matchesSearch(e, searchField, parameter))
                .toList();

        return sortAndPaginate(filteredEmployeesWithStatusNewAndSelected, sortField, sortDirection, pageSize, pageNumber);
    }

    @Override
    public List<EmployeeResponseDto> filterByJobTypeIdAndPositionIdWithSearch(
            Long jobTypeId, Long jobPositionId, Gender gender, Location location,
            String searchField, String parameter,
            String sortField, String sortDirection,
            int pageSize, int pageNumber) {
        validateFieldAndParameter(searchField, parameter, List.of(Status.NEW.name(), Status.SELECTED.name()));

        JobType jobType = findJobTypeById(jobTypeId);
        JobPosition jobPosition = findJobPositionById(jobPositionId);

        Specification<Employee> spec = Specification.<Employee>where((root, query, cb) ->
                        root.get("status").in(List.of(Status.NEW, Status.SELECTED)))
                .and(jobType != null
                        ? (root, query, cb) -> cb.equal(root.get("jobType"), jobType)
                        : null)
                .and(jobPosition != null
                        ? (root, query, cb) -> cb.equal(root.get("jobPosition"), jobPosition)
                        : null)
                .and(gender != null
                        ? (root, query, cb) -> cb.equal(root.get("gender"), gender)
                        : null)
                .and(location != null
                        ? (root, query, cb) -> cb.equal(root.get("location"), location)
                        : null)
                .and(buildSearchSpecification(searchField, parameter));

        PageRequest pageRequest = createPageRequest(pageNumber, pageSize, sortField, sortDirection);

        return employeeRepository.findAll(spec, pageRequest).getContent().stream()
                .map(employeeMapper::toResponse)
                .toList();
    }

    @Override
    public List<EmployeeResponseDto> getAll(String searchField, String parameter, String status,
                                            String sortField, String sortDirection,
                                            int pageSize, int pageNumber) {
        validateFieldAndParameter(searchField, parameter);

        Specification<Employee> spec = buildSpecification(searchField, parameter, status);

        Page<Employee> page = employeeRepository.findAll(spec, createPageRequest(pageNumber, pageSize, sortField, sortDirection));
        return page.getContent().stream()
                .map(employeeMapper::toResponse)
                .toList();
    }

    @Override
    public List<EmployeeResponseDto> getAllForUser(String searchField, String parameter,
                                                   String sortField, String sortDirection,
                                                   int pageSize, int pageNumber) {
        validateFieldAndParameter(searchField, parameter, "");

        Specification<Employee> spec = buildSpecificationForUser(searchField, parameter);

        Page<Employee> page = employeeRepository.findAll(
                spec, createPageRequest(pageNumber, pageSize, sortField, sortDirection)
        );

        return page.getContent().stream()
                .map(employeeMapper::toResponse)
                .toList();
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
    protected Map<String, Function<String, Specification<Employee>>> getFieldSpecs() {
        return Map.ofEntries(
                Map.entry("id", val -> (r, q, cb) -> cb.like(r.get("id").as(String.class), val)),
                Map.entry("name", val -> (r, q, cb) -> cb.like(cb.lower(r.get("name")), val.toLowerCase() + "%")),
                Map.entry("jobPosition", val -> (r, q, cb) -> cb.like(cb.lower(r.get("jobPosition").get("name")), val.toLowerCase() + "%")),
                Map.entry("gender", val -> (r, q, cb) -> cb.like(cb.lower(r.get("gender").as(String.class)), val.toLowerCase() + "%")),
                Map.entry("location", val -> (r, q, cb) -> cb.like(cb.lower(r.get("location").as(String.class)), val.toLowerCase() + "%")),
                Map.entry("jobType", val -> (r, q, cb) -> cb.like(cb.lower(r.get("jobType").get("name")), val.toLowerCase() + "%")),
                Map.entry("experienceYears", val -> (r, q, cb) -> cb.like(r.get("experienceYears").as(String.class), val + "%")),
                Map.entry("expectedSalary", val -> (r, q, cb) -> cb.like(r.get("expectedSalary").as(String.class), val + "%")),
                Map.entry("availabilityDate", this::availabilityDateSpec),
                Map.entry("educationLevel", val -> (r, q, cb) -> cb.like(cb.lower(r.get("educationLevel").as(String.class)), val.toLowerCase() + "%")),
                Map.entry("age", val -> (r, q, cb) -> cb.like(r.get("age").as(String.class), val + "%")),
                Map.entry("status", val -> (r, q, cb) -> cb.like(cb.lower(r.get("status").as(String.class)), val.toLowerCase() + "%"))
        );
    }

    @Override
    protected void validateFieldAndParameter(String searchField, String parameter) {
        validateFieldAndParameter(searchField, parameter, List.of());
    }

    private Specification<Employee> buildSpecification(String searchField, String parameter, String status) {
        return Specification.where(buildStatusSpecification(status))
                .and(buildSearchSpecification(searchField, parameter));
    }

    private Specification<Employee> buildSpecificationForUser(String searchField, String parameter) {
        return Specification
                .where(buildStatusesSpecification(List.of("NEW", "SELECTED")))
                .and(buildSearchSpecification(searchField, parameter));
    }

    private Specification<Employee> buildStatusSpecification(String status) {
        return Optional.ofNullable(status)
                .filter(s -> !s.isBlank())
                .map(s -> (Specification<Employee>) (root, query, cb) ->
                        cb.equal(cb.lower(root.get("status")), s.toLowerCase()))
                .orElse((root, query, cb) -> cb.conjunction());
    }

    private Specification<Employee> buildStatusesSpecification(List<String> statuses) {
        return (root, query, cb) -> {
            CriteriaBuilder.In<String> inClause = cb.in(cb.lower(root.get("status")));
            for (String status : statuses) {
                inClause.value(status.toLowerCase());
            }
            return inClause;
        };
    }

    private boolean matchesSearch(Employee e, String field, String param) {
        if (param == null || param.isEmpty()) return true;

        String lowerParam = param.toLowerCase();

        return switch (field) {
            case "id" -> String.valueOf(e.getId()).startsWith(lowerParam);
            case "name" -> e.getName() != null && e.getName().toLowerCase().startsWith(lowerParam);
            case "jobType" -> e.getJobType() != null && e.getJobType().getName().toLowerCase().startsWith(lowerParam);
            case "jobPosition" -> e.getJobPosition() != null && e.getJobPosition().getName().toLowerCase().startsWith(lowerParam);
            case "gender" -> e.getGender() != null && e.getGender().name().toLowerCase().startsWith(lowerParam);
            case "location" -> e.getLocation() != null && e.getLocation().name().toLowerCase().startsWith(lowerParam);
            case "experienceYears" -> e.getExperienceYears() != null && String.valueOf(e.getExperienceYears()).startsWith(lowerParam);
            case "expectedSalary" -> e.getExpectedSalary() != null && String.valueOf(e.getExpectedSalary()).startsWith(lowerParam);
            case "availabilityDate" -> e.getAvailabilityDate() != null && e.getAvailabilityDate().toString().contains(lowerParam);
            case "educationLevel" -> e.getEducationLevel() != null && e.getEducationLevel().name().toLowerCase().startsWith(lowerParam);
            case "age" -> e.getAge() != null && String.valueOf(e.getAge()).startsWith(lowerParam);
            default -> true;
        };
    }

    private List<EmployeeResponseDto> sortAndPaginate(List<Employee> employees, String sortField, String sortDirection, int pageSize, int pageNumber) {
        List<EmployeeResponseDto> dtos = employeeMapper.toDtos(employees);
        return DtoUtil.paginate(DtoUtil.sort(dtos, sortField, sortDirection, COMPARATORS), pageSize, pageNumber);
    }

    private Employee findEmployeeById(Long employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(String.format(EMPLOYEE_NOT_FOUND, employeeId)));
    }

    private JobType findJobTypeById(Long jobTypeId) {
        return jobTypeRepository.findById(jobTypeId)
                .orElseThrow(() -> new JobTypeNotFoundException(String.format(JOB_TYPE_NOT_FOUND, jobTypeId)));
    }

    private JobPosition findJobPositionById(Long jobPositionId) {
        return jobPositionRepository.findById(jobPositionId)
                .orElseThrow(() -> new JobPositionNotFoundException(String.format(JOB_POSITION_NOT_FOUND, jobPositionId)));
    }

    private Specification<Employee> availabilityDateSpec(String val) {
        return (root, query, cb) -> {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDate date = LocalDate.parse(val, formatter);
                Expression<LocalDate> dbDate = cb.function("DATE", LocalDate.class, root.get("availabilityDate"));
                return cb.equal(dbDate, date);
            } catch (DateTimeParseException e) {
                return cb.disjunction();
            }
        };
    }

    protected void validateFieldAndParameter(String searchField, String parameter, String status) {
        List<String> statuses = (status == null || status.isBlank())
                ? List.of()
                : List.of(status.trim());
        validateFieldAndParameter(searchField, parameter, statuses);
    }

    private void validateFieldAndParameter(String searchField, String parameter, List<String> statuses) {
        String field = Optional.ofNullable(searchField).filter(f -> !f.isBlank()).orElse("default");

        if (!"default".equals(field) && !getFieldSpecs().containsKey(field)) {
            throw new IllegalArgumentException("Invalid search field: " + field);
        }

        if (!statuses.isEmpty() && "status".equals(field)) {
            throw new IllegalArgumentException("Filtering by 'status' is not allowed when status param is used");
        }

        if (parameter != null && !parameter.isEmpty()) {
            String normalPattern = "[a-zA-Z0-9.]+";
            String datePattern = "\\d{2}/\\d{2}/\\d{4}";

            if (Stream.of(normalPattern, datePattern).noneMatch(p -> Pattern.matches(p, parameter))) {
                throw new WrongSearchParameterException("Search should be in latin letters, figures or date in format dd/mm/yyyy");
            }
        }

        statuses.forEach(status -> {
            try {
                Status.valueOf(status);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status: " + status);
            }
        });
    }
}
