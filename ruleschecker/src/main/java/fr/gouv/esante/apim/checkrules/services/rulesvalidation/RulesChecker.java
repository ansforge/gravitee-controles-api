/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.services.rulesvalidation;

import fr.gouv.esante.apim.checkrules.model.results.ApiDefinitionCheckResult;
import fr.gouv.esante.apim.checkrules.model.definition.GraviteeApiDefinition;
import fr.gouv.esante.apim.checkrules.rules.ApiDefinitionQualityRule;

import org.springframework.stereotype.Service;
import java.time.Instant;

/**
 * Service déclenchant toutes les vérifications de règles sur une API
 * et compilant les résultats de ces contrôles
 */
@Service
public class RulesChecker {

    private final RulesLoader loader;


    public RulesChecker(RulesLoader loader) {
        this.loader = loader;
    }

    public ApiDefinitionCheckResult checkRules(GraviteeApiDefinition apiDefinition) {
        ApiDefinitionCheckResult apiChecksResult = new ApiDefinitionCheckResult();
        apiChecksResult.setApiDefinitionName(apiDefinition.getApiName());
        apiChecksResult.setTimestamp(Instant.now().toString());
        for (ApiDefinitionQualityRule rule : loader.getRules()) {
            apiChecksResult.getRuleResults().add(apiDefinition.accept(rule));
        }
        return apiChecksResult;
    }
}
