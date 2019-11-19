package com.code4ro.legalconsultation.common.controller;

import com.code4ro.legalconsultation.model.dto.SignUpRequest;
import com.code4ro.legalconsultation.service.impl.ApplicationUserService;
import com.code4ro.legalconsultation.util.RandomObjectFiller;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public abstract class AbstractControllerIntegrationTest {

    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected ApplicationUserService applicationUserService;

    @MockBean
    protected JavaMailSender mailSender;


    protected static String endpoint(Object ...args) {
        final List<String> stringArgs = Arrays.stream(args)
                .filter(Objects::nonNull)
                .map(Object::toString)
                .collect(Collectors.toList());
        return StringUtils.join(stringArgs, '/');
    }

    protected void persistMockedUser() {
        final SignUpRequest signUpRequest = RandomObjectFiller.createAndFill(SignUpRequest.class);
        signUpRequest.setUsername("user");
        signUpRequest.setPassword("password");
        applicationUserService.save(signUpRequest);
    }
}
