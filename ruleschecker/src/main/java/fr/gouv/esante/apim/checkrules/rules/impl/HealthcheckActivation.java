/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.rules.impl;

import fr.gouv.esante.apim.checkrules.model.definition.GraviteeApiDefinition;
import fr.gouv.esante.apim.checkrules.model.definition.HealthCheckService;
import fr.gouv.esante.apim.checkrules.model.results.RuleResult;
import fr.gouv.esante.apim.checkrules.services.rulesvalidation.RulesRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class HealthcheckActivation extends AbstractRule {

    protected static final String FAILURE_MSG = "Aucun healthcheck n'est actif sur cette API";
    protected static final String SUCCESS_MSG = "Healthcheck correctement activé sur cette API";


    @Autowired
    public HealthcheckActivation(RulesRegistry registry) {
        super(registry);
        super.register(this);
    }

    @Override
    public String getName() {
        return "6.1 - Un HealthCheck doit être configuré pour vérifier la disponibilité du Backend de l’API";
    }

    @Override
    public RuleResult visit(GraviteeApiDefinition apiDefinition) {
        log.info("HealthcheckActivation visit");
        HealthCheckService healthCheck = apiDefinition.getHealthCheck();
        boolean success = verify(healthCheck);
        return new RuleResult(
                getName(),
                success,
                success ? SUCCESS_MSG : FAILURE_MSG
        );
    }

    private boolean verify(HealthCheckService healthCheck) {
        if (healthCheck == null) {
            return false;
        }
        return Boolean.TRUE.equals(healthCheck.isEnabled());
    }
}
