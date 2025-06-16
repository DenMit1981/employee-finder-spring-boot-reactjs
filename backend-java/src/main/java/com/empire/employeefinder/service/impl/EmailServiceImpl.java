package com.empire.employeefinder.service.impl;

import com.empire.employeefinder.dto.response.SelectionResponseDto;
import com.empire.employeefinder.dto.response.UserRegisterResponseDto;
import com.empire.employeefinder.model.User;
import com.empire.employeefinder.model.enums.Role;
import com.empire.employeefinder.repository.UserRepository;
import com.empire.employeefinder.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final UserRepository userRepository;
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${mail.username}")
    private String from;

    @Value("${custom.resume-url}")
    private String resumeUrl;

    @Override
    public void sendRegistrationDetails(UserRegisterResponseDto userDto) {
        sendTemplateEmailToAll("registerDetails", "New User Registration", "user", userDto);
    }

    @Override
    public void sendSelectionDetails(SelectionResponseDto selectionDto) {
        sendTemplateEmailToAll("selectionDetails", "Final Selection Details", "selection", selectionDto);
    }

    private void sendTemplateEmailToAll(String templateName, String subject, String variableName, Object dto) {
        List<String> recipients = userRepository.findByRoleIn(List.of(Role.ROLE_ADMIN, Role.ROLE_SUPERADMIN))
                .stream()
                .map(User::getEmail)
                .toList();

        Context context = new Context();
        context.setVariable(variableName, dto);
        context.setVariable("resumeUrl", resumeUrl);

        String body = templateEngine.process(templateName, context);

        for (String recipient : recipients) {
            sendEmail(recipient, subject, body);
        }
    }

    private void sendEmail(String to, String subject, String htmlBody) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText("Please view this message in an HTML-compatible email client.", htmlBody); // plain + html
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
