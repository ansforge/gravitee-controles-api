/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.services;

import fr.gouv.esante.apim.checkrules.model.ApiDefinitionCheckResult;
import fr.gouv.esante.apim.checkrules.model.GraviteeApiDefinition;
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
        ApiDefinitionCheckResult rulesResult = new ApiDefinitionCheckResult();
        rulesResult.setApiDefinitionName(apiDefinition.getApiName());
        rulesResult.setTimestamp(Instant.now().toString());
        for (ApiDefinitionQualityRule rule : loader.getRules()) {
            rulesResult.getRuleResults().add(apiDefinition.accept(rule));
        }
        return rulesResult;
    }
}
