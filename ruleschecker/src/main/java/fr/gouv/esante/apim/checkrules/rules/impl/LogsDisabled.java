/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.rules.impl;

import fr.gouv.esante.apim.checkrules.model.GraviteeApiDefinition;
import fr.gouv.esante.apim.checkrules.model.RuleResult;
import fr.gouv.esante.apim.checkrules.rules.ApiDefinitionQualityRule;
import fr.gouv.esante.apim.client.model.LoggingGravitee;
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
        LoggingGravitee logging = apiDefinition.getLogging();
        boolean success = verify(logging);
        return new RuleResult(
                getName(),
                success,
                success ? SUCCESS_MSG : FAILURE_MSG
        );
    }

    private boolean verify(LoggingGravitee logging) {
        if (logging == null) {
            return false;
        }
        if (logging.getContent() != LoggingGravitee.ContentEnum.NONE) {
            return false;
        }
        if (logging.getMode() != LoggingGravitee.ModeEnum.NONE) {
            return false;
        }
        if (logging.getScope() != LoggingGravitee.ScopeEnum.NONE) {
            return false;
        }
        return true;
    }
}
