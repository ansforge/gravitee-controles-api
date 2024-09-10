package fr.gouv.esante.apim.checkrules.services;

import fr.gouv.esante.apim.checkrules.model.ApiDefinition;
import fr.gouv.esante.apim.checkrules.model.ApiDefinitionCheckResult;
import fr.gouv.esante.apim.checkrules.rules.ApiDefinitionQualityRule;
import org.springframework.stereotype.Service;


@Service
public class RulesChecker {

    private RulesLoader loader;

    public ApiDefinitionCheckResult checkRules(ApiDefinition apiDefinition) {
        ApiDefinitionCheckResult rulesResult = new ApiDefinitionCheckResult();
        for (ApiDefinitionQualityRule rule : loader.getRules()) {
            rulesResult.getRuleResults().add(apiDefinition.accept(rule));
        }
        return rulesResult;
    }
}
