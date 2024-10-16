/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.rules;

import fr.gouv.esante.apim.checkrules.model.definition.GraviteeApiDefinition;
import fr.gouv.esante.apim.checkrules.model.results.RuleResult;


/**
 * Règle d'implémentation à contrôler
 */
public interface ApiDefinitionQualityRule {

    String getName();

    RuleResult visit(GraviteeApiDefinition apiDefinition);
}
