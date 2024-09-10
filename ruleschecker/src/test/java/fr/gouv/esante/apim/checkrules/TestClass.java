package fr.gouv.esante.apim.checkrules;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@WireMockTest
@Slf4j
public class TestClass {

    @Test
    void testAppelClient(WireMockRuntimeInfo wmri) {
        log.info("HTTP port : {}", wmri.getHttpPort());
    }
}
