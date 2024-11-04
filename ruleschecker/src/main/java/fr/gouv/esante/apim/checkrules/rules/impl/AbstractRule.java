/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.rules.impl;

import fr.gouv.esante.apim.checkrules.rules.ApiDefinitionQualityRule;
import fr.gouv.esante.apim.checkrules.services.MessageProvider;
import fr.gouv.esante.apim.checkrules.services.rulesvalidation.RulesRegistry;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public abstract class AbstractRule implements ApiDefinitionQualityRule {

    private final RulesRegistry registry;

    protected final MessageProvider messageProvider;

    protected AbstractRule(RulesRegistry registry, MessageProvider messageProvider) {
        this.registry = registry;
        this.messageProvider = messageProvider;
    }

    protected void register(ApiDefinitionQualityRule rule) {
        registry.getRules().add(rule);
        log.info("Api definition quality rule: {} registered.", rule.getName());
    }

    protected void logResults(String apiName, boolean result) {
        log.info("le contrôle de la règle {} pour l'API {} est en {}",
                getName(),
                apiName,
                result ? "succès" : "échec");
    }


}
