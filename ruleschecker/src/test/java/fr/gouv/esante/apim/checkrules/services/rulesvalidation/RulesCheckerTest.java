/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.services.rulesvalidation;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import fr.gouv.esante.apim.checkrules.model.definition.Configuration;
import fr.gouv.esante.apim.checkrules.model.definition.Entrypoint;
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
import fr.gouv.esante.apim.checkrules.model.results.RuleResult;
import fr.gouv.esante.apim.checkrules.rules.ApiDefinitionQualityRule;
import fr.gouv.esante.apim.checkrules.rules.impl.GroupAssignment;
import fr.gouv.esante.apim.checkrules.rules.impl.LogsDisabled;
import fr.gouv.esante.apim.checkrules.rules.impl.SecuredPlan;
import fr.gouv.esante.apim.checkrules.rules.impl.SubdomainConfiguration;

import fr.gouv.esante.apim.checkrules.services.notification.EmailNotifier;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


@WireMockTest
@SpringBootTest(classes= {RulesChecker.class, RulesLoader.class,
                        GroupAssignment.class,
                        HealthCheckService.class,
                        HealthCheckRequest.class,
                        LogsDisabled.class,
                        SecuredPlan.class,
                        SubdomainConfiguration.class
})
@ActiveProfiles({ "test" })
@Slf4j
class RulesCheckerTest {

    private static final String PACKAGE_NAME = "fr.gouv.esante.apim.checkrules.rules.impl";


    @Test
    void checkRulesTest() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        // Construction de l'API definition d'entrée
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        Set<String> groups = new HashSet<>();
        groups.add("group1");
        groups.add("group2");

        List<ShardingTag> shardingTags = new ArrayList<>();
        ShardingTag shardingTag = new ShardingTag();
        shardingTag.setName("test-tag");
        shardingTag.setHostname("/testHost");
        shardingTag.setRestrictedGroups(List.of("group1", "group2"));
        shardingTags.add(shardingTag);

        Plan accessPlan = new Plan();
        accessPlan.setAuthMechanism("OAUTH2");
        accessPlan.setName("access-plan");
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

        Entrypoint entrypoint = new Entrypoint();
        entrypoint.setHost("localhost");
        entrypoint.setTags(Set.of("test-tag"));
        entrypoint.setTarget("/testPath");
        VirtualHost virtualHost = new VirtualHost();
        virtualHost.setHost("/testHost");
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
        apiDef.setGroups(groups);
        apiDef.setPlans(Set.of(accessPlan, healthcheckPlan));
        apiDef.setTags(Set.of("test-tag"));
        apiDef.setShardingTags(shardingTags);
        apiDef.setEntrypoints(List.of(entrypoint));
        apiDef.setVirtualHosts(List.of(virtualHost));
        apiDef.setHealthCheck(healthCheck);
        apiDef.setLogging(logging);

        // Construction du résultat attendu
        String now = "2024-10-16T13:39:34.347787200Z";
        // Construction des résultats de contrôles de 3 règles quelconques, dont 1 en échec
        RuleResult rule1Result = new RuleResult("rule1", true, "Success message 1", now);
        RuleResult rule2Result = new RuleResult("rule2", false, "Failure message 2", now);
        RuleResult rule3Result = new RuleResult("rule3", true, "Success message 3", now);

        ApiDefinitionCheckResult apiResult = new ApiDefinitionCheckResult();
        apiResult.setTimestamp(now);
        apiResult.setApiDefinitionName("API under test");
        apiResult.setRuleResults(List.of(rule1Result, rule2Result, rule3Result));

        Map<String, ApiDefinitionCheckResult> apiResultsMap = new HashMap<>();
        apiResultsMap.put(apiResult.getApiDefinitionName(), apiResult);

        // Test
        final RulesLoader[] loader = new RulesLoader[1];
        Assertions.assertDoesNotThrow(() -> {
            loader[0] = new RulesLoader(new EmailNotifier());
        });
        Set<ApiDefinitionQualityRule> rules = new HashSet<>();
        List<String> rulesNames = List.of(
                "GroupAssignment",
                "HealthcheckActivation",
                "HealthcheckSecured",
                "LogsDisabled",
                "SecuredPlan",
                "SubdomainConfiguration"
        );
        for(String ruleName : rulesNames) {
            ApiDefinitionQualityRule apiDefinitionQualityRule = (ApiDefinitionQualityRule) Class
                    .forName(PACKAGE_NAME + "." + ruleName)
                    .getDeclaredConstructor().newInstance();
            rules.add(apiDefinitionQualityRule);
        }
        loader[0].setRules(rules);
        RulesChecker checker = new RulesChecker(loader[0]);
        ApiDefinitionCheckResult checkResult = checker.checkRules(apiDef);
        log.info("checkResult :\n{}", checkResult.toString());
    }
}