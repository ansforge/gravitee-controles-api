package fr.gouv.esante.apim.checkrules.model;

import fr.gouv.esante.apim.checkrules.rules.ApiDefinitionQualityRule;
import fr.gouv.esante.apim.client.model.ApiGravitee;
import fr.gouv.esante.apim.client.model.PlanGravitee;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class GraviteeApiDefinition implements ApiDefinition {

    private ApiGravitee apiDefinition;

    private List<PlanGravitee> plans = new ArrayList<>();

    public GraviteeApiDefinition(ApiGravitee apiDefinition, List<PlanGravitee> plans) {
        this.apiDefinition = apiDefinition;
        this.plans.addAll(plans);
    }

    @Override
    public RuleResult accept(ApiDefinitionQualityRule rule) {
        return rule.visit(this);

    }
}
