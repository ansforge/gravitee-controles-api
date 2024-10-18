/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.config;

import fr.gouv.esante.apim.client.ApiClient;
import fr.gouv.esante.apim.client.api.ApisApi;
import fr.gouv.esante.apim.client.api.ConfigurationApi;
import fr.gouv.esante.apim.client.api.GatewayApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;


@Configuration
public class AppConfig {

    @Value("${apim.management.url}")
    private String apimUrl;

    /**
     * Token d'accès à l'API de gestion de Gravitee
     * Dépend de l'environnement ciblé
     */
    @Value("${apikey}")
    private String apiKey;

    @Value("${spring.mail.host}")
    private String mailHost;

    @Value("${spring.mail.port}")
    private int mailPort;


    @Bean
    public ApiClient apiClient() {
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(apimUrl);
        apiClient.setBearerToken(apiKey);
        return apiClient;
    }

    @Bean
    public ApisApi apisApi() {
        return new ApisApi(apiClient());
    }

    @Bean
    public ConfigurationApi configurationApi() {
        return new ConfigurationApi(apiClient());
    }

    @Bean
    public GatewayApi gatewayApi() {
        return new GatewayApi(apiClient());
    }

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailHost);
        mailSender.setPort(mailPort);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.debug", "true");

        return mailSender;
    }


}
