package fr.gouv.esante.apim.checkrules.model;

import fr.gouv.esante.apim.checkrules.rules.ApiDefinitionQualityRule;

public interface ApiDefinition {

    RuleResult accept(ApiDefinitionQualityRule rule);
}
