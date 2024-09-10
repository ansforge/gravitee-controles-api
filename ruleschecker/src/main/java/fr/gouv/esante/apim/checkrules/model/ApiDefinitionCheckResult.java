package fr.gouv.esante.apim.checkrules.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ApiDefinitionCheckResult {

    private List<RuleResult> ruleResults;

    private Date timestamp;

    private String apiDefinitionName;

}
