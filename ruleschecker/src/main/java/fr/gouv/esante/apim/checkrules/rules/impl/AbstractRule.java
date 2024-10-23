/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.rules.impl;

import fr.gouv.esante.apim.checkrules.rules.ApiDefinitionQualityRule;
import fr.gouv.esante.apim.checkrules.services.rulesvalidation.RulesRegistry;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public abstract class AbstractRule implements ApiDefinitionQualityRule {

    private final RulesRegistry registry;


    public AbstractRule(RulesRegistry registry) {
        log.info("Setting registry to {}", registry);
        this.registry = registry;
    }

    protected void register(ApiDefinitionQualityRule rule) {
        log.info("Registering api definition quality rule: {}", rule);
        registry.getRules().add(rule);
    }
}
