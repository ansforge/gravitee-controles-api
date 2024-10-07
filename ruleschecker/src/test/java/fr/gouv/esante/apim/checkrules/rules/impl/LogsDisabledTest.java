package fr.gouv.esante.apim.checkrules.rules.impl;

import fr.gouv.esante.apim.checkrules.model.GraviteeApiDefinition;
import fr.gouv.esante.apim.checkrules.model.RuleResult;
import fr.gouv.esante.apim.checkrules.services.ApiDefinitionMapper;
import fr.gouv.esante.apim.client.model.LoggingGravitee;

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
        LoggingGravitee logging = new LoggingGravitee();
        logging.setContent(LoggingGravitee.ContentEnum.NONE);
        logging.setMode(LoggingGravitee.ModeEnum.NONE);
        logging.setScope(LoggingGravitee.ScopeEnum.NONE);
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
        LoggingGravitee logging = new LoggingGravitee();
        logging.setContent(LoggingGravitee.ContentEnum.HEADERS);
        logging.setMode(LoggingGravitee.ModeEnum.NONE);
        logging.setScope(LoggingGravitee.ScopeEnum.NONE);
        apiDef.setLogging(logging);
        LogsDisabled logsDisabled = new LogsDisabled();
        RuleResult result = apiDef.accept(logsDisabled);

        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG, result.getMessage());
    }

    @Test
    void testLogsModeIsNotNone() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        LoggingGravitee logging = new LoggingGravitee();
        logging.setContent(LoggingGravitee.ContentEnum.NONE);
        logging.setMode(LoggingGravitee.ModeEnum.CLIENT);
        logging.setScope(LoggingGravitee.ScopeEnum.NONE);
        apiDef.setLogging(logging);
        LogsDisabled logsDisabled = new LogsDisabled();
        RuleResult result = apiDef.accept(logsDisabled);

        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG, result.getMessage());
    }

    @Test
    void testLogsScopeIsNotNone() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        LoggingGravitee logging = new LoggingGravitee();
        logging.setContent(LoggingGravitee.ContentEnum.NONE);
        logging.setMode(LoggingGravitee.ModeEnum.NONE);
        logging.setScope(LoggingGravitee.ScopeEnum.REQUEST_RESPONSE);
        apiDef.setLogging(logging);
        LogsDisabled logsDisabled = new LogsDisabled();
        RuleResult result = apiDef.accept(logsDisabled);

        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG, result.getMessage());
    }
}