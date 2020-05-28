package com.code4ro.legalconsultation.service.impl;

import com.code4ro.legalconsultation.common.exceptions.LegalValidationException;
import com.code4ro.legalconsultation.model.persistence.User;
import com.code4ro.legalconsultation.service.api.MailApi;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Profile("production")
@Slf4j
public class MailService implements MailApi {

    @Value("${app.signupurl}")
    private String signupUrl;

    @Value("${spring.mvc.locale}")
    private String configuredLocale;

    @Value("${app.email.sender}")
    private String from;

    private final JavaMailSender mailSender;
    private final I18nService i18nService;
    private final Configuration freemarkerConfig;

    @Autowired
    public MailService(final JavaMailSender mailSender,
                       final I18nService i18nService,
                       final Configuration freemarkerConfig) {
        this.mailSender = mailSender;
        this.i18nService = i18nService;
        this.freemarkerConfig = freemarkerConfig;
    }

    @Override
    public void sendRegisterMail(final List<User> users) throws LegalValidationException {
        final List<String> failedEmails = new ArrayList<>();
        users.forEach(user -> {
            final MimeMessage message = mailSender.createMimeMessage();
            final MimeMessageHelper helper = new MimeMessageHelper(message);
            try {
                helper.setFrom(from);
                helper.setTo(user.getEmail());
                final Template template = freemarkerConfig.getTemplate(getRegisterTemplate());
                final String content =
                        FreeMarkerTemplateUtils.processTemplateIntoString(template, getRegisterModel(user));
                helper.setText(content, true);
                helper.setSubject(i18nService.translate("register.User.confirmation.subject"));
                mailSender.send(message);
            } catch (final Exception e) {
                log.error("Problem preparing or sending email to user with address {}", user.getEmail(), e);
                failedEmails.add(user.getEmail());
            }
        });
        if (!failedEmails.isEmpty()) {
            throw LegalValidationException.builder()
                    .i18nKey("user.Email.send.failed")
                    .i8nArguments(failedEmails)
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .build();
        }
    }

    private String getRegisterTemplate() {
        return "register-email-" + configuredLocale + ".ftl";
    }

    private Map<String, String> getRegisterModel(final User user) {
        return Map.of(
                "username", getUserName(user),
                "signupurl", getSignupUrl(user)
        );
    }

    private String getSignupUrl(final User user) {
        return signupUrl + '/' + user.getEmail();
    }

    private String getUserName(final User user) {
        final String USERNAME_SEPARATOR = " ";
        return Stream.of(user.getFirstName(), user.getLastName())
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(USERNAME_SEPARATOR));
    }
}
