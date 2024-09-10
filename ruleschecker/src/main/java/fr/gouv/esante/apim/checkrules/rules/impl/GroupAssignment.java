package fr.gouv.esante.apim.checkrules.rules.impl;

import fr.gouv.esante.apim.checkrules.model.ApiDefinition;
import fr.gouv.esante.apim.checkrules.model.RuleResult;
import fr.gouv.esante.apim.checkrules.rules.ApiDefinitionQualityRule;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GroupAssignment implements ApiDefinitionQualityRule {

    @Override
    public RuleResult visit(ApiDefinition apiDefinition) {
        log.info("GroupAssignment visit");
        // règle a implémenter
        RuleResult result = new RuleResult();
        return result;
    }
}
