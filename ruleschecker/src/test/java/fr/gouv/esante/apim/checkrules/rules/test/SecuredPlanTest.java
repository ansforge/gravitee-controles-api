/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.rules.test;

import fr.gouv.esante.apim.checkrules.model.definition.GraviteeApiDefinition;
import fr.gouv.esante.apim.checkrules.model.definition.Plan;
import fr.gouv.esante.apim.checkrules.model.results.RuleResult;
import fr.gouv.esante.apim.checkrules.rules.impl.SecuredPlan;
import fr.gouv.esante.apim.checkrules.services.rulesvalidation.ApiDefinitionMapper;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(classes = {SecuredPlanTest.class, ApiDefinitionMapper.class})
@ActiveProfiles({ "test" })
@Slf4j
class SecuredPlanTest extends SecuredPlan {

    @Test
    void testSecuredPlanExists() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        Set<Plan> plans = new HashSet<>();
        Plan plan = new Plan();
        plan.setAuthMechanism("API_KEY");
        plans.add(plan);
        apiDef.setPlans(plans);
        SecuredPlan securedPlanRule = new SecuredPlan();
        RuleResult result = apiDef.accept(securedPlanRule);

        assertEquals(super.getName(), result.getRuleName());
        assertTrue(result.isSuccess());
        assertEquals(SUCCESS_MSG, result.getMessage());
    }

    @Test
    void testNoPlanExists() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        SecuredPlan securedPlanRule = new SecuredPlan();
        RuleResult result = apiDef.accept(securedPlanRule);

        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG, result.getMessage());
    }

    @Test
    void testSecuredPlanDoesNotExist() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        Set<Plan> plans = new HashSet<>();
        Plan keyLessPlan = new Plan();
        keyLessPlan.setAuthMechanism("KEY_LESS");
        plans.add(keyLessPlan);
        Plan jwtPlan = new Plan();
        jwtPlan.setAuthMechanism("JWT");
        plans.add(jwtPlan);
        Plan subscriptionPlan = new Plan();
        subscriptionPlan.setAuthMechanism("SUBSCRIPTION");
        plans.add(subscriptionPlan);
        Plan noSecurityPlan = new Plan();
        plans.add(noSecurityPlan);
        apiDef.setPlans(plans);
        SecuredPlan securedPlanRule = new SecuredPlan();
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
        plans.add(apiKeyPlan);
        Plan oAuth2Plan = new Plan();
        oAuth2Plan.setAuthMechanism("OAUTH2");
        plans.add(oAuth2Plan);
        Plan keyLessPlan = new Plan();
        keyLessPlan.setAuthMechanism("KEY_LESS");
        plans.add(keyLessPlan);
        apiDef.setPlans(plans);
        SecuredPlan securedPlanRule = new SecuredPlan();
        RuleResult result = apiDef.accept(securedPlanRule);

        assertTrue(result.isSuccess());
    }








}