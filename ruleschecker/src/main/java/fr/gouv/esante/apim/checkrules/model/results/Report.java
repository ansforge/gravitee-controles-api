/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.model.results;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;


@Slf4j
@Getter
@Setter
public class Report {

    private Map<String, ApiDefinitionCheckResult> globalCheckResults;
    private boolean success;
    private String timestamp;
    private String environment;

    public Report(Map<String, ApiDefinitionCheckResult> globalCheckResults, String timestamp, String environment) {
        this.globalCheckResults = globalCheckResults;
        this.timestamp = timestamp;
        this.environment = environment;
        this.success = evaluateSuccess();
    }

    private boolean evaluateSuccess() {
        for (Map.Entry<String, ApiDefinitionCheckResult> entry : globalCheckResults.entrySet()) {
            ApiDefinitionCheckResult checkResult = entry.getValue();
            for(RuleResult ruleResult : checkResult.getRuleResults()) {
                if(!ruleResult.isSuccess()) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return super.toString();
        }
    }
}
