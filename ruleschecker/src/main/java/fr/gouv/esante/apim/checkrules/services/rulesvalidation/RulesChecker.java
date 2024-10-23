/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.services.rulesvalidation;

import fr.gouv.esante.apim.checkrules.model.definition.GraviteeApiDefinition;
import fr.gouv.esante.apim.checkrules.model.results.ApiDefinitionCheckResult;
import fr.gouv.esante.apim.checkrules.model.results.RuleResult;
import fr.gouv.esante.apim.checkrules.rules.ApiDefinitionQualityRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * Service déclenchant toutes les vérifications de règles sur une API
 * et compilant les résultats de ces contrôles
 */
@Service
@Slf4j
public class RulesChecker {

    private final RulesRegistry registry;


    public RulesChecker(RulesRegistry registry) {
        this.registry = registry;
    }

    public ApiDefinitionCheckResult checkRules(GraviteeApiDefinition apiDefinition) {
        log.info("Checking rules for {}", apiDefinition.getApiName());
        // Initialise l'objet contenant les résultats des vérifications pour une API
        ApiDefinitionCheckResult apiChecksResult = new ApiDefinitionCheckResult();
        apiChecksResult.setApiDefinitionName(apiDefinition.getApiName());
        apiChecksResult.setTimestamp(Instant.now().toString());

        // Exécute les vérifications des règles et enregistre les résultats
        for (ApiDefinitionQualityRule rule : registry.getRules()) {
            apiChecksResult.getRuleResults().add(apiDefinition.accept(rule));
        }
        log.info("Result for {} : {}", apiDefinition.getApiName(), isSuccess(apiChecksResult.getRuleResults()));

        return apiChecksResult;
    }

    private boolean isSuccess(List<RuleResult> apiRuleResults) {
        if (apiRuleResults == null || apiRuleResults.isEmpty()) {
            return false;
        }
        return apiRuleResults.stream().allMatch(RuleResult::isSuccess);
    }
}
