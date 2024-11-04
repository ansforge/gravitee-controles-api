/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.integration;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import fr.gouv.esante.apim.checkrules.services.MessageProvider;
import fr.gouv.esante.apim.checkrules.services.rulesvalidation.CheckRulesService;
import fr.gouv.esante.apim.client.ApiClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@SpringBootTest
@Slf4j
public abstract class AbstractIntegrationTest {

    @RegisterExtension
    static WireMockExtension httpMockServer = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort().usingFilesUnderClasspath(".")).build();

    @Autowired
    protected CheckRulesService checkRulesService;

    @Autowired
    protected MessageProvider messageProvider;

    @Autowired
    private ApiClient client;


    @BeforeEach
    public void init() {
        log.info("Init Client");
        client.setBasePath(AbstractIntegrationTest.httpMockServer.baseUrl() + "/");
        client.setBearerToken("apiKey");
        log.info("ApiClient base url: {}", AbstractIntegrationTest.httpMockServer.baseUrl());
    }
}
