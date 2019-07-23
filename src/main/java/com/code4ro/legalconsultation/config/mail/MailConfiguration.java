package com.code4ro.legalconsultation.config.mail;

import com.code4ro.legalconsultation.service.api.MailApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("dev")
@Configuration
public class MailConfiguration {

    @Bean
    @Primary
    public MailApi mailApi() {
        return users -> {
            // ignore mail sending on development environment
        };
    }
}
