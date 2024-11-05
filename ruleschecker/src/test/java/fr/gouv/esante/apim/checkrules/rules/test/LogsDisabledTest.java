/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.rules.test;

import fr.gouv.esante.apim.checkrules.config.AppTestConfig;
import fr.gouv.esante.apim.checkrules.model.definition.GraviteeApiDefinition;
import fr.gouv.esante.apim.checkrules.model.definition.Logging;
import fr.gouv.esante.apim.checkrules.model.results.RuleResult;
import fr.gouv.esante.apim.checkrules.rules.impl.LogsDisabled;
import fr.gouv.esante.apim.checkrules.services.MessageProvider;
import fr.gouv.esante.apim.checkrules.services.rulesvalidation.ApiDefinitionMapper;
import fr.gouv.esante.apim.checkrules.services.rulesvalidation.RulesRegistry;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(
        classes = {
            AppTestConfig.class,
            ApiDefinitionMapper.class,
            RulesRegistry.class,
            MessageProvider.class
        })
@ActiveProfiles({"test"})
@Slf4j
class LogsDisabledTest extends LogsDisabled {

    @Autowired
    public LogsDisabledTest(RulesRegistry registry, MessageProvider messageProvider) {
        super(registry, messageProvider);
    }

    @Test
    void testLogsAreDisabled() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        Logging logging = new Logging();
        logging.setContent("NONE");
        logging.setMode("NONE");
        logging.setScope("NONE");
        apiDef.setLogging(logging);
        LogsDisabled logsDisabled = new LogsDisabled(new RulesRegistry(), messageProvider);
        RuleResult result = apiDef.accept(logsDisabled);

        assertEquals(super.getName(), result.getRuleName());
        assertTrue(result.isSuccess());
        assertEquals(messageProvider.getMessage("rule.logging.msg.success"), result.getMessage());
    }

    @Test
    void testLoggingConfigDoesntExists() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        LogsDisabled logsDisabled = new LogsDisabled(new RulesRegistry(), messageProvider);
        RuleResult result = apiDef.accept(logsDisabled);

        assertFalse(result.isSuccess());
        assertEquals(messageProvider.getMessage("rule.logging.msg.failure"), result.getMessage());
    }

    @ParameterizedTest
    @CsvSource({
            "HEADERS, NONE, NONE",
            "NONE, CLIENT, NONE",
            "NONE, NONE, REQUEST_RESPONSE",
    })
    void testLogsAreEnabled(String content, String mode, String scope) {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        Logging logging = new Logging();
        logging.setContent(content);
        logging.setMode(mode);
        logging.setScope(scope);
        apiDef.setLogging(logging);
        LogsDisabled logsDisabled = new LogsDisabled(new RulesRegistry(), messageProvider);
        RuleResult result = apiDef.accept(logsDisabled);

        assertFalse(result.isSuccess());
        assertEquals(messageProvider.getMessage("rule.logging.msg.failure"), result.getMessage());
    }
}