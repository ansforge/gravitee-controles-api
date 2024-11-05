/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.config;

import fr.gouv.esante.apim.client.ApiClient;
import fr.gouv.esante.apim.client.api.ApisApi;
import fr.gouv.esante.apim.client.api.ConfigurationApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;


@TestConfiguration
@Slf4j
public class AppTestConfig {

    @Bean
    public ApiClient apiClient() {
        log.info("ApiClient instantiated in Test Configuration");
        return new ApiClient();
    }

    @Bean
    public ApisApi apisApi() {
        log.info("ApisApi instantiated with url : {}", apiClient().getBasePath());
        return new ApisApi(apiClient());
    }

    @Bean
    public ConfigurationApi configurationApi() {
        return new ConfigurationApi(apiClient());
    }

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.debug", "true");

        return mailSender;
    }

    @Bean
    public MessageSource messageSource() {
        log.info("Inside MessageSource in AppConfig");
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        return messageSource;
    }

}
