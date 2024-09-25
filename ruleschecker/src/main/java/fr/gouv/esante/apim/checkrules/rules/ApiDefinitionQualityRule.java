package fr.gouv.esante.apim.checkrules.rules;

import fr.gouv.esante.apim.checkrules.model.GraviteeApiDefinition;
import fr.gouv.esante.apim.checkrules.model.RuleResult;


/**
 * Règle d'implémentation à contrôler
 */
public interface ApiDefinitionQualityRule {

    String getName();

    RuleResult visit(GraviteeApiDefinition apiDefinition);
}
