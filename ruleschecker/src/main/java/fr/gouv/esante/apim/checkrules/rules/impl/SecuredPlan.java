/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.rules.impl;

import fr.gouv.esante.apim.checkrules.model.definition.GraviteeApiDefinition;
import fr.gouv.esante.apim.checkrules.model.definition.Plan;
import fr.gouv.esante.apim.checkrules.model.results.RuleResult;
import fr.gouv.esante.apim.checkrules.services.MessageProvider;
import fr.gouv.esante.apim.checkrules.services.rulesvalidation.RulesRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Set;


@Component
@Slf4j
public class SecuredPlan extends AbstractRule {

    @Autowired
    public SecuredPlan(RulesRegistry registry, MessageProvider messageProvider) {
        super(registry, messageProvider);
        super.register(this);
    }

    @Override
    public String getName() {
        return messageProvider.getMessage("rule.securedplan.name");
    }

    @Override
    public RuleResult visit(GraviteeApiDefinition apiDefinition) {
        Set<Plan> plans = apiDefinition.getPlans();
        boolean success = verify(plans);
        logResults(apiDefinition.getApiName(), success);
        return new RuleResult(
                getName(),
                success,
                success ? messageProvider.getMessage("rule.securedplan.msg.success") : messageProvider.getMessage("rule.securedplan.msg.failure")
        );
    }

    private boolean verify(Set<Plan> plans) {
        boolean success = false;
        if (plans == null || plans.isEmpty()) {
            return false;
        }
        // On ne considère que les plans ayant un status STAGING ou PUBLISHED comme plans "actifs", ceux en status
        // CLOSED ou DEPRECATED ne peuvent pas satisfaire cette règle d'implémentation
        List<Plan> activePlans = plans.stream()
                .filter(plan -> Arrays.asList("STAGING", "PUBLISHED").contains(plan.getStatus()))
                .toList();
        for (Plan plan : activePlans) {
            if (Arrays.asList("API_KEY", "OAUTH2").contains(plan.getAuthMechanism())) {
                success = true;
                break;
            }
        }
        return success;
    }
}
