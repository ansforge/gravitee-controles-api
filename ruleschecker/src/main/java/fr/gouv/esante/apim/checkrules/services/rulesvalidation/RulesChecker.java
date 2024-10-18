/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.services.rulesvalidation;

import fr.gouv.esante.apim.checkrules.model.results.ApiDefinitionCheckResult;
import fr.gouv.esante.apim.checkrules.model.definition.GraviteeApiDefinition;
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

    private final RulesLoader loader;


    public RulesChecker(RulesLoader loader) {
        this.loader = loader;
    }

    public ApiDefinitionCheckResult checkRules(GraviteeApiDefinition apiDefinition) {
        log.info("Checking rules for {}", apiDefinition.getApiName());
        ApiDefinitionCheckResult apiChecksResult = new ApiDefinitionCheckResult();
        apiChecksResult.setApiDefinitionName(apiDefinition.getApiName());
        apiChecksResult.setTimestamp(Instant.now().toString());

        for (ApiDefinitionQualityRule rule : loader.getRules()) {
            apiChecksResult.getRuleResults().add(apiDefinition.accept(rule));
        }
        log.info("Result for {} : {}", apiDefinition.getApiName(), isSuccess(apiChecksResult.getRuleResults()));

        return apiChecksResult;
    }

    private boolean isSuccess(List<RuleResult> apiRuleResults) {
        return apiRuleResults.stream().allMatch(RuleResult::isSuccess);
    }
}
