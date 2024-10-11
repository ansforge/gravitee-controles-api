/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.rules.impl;

import fr.gouv.esante.apim.checkrules.model.GraviteeApiDefinition;
import fr.gouv.esante.apim.checkrules.model.Logging;
import fr.gouv.esante.apim.checkrules.model.RuleResult;
import fr.gouv.esante.apim.checkrules.rules.ApiDefinitionQualityRule;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class LogsDisabled implements ApiDefinitionQualityRule {

    protected static final String FAILURE_MSG = "Aucun healthcheck n'est actif sur cette API";
    protected static final String SUCCESS_MSG = "Healthcheck correctement activé sur cette API";


    @Override
    public String getName() {
        return "6.3 - Les logs détaillés des appels doivent être désactivés";
    }

    @Override
    public RuleResult visit(GraviteeApiDefinition apiDefinition) {
        log.info("LogsDisabled visit");
        Logging logging = apiDefinition.getLogging();
        boolean success = verify(logging);
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
        if (!"NONE".equals(logging.getScope())) {
            return false;
        }
        return true;
    }
}
