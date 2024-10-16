/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.rules.impl;

import fr.gouv.esante.apim.checkrules.model.definition.Configuration;
import fr.gouv.esante.apim.checkrules.model.definition.Filter;
import fr.gouv.esante.apim.checkrules.model.definition.Flow;
import fr.gouv.esante.apim.checkrules.model.definition.GraviteeApiDefinition;
import fr.gouv.esante.apim.checkrules.model.definition.Plan;
import fr.gouv.esante.apim.checkrules.model.results.RuleResult;
import fr.gouv.esante.apim.checkrules.model.definition.Step;
import fr.gouv.esante.apim.checkrules.services.rulesvalidation.ApiDefinitionMapper;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(classes = {HealthcheckSecuredTest.class, ApiDefinitionMapper.class})
@ActiveProfiles({ "test" })
@Slf4j
class HealthcheckSecuredTest extends HealthcheckSecured {

    @Test
    void testHealthCheckPlanIsSecured() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        Set<Plan> plans = new HashSet<>();
        Plan plan = new Plan();
        plan.setName("testPlan-HealthCheck");
        plan.setAuthMechanism("KEY_LESS");

        Flow flow = new Flow();
        Step pre = new Step();
        pre.setPolicy("resource-filtering");

        Filter filter = new Filter();
        filter.setMethods(List.of("GET"));
        List<Filter> whitelist = List.of(filter);

        Configuration configuration = new Configuration();
        configuration.setWhitelist(whitelist);
        configuration.setBlacklist(new ArrayList<>());

        pre.setConfiguration(configuration);
        flow.setPreSteps(Collections.singletonList(pre));
        plan.setFlows(Collections.singletonList(flow));
        plans.add(plan);
        apiDef.setPlans(plans);

        HealthcheckSecured healthcheckSecured = new HealthcheckSecured();
        RuleResult result = apiDef.accept(healthcheckSecured);

        assertEquals(super.getName(), result.getRuleName());
        assertTrue(result.isSuccess());
        assertEquals(SUCCESS_MSG, result.getMessage());
    }

    @Test
    void testNoPlanExists() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        HealthcheckSecured healthcheckSecured = new HealthcheckSecured();
        RuleResult result = apiDef.accept(healthcheckSecured);

        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG, result.getMessage());
    }

    @Test
    void testPlansIsEmpty() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        Set<Plan> plans = new HashSet<>();
        apiDef.setPlans(plans);
        HealthcheckSecured healthcheckSecured = new HealthcheckSecured();
        RuleResult result = apiDef.accept(healthcheckSecured);

        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG, result.getMessage());
    }

    @Test
    void testHealthCheckPlanDoesntExists() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        Set<Plan> plans = new HashSet<>();
        Plan plan = new Plan();
        plan.setName("testPlan-NotHealthCheck");
        plan.setAuthMechanism("KEY_LESS");
        plans.add(plan);
        apiDef.setPlans(plans);
        HealthcheckSecured healthcheckSecured = new HealthcheckSecured();
        RuleResult result = apiDef.accept(healthcheckSecured);

        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG, result.getMessage());
    }

    @Test
    void testHealthCheckPlanIsNotSecured() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        Set<Plan> plans = new HashSet<>();
        Plan plan = new Plan();
        plan.setName("testPlan-HealthCheck");
        plans.add(plan);
        apiDef.setPlans(plans);
        HealthcheckSecured healthcheckSecured = new HealthcheckSecured();
        RuleResult result = apiDef.accept(healthcheckSecured);

        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG, result.getMessage());
    }

}