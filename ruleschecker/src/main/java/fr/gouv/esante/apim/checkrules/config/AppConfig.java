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


@Configuration
public class AppConfig {

    @Value("${apim.management.url}")
    private String apimUrl;

    @Bean
    public ApiClient apiClient() {
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(apimUrl);
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

}
