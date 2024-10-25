/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.rules.impl;

import fr.gouv.esante.apim.checkrules.model.definition.GraviteeApiDefinition;
import fr.gouv.esante.apim.checkrules.model.definition.Logging;
import fr.gouv.esante.apim.checkrules.model.results.RuleResult;
import fr.gouv.esante.apim.checkrules.services.rulesvalidation.RulesRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class LogsDisabled extends AbstractRule {

    protected static final String FAILURE_MSG = "Le logging n'est pas désactivé sur cette API";
    protected static final String SUCCESS_MSG = "Le logging est correctement désactivé sur cette API";


    @Autowired
    public LogsDisabled(RulesRegistry registry) {
        super(registry);
        super.register(this);
    }

    @Override
    public String getName() {
        return "6.3 - Les logs détaillés des appels doivent être désactivés";
    }

    @Override
    public RuleResult visit(GraviteeApiDefinition apiDefinition) {
        Logging logging = apiDefinition.getLogging();
        boolean success = verify(logging);
        logResults(apiDefinition.getApiName(), success);
        return new RuleResult(
                getName(),
                success,
                success ? SUCCESS_MSG : FAILURE_MSG
        );
    }

    private boolean verify(Logging logging) {
        if (logging == null) {
            return false;
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
