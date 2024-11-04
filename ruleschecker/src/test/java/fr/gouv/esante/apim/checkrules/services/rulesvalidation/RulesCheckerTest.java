/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.services.rulesvalidation;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import fr.gouv.esante.apim.checkrules.config.AppTestConfig;
import fr.gouv.esante.apim.checkrules.model.definition.Configuration;
import fr.gouv.esante.apim.checkrules.model.definition.Filter;
import fr.gouv.esante.apim.checkrules.model.definition.Flow;
import fr.gouv.esante.apim.checkrules.model.definition.GraviteeApiDefinition;
import fr.gouv.esante.apim.checkrules.model.definition.HealthCheckRequest;
import fr.gouv.esante.apim.checkrules.model.definition.HealthCheckService;
import fr.gouv.esante.apim.checkrules.model.definition.Logging;
import fr.gouv.esante.apim.checkrules.model.definition.Plan;
import fr.gouv.esante.apim.checkrules.model.definition.ShardingTag;
import fr.gouv.esante.apim.checkrules.model.definition.Step;
import fr.gouv.esante.apim.checkrules.model.definition.VirtualHost;
import fr.gouv.esante.apim.checkrules.model.results.ApiDefinitionCheckResult;
import fr.gouv.esante.apim.checkrules.rules.impl.GroupAssignment;
import fr.gouv.esante.apim.checkrules.rules.impl.HealthcheckActivation;
import fr.gouv.esante.apim.checkrules.rules.impl.HealthcheckSecured;
import fr.gouv.esante.apim.checkrules.rules.impl.LogsDisabled;
import fr.gouv.esante.apim.checkrules.rules.impl.SecuredPlan;
import fr.gouv.esante.apim.checkrules.rules.impl.SubdomainConfiguration;
import fr.gouv.esante.apim.checkrules.services.MessageProvider;
import fr.gouv.esante.apim.checkrules.services.notification.EmailNotifier;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@WireMockTest
@SpringBootTest(
        classes = {
            AppTestConfig.class,
            RulesChecker.class,
            RulesRegistry.class,
            EmailNotifier.class,
            MessageProvider.class,
            GroupAssignment.class,
            HealthcheckSecured.class,
            HealthcheckActivation.class,
            LogsDisabled.class,
            SecuredPlan.class,
            SubdomainConfiguration.class
})
@ActiveProfiles({"test"})
@Slf4j
class RulesCheckerTest {

    @Autowired
    private RulesRegistry registry;


    @Test
    void checkRulesTest() {
        // Construction de l'API definition d'entrée
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        Set<String> groups = new HashSet<>();
        groups.add("group1");
        groups.add("group2");

        List<ShardingTag> shardingTags = new ArrayList<>();
        ShardingTag shardingTag = new ShardingTag("test-tag");
        shardingTag.setEntrypointMappings(List.of("api.gateway.com", "api.gateway.net"));
        shardingTags.add(shardingTag);

        Plan accessPlan = new Plan();
        accessPlan.setAuthMechanism("OAUTH2");
        accessPlan.setName("access-plan");
        accessPlan.setStatus("PUBLISHED");
        Flow emptyFlow = new Flow();
        emptyFlow.setPreSteps(new ArrayList<>());
        emptyFlow.setPostSteps(new ArrayList<>());
        accessPlan.setFlows(List.of(emptyFlow));

        Plan healthcheckPlan = new Plan();
        healthcheckPlan.setAuthMechanism("KEY_LESS");
        healthcheckPlan.setName("plan-HealthCheck");

        List<Flow> flows = new ArrayList<>();
        Flow flow = new Flow();
        List<Step> preSteps = new ArrayList<>();
        Step preStep = new Step();
        preStep.setPolicy("resource-filtering");

        Configuration config = new Configuration();
        Filter whitelistItem = new Filter();
        whitelistItem.setMethods(List.of("GET"));
        whitelistItem.setPattern("/healthcheck");
        config.setWhitelist(List.of(whitelistItem));
        config.setBlacklist(new ArrayList<>());
        preStep.setConfiguration(config);

        preSteps.add(preStep);
        flow.setPreSteps(preSteps);
        flow.setPostSteps(new ArrayList<>());
        flows.add(flow);
        healthcheckPlan.setFlows(flows);

        VirtualHost virtualHost = new VirtualHost();
        virtualHost.setHost("api.gateway.net");
        virtualHost.setOverrideEntrypoint(true);
        virtualHost.setPath("/testPath");

        HealthCheckService healthCheck = new HealthCheckService();
        healthCheck.setEnabled(true);
        HealthCheckRequest healthCheckPath = new HealthCheckRequest();
        healthCheckPath.setMethod("GET");
        healthCheckPath.setPath("/actuator/healthcheck");
        healthCheck.setPaths(List.of(healthCheckPath));

        Logging logging = new Logging();
        logging.setContent("NONE");
        logging.setMode("NONE");
        logging.setScope("NONE");

        apiDef.setApiName("TestAPI-ex");
        apiDef.setAdminGroups(groups);
        apiDef.setPlans(Set.of(accessPlan, healthcheckPlan));
        apiDef.setShardingTags(shardingTags);
        apiDef.setVirtualHosts(List.of(virtualHost));
        apiDef.setHealthCheck(healthCheck);
        apiDef.setLogging(logging);

        // Test
        RulesChecker checker = new RulesChecker(registry);
        ApiDefinitionCheckResult checkResult = checker.checkRules(apiDef);
        log.info("checkResult :\n{}", checkResult.toString());

        Assertions.assertEquals(6, checkResult.getRuleResults().size());
        Assertions.assertEquals("TestAPI-ex", checkResult.getApiDefinitionName());

        Assertions.assertTrue(checkResult.getRuleResults().get(0).isSuccess());
        Assertions.assertTrue(checkResult.getRuleResults().get(1).isSuccess());
        Assertions.assertTrue(checkResult.getRuleResults().get(2).isSuccess());
        Assertions.assertTrue(checkResult.getRuleResults().get(3).isSuccess());
        Assertions.assertTrue(checkResult.getRuleResults().get(4).isSuccess());
        Assertions.assertTrue(checkResult.getRuleResults().get(5).isSuccess());

    }
}