package com.empire.employeefinder.service.impl;

import com.empire.employeefinder.dto.request.CompanyRequestDto;
import com.empire.employeefinder.dto.request.UserLoginRequestDto;
import com.empire.employeefinder.dto.request.UserRegisterRequestDto;
import com.empire.employeefinder.dto.request.UserUpdateRequestDto;
import com.empire.employeefinder.dto.response.UserLoginResponseDto;
import com.empire.employeefinder.dto.response.UserRegisterResponseDto;
import com.empire.employeefinder.dto.response.UserResponseDto;
import com.empire.employeefinder.exception.*;
import com.empire.employeefinder.kafka.model.RegisterMessage;
import com.empire.employeefinder.kafka.service.KafkaProducerService;
import com.empire.employeefinder.mapper.CompanyMapper;
import com.empire.employeefinder.mapper.UserMapper;
import com.empire.employeefinder.model.Company;
import com.empire.employeefinder.model.User;
import com.empire.employeefinder.model.enums.Role;
import com.empire.employeefinder.repository.CompanyRepository;
import com.empire.employeefinder.repository.UserRepository;
import com.empire.employeefinder.security.jwt.JwtTokenProvider;
import com.empire.employeefinder.service.EmailService;
import com.empire.employeefinder.service.UserService;
import com.empire.employeefinder.utils.AbstractSearchUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl extends AbstractSearchUtil<User> implements UserService {

    private static final String USER_IS_PRESENT = "User with login %s is already present";
    private static final String USER_NOT_FOUND = "User with %s %s not found";
    private static final String USER_HAS_ANOTHER_PASSWORD = "User with login %s has another password. " +
            "Go to register or enter valid credentials";
    private static final String SUCCESSFUL_REGISTRATION = "User %s has been successfully registered";
    private static final String INCORRECT_PASSWORD = "The current password is entered incorrectly";
    private static final String PASSWORDS_DO_NOT_MATCH = "Passwords don't match";
    private static final String SUCCESSFUL_CHANGE_PASSWORD = "Password has been changed successfully";

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final UserMapper userMapper;
    private final CompanyMapper companyMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final KafkaProducerService kafkaProducerService;
    private final EmailService emailService;

    @Value("${topic.registration}")
    private String topicRegistration;

    @Override
    @Transactional
    public UserRegisterResponseDto registration(UserRegisterRequestDto userRegisterRequestDto) {
        validateUserBeforeSave(userRegisterRequestDto);

        String password = passwordEncoder.encode(userRegisterRequestDto.getPassword());
        User user = userMapper.toUser(userRegisterRequestDto, password, Role.ROLE_USER);
        CompanyRequestDto companyDto = userRegisterRequestDto.getCompany();

        Company company = findOrCreateCompany(companyDto);

        company.addUser(user);
        userRepository.save(user);

        log.info("New user : {}", user);

        sendRegisterMessage(user);

        UserRegisterResponseDto newUser = userMapper.toUserRegisterResponseDto(user, String.format(SUCCESSFUL_REGISTRATION, user.getName()));

        CompletableFuture.runAsync(() -> emailService.sendRegistrationDetails(newUser));

        return newUser;
    }

    @Override
    @Transactional
    public UserLoginResponseDto authentication(UserLoginRequestDto userLoginRequestDto) {
        User user = findByLoginAndPassword(userLoginRequestDto.getLogin(), userLoginRequestDto.getPassword());
        String token = jwtTokenProvider.createToken(user.getEmail(), user.getRole(), String.valueOf(user.getId()));

        return userMapper.toUserLoginResponseDto(user, token);
    }

    @Override
    public UserResponseDto getById(Long userId) {
        return userMapper.toUserResponseDto(findById(userId));
    }

    @Override
    public List<UserResponseDto> getAll(String searchField, String parameter,
                                        String sortField, String sortDirection,
                                        int pageSize, int pageNumber, String login) {
        validateFieldAndParameter(searchField, parameter);

        Specification<User> spec = buildSearchSpecification(searchField, parameter);

        Page<User> page = userRepository.findAll(
                spec,
                createPageRequest(pageNumber, pageSize, sortField, sortDirection)
        );

        return userMapper.toDtos(page.getContent());
    }

    @Override
    protected Map<String, Function<String, Specification<User>>> getFieldSpecs() {
        return Map.ofEntries(
                Map.entry("id", val -> (r, q, cb) -> cb.like(r.get("id").as(String.class), val)),
                Map.entry("name", val -> (r, q, cb) -> cb.like(cb.lower(r.get("name")), val.toLowerCase() + "%")),
                Map.entry("email", val -> (r, q, cb) -> cb.like(cb.lower(r.get("email")), val.toLowerCase())),
                Map.entry("companyName", val -> (r, q, cb) -> cb.like(cb.lower(r.get("company").get("companyName")), val.toLowerCase() + "%")),
                Map.entry("regNumber", val -> (r, q, cb) -> cb.like(cb.lower(r.get("company").get("regNumber")), val.toLowerCase() + "%")),
                Map.entry("jobTitle", val -> (r, q, cb) -> cb.like(cb.lower(r.get("jobTitle").as(String.class)), val.toLowerCase() + "%")),
                Map.entry("role", val -> (r, q, cb) -> cb.like(cb.lower(r.get("role").as(String.class)), val.toLowerCase() + "%"))
        );
    }

    @Override
    protected String mapSortField(String field) {
        return switch (field) {
            case "companyName" -> "company.companyName";
            case "regNumber" -> "company.regNumber";
            default -> field;
        };
    }

    @Override
    protected void validateFieldAndParameter(String searchField, String parameter) {
        if (parameter != null && !parameter.isEmpty() && !parameter.matches("[a-zA-Z0-9.@]+")) {
            throw new WrongSearchParameterException("Search should contain only latin letters, digits, dot or @");
        }
    }

    @Override
    @Transactional
    public UserResponseDto changeUserRole(Long userId, Role newRole) {
        User currentUser = getCurrentUser();

        User user = findById(userId);

        if (currentUser.getId().equals(userId)) {
            throw new SelfRoleModificationException();
        }

        user.setRole(newRole);
        userRepository.save(user);

        return userMapper.toUserResponseDto(user);
    }

    @Override
    @Transactional
    public void deleteById(Long userId) {
        User currentUser = getCurrentUser();

        if (currentUser.getId().equals(userId)) {
            throw new SelfDeletionException();
        }

        User user = findById(userId);

        if (user.getRole() == Role.ROLE_SUPERADMIN) {
            throw new ForbiddenSuperadminDeleteException();
        }

        userRepository.deleteById(userId);
    }

    @Override
    @Transactional
    public UserRegisterResponseDto changePassword(UserUpdateRequestDto userDto) {
        User user = findByLogin(userDto.getLogin());

        if (!passwordEncoder.matches(userDto.getCurrentPassword(), user.getPassword())) {
            throw new WrongPasswordException(INCORRECT_PASSWORD);
        }

        if (!userDto.getNewPassword().equals(userDto.getConfirmPassword())) {
            throw new PasswordMismatchException(PASSWORDS_DO_NOT_MATCH);
        }

        user.setPassword(passwordEncoder.encode(userDto.getNewPassword()));
        userRepository.save(user);

        return userMapper.toUserRegisterResponseDto(user, SUCCESSFUL_CHANGE_PASSWORD);
    }

    private User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format(USER_NOT_FOUND, "id", userId)));
    }

    private User findByLoginAndPassword(String login, String password) {
        User user = findByLogin(login);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UserNotFoundException(String.format(USER_HAS_ANOTHER_PASSWORD, login));
        }

        return user;
    }

    private User findByLogin(String login) {
        return userRepository.findByEmail(login)
                .orElseThrow(() -> new UserNotFoundException(String.format(USER_NOT_FOUND, "login", login)));
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        return findByLogin(currentUsername);
    }

    private Company findOrCreateCompany(CompanyRequestDto companyDto) {
        return companyRepository.findByRegNumber(companyDto.getRegNumber())
                .map(existingCompany -> {
                    if (!existingCompany.getCompanyName().equals(companyDto.getCompanyName())) {
                        throw new WrongCompanyRequisits("The registration number is already used by another company.");
                    }
                    return existingCompany;
                })
                .or(() -> companyRepository.findByCompanyName(companyDto.getCompanyName())
                        .map(existingCompany -> {
                            if (!existingCompany.getRegNumber().equals(companyDto.getRegNumber())) {
                                throw new WrongCompanyRequisits("The company name is already used with a different registration number.");
                            }
                            return existingCompany;
                        }))
                .orElseGet(() -> companyRepository.save(companyMapper.toCompany(companyDto)));
    }

    private void validateUserBeforeSave(UserRegisterRequestDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new UserIsPresentException(String.format(USER_IS_PRESENT, userDto.getEmail()));
        }
        if (!userDto.getPassword().equals(userDto.getConfirmPassword())) {
            throw new PasswordMismatchException(PASSWORDS_DO_NOT_MATCH);
        }
    }

    private void sendRegisterMessage(User user) {
        kafkaProducerService.sendMessage(RegisterMessage.builder()
                .password(user.getPassword())
                .username(user.getName()), topicRegistration);
        log.info("New user registered successfully");
    }
}
