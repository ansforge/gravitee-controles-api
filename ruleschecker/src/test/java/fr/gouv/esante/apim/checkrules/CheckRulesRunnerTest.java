package fr.gouv.esante.apim.checkrules;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import fr.gouv.esante.apim.client.ApiClient;
import fr.gouv.esante.apim.client.api.ApisApi;
import fr.gouv.esante.apim.client.model.ApisResponseGravitee;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@WireMockTest
@SpringBootTest(classes=CheckRulesRunnerTest.class)
@ActiveProfiles({ "test" })
@Slf4j
class CheckRulesRunnerTest {

    private ApiClient client;

    @BeforeEach
    void setUp() {
        log.info("Running test");
    }

    @Test
    void testAppelClient(WireMockRuntimeInfo wmri) {
        log.info("HTTP port : {}", wmri.getHttpPort());

        log.info(WireMock.listAllStubMappings().toString());
        client = new ApiClient();
        client.setBasePath("http://localhost:" + wmri.getHttpPort() + "/");
        ApisApi apisApi = new ApisApi(client);
        try {
            ApisResponseGravitee response = apisApi.listApis("env", 1, 100, Collections.emptyList());
        } catch (HttpClientErrorException ex) {
            HttpStatusCode status = ex.getStatusCode();
            assertEquals(HttpStatus.BAD_REQUEST, status);
        }

    }

}