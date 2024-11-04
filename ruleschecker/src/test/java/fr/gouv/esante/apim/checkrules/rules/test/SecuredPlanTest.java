/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.rules.test;

import fr.gouv.esante.apim.checkrules.config.AppTestConfig;
import fr.gouv.esante.apim.checkrules.model.definition.GraviteeApiDefinition;
import fr.gouv.esante.apim.checkrules.model.definition.Plan;
import fr.gouv.esante.apim.checkrules.model.results.RuleResult;
import fr.gouv.esante.apim.checkrules.rules.impl.SecuredPlan;
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

import java.util.HashSet;
import java.util.Set;

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
class SecuredPlanTest extends SecuredPlan {

    @Autowired
    public SecuredPlanTest(RulesRegistry registry, MessageProvider messageProvider) {
        super(registry, messageProvider);
    }

    @ParameterizedTest
    @CsvSource({
            "API_KEY, STAGING",
            "OAUTH2, PUBLISHED",
            "API_KEY, PUBLISHED",
            "OAUTH2, STAGING",
    })
    void testSecuredPlanExists(String authMechanism, String status) {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        Set<Plan> plans = new HashSet<>();
        Plan plan = new Plan();
        plan.setAuthMechanism(authMechanism);
        plan.setStatus(status);
        plans.add(plan);
        apiDef.setPlans(plans);
        SecuredPlan securedPlanRule = new SecuredPlan(new RulesRegistry(), messageProvider);
        RuleResult result = apiDef.accept(securedPlanRule);

        assertEquals(super.getName(), result.getRuleName());
        assertTrue(result.isSuccess());
        assertEquals(messageProvider.getMessage("rule.securedplan.msg.success"), result.getMessage());
    }

    @Test
    void testNoPlanExists() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        SecuredPlan securedPlanRule = new SecuredPlan(new RulesRegistry(), messageProvider);
        RuleResult result = apiDef.accept(securedPlanRule);

        assertFalse(result.isSuccess());
        assertEquals(messageProvider.getMessage("rule.securedplan.msg.failure"), result.getMessage());
    }

    @ParameterizedTest
    @CsvSource({
            "KEY_LESS, CLOSED",
            "JWT, DEPRECATED",
            "KEY_LESS, DEPRECATED",
            "JWT, CLOSED",
            "KEY_LESS, STAGING",
            "JWT, PUBLISHED",
            "KEY_LESS, PUBLISHED",
            "JWT, STAGING",
            "API_KEY, CLOSED",
            "OAUTH2, DEPRECATED",
            "API_KEY, DEPRECATED",
            "OAUTH2, CLOSED",
            "SUBSCRIPTION, PUBLISHED",
            "SUBSCRIPTION, CLOSED",
            "'', PUBLISHED",
            "'', CLOSED",
    })
    void testPlansAreNotSecured(String authMechanism, String status) {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        Set<Plan> plans = new HashSet<>();
        Plan plan = new Plan();
        plan.setAuthMechanism(authMechanism);
        plan.setStatus(status);
        plans.add(plan);
        apiDef.setPlans(plans);
        SecuredPlan securedPlanRule = new SecuredPlan(new RulesRegistry(), messageProvider);
        RuleResult result = apiDef.accept(securedPlanRule);

        assertFalse(result.isSuccess());
    }

    @Test
    void testMultipleSecuredPlanExists() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        Set<Plan> plans = new HashSet<>();
        Plan noSecurityPlan = new Plan();
        plans.add(noSecurityPlan);
        Plan apiKeyPlan = new Plan();
        apiKeyPlan.setAuthMechanism("API_KEY");
        apiKeyPlan.setStatus("STAGING");
        plans.add(apiKeyPlan);
        Plan oAuth2Plan = new Plan();
        oAuth2Plan.setAuthMechanism("OAUTH2");
        oAuth2Plan.setStatus("PUBLISHED");
        plans.add(oAuth2Plan);
        Plan keyLessPlan = new Plan();
        keyLessPlan.setAuthMechanism("KEY_LESS");
        plans.add(keyLessPlan);
        apiDef.setPlans(plans);
        SecuredPlan securedPlanRule = new SecuredPlan(new RulesRegistry(), messageProvider);
        RuleResult result = apiDef.accept(securedPlanRule);

        assertTrue(result.isSuccess());
    }


}