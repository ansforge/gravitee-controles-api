package fr.gouv.esante.apim.checkrules.rules;

import fr.gouv.esante.apim.checkrules.model.ApiDefinition;
import fr.gouv.esante.apim.checkrules.model.RuleResult;

public interface ApiDefinitionQualityRule {

    RuleResult visit(ApiDefinition apiDefinition);
}
