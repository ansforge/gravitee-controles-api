/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import fr.gouv.esante.apim.client.ApiClient;
import fr.gouv.esante.apim.client.api.ApisApi;
import fr.gouv.esante.apim.client.model.ApiListItemGravitee;
import fr.gouv.esante.apim.client.model.ExecutionModeGravitee;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@WireMockTest
@SpringBootTest(classes = CheckRulesRunnerTest.class)
@ActiveProfiles({"test"})
@Slf4j
class CheckRulesRunnerTest {

    private ApiClient client;

    @BeforeEach
    void setUp() {
        log.info("Running test");
    }

    @Test
    void testAppelClient_400(WireMockRuntimeInfo wmri) {
        log.info("HTTP port : {}", wmri.getHttpPort());

        log.info(WireMock.listAllStubMappings().toString());
        client = new ApiClient();
        client.setBasePath("http://localhost:" + wmri.getHttpPort() + "/");
        ApisApi apisApi = new ApisApi(client);
        try {
            List<ApiListItemGravitee> response = apisApi.getApis(
                    "env400",
                    "org",
                    "",
                    "",
                    false,
                    "",
                    "",
                    "",
                    "",
                    "",
                    ExecutionModeGravitee.V3,
                    "",
                    "",
                    false,
                    "",
                    Collections.emptyList()
            );
        } catch (HttpClientErrorException ex) {
            HttpStatusCode status = ex.getStatusCode();
            assertEquals(HttpStatus.BAD_REQUEST, status);
        }
    }

    @Test
    void testGetApisList(WireMockRuntimeInfo wmri) {
        log.info(WireMock.listAllStubMappings().toString());
        client = new ApiClient();
        client.setBasePath("http://localhost:" + wmri.getHttpPort() + "/");

        ApisApi apisApi = new ApisApi(client);
        List<ApiListItemGravitee> response = apisApi.getApis(
                "env200",
                "org",
                "",
                "",
                false,
                "",
                "",
                "",
                "",
                "",
                ExecutionModeGravitee.V3,
                "",
                "",
                false,
                "",
                Collections.emptyList()
        );
        assertEquals(6, response.size());
        assertEquals("81291589-44e6-39af-925a-a66130af09f7", response.get(0).getId());
    }
}