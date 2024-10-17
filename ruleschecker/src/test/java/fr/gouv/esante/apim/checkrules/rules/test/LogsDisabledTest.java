/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.rules.test;

import fr.gouv.esante.apim.checkrules.model.definition.GraviteeApiDefinition;
import fr.gouv.esante.apim.checkrules.model.definition.Logging;
import fr.gouv.esante.apim.checkrules.model.results.RuleResult;
import fr.gouv.esante.apim.checkrules.rules.impl.LogsDisabled;
import fr.gouv.esante.apim.checkrules.services.rulesvalidation.ApiDefinitionMapper;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(classes = {LogsDisabledTest.class, ApiDefinitionMapper.class})
@ActiveProfiles({ "test" })
@Slf4j
class LogsDisabledTest extends LogsDisabled {

    @Test
    void testLogsAreDisabled() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        Logging logging = new Logging();
        logging.setContent("NONE");
        logging.setMode("NONE");
        logging.setScope("NONE");
        apiDef.setLogging(logging);
        LogsDisabled logsDisabled = new LogsDisabled();
        RuleResult result = apiDef.accept(logsDisabled);

        assertEquals(super.getName(), result.getRuleName());
        assertTrue(result.isSuccess());
        assertEquals(SUCCESS_MSG, result.getMessage());
    }

    @Test
    void testLoggingConfigDoesntExists() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        LogsDisabled logsDisabled = new LogsDisabled();
        RuleResult result = apiDef.accept(logsDisabled);

        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG, result.getMessage());
    }

    @Test
    void testLogsContentIsNotNone() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        Logging logging = new Logging();
        logging.setContent("HEADERS");
        logging.setMode("NONE");
        logging.setScope("NONE");
        apiDef.setLogging(logging);
        LogsDisabled logsDisabled = new LogsDisabled();
        RuleResult result = apiDef.accept(logsDisabled);

        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG, result.getMessage());
    }

    @Test
    void testLogsModeIsNotNone() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        Logging logging = new Logging();
        logging.setContent("NONE");
        logging.setMode("CLIENT");
        logging.setScope("NONE");
        apiDef.setLogging(logging);
        LogsDisabled logsDisabled = new LogsDisabled();
        RuleResult result = apiDef.accept(logsDisabled);

        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG, result.getMessage());
    }

    @Test
    void testLogsScopeIsNotNone() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        Logging logging = new Logging();
        logging.setContent("NONE");
        logging.setMode("NONE");
        logging.setScope("REQUEST_RESPONSE");
        apiDef.setLogging(logging);
        LogsDisabled logsDisabled = new LogsDisabled();
        RuleResult result = apiDef.accept(logsDisabled);

        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG, result.getMessage());
    }
}