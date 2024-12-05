/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.rules.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.gouv.esante.apim.checkrules.model.definition.GraviteeApiDefinition;
import fr.gouv.esante.apim.checkrules.model.definition.Logging;
import fr.gouv.esante.apim.checkrules.model.results.RuleResult;
import fr.gouv.esante.apim.checkrules.services.MessageProvider;
import fr.gouv.esante.apim.checkrules.services.rulesvalidation.RulesRegistry;


@Component
public class LogsDisabled extends AbstractRule {

    @Autowired
    public LogsDisabled(RulesRegistry registry, MessageProvider messageProvider) {
        super(registry, messageProvider);
        super.register(this);
    }

    @Override
    public String getName() {
        return messageProvider.getMessage("rule.logging.name");
    }

    @Override
    public RuleResult visit(GraviteeApiDefinition apiDefinition) {
        Logging logging = apiDefinition.getLogging();
        boolean success = verify(logging);
        logResults(apiDefinition.getApiName(), success);
        return new RuleResult(
                getName(),
                success,
                success ? messageProvider.getMessage("rule.logging.msg.success") : messageProvider.getMessage("rule.logging.msg.failure")
        );
    }

    private boolean verify(Logging logging) {
        if (logging == null) {
            return true;
        }
        if (!"NONE".equals(logging.getContent())) {
            return false;
        }
        if (!"NONE".equals(logging.getMode())) {
            return false;
        }
        return "NONE".equals(logging.getScope());
    }
}
