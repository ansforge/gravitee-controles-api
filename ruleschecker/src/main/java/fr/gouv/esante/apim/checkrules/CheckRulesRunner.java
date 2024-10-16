/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules;

import fr.gouv.esante.apim.checkrules.model.results.ApiDefinitionCheckResult;
import fr.gouv.esante.apim.checkrules.model.definition.GraviteeApiDefinition;
import fr.gouv.esante.apim.checkrules.model.results.Report;
import fr.gouv.esante.apim.checkrules.services.rulesvalidation.ApiDefinitionLoader;
import fr.gouv.esante.apim.checkrules.services.rulesvalidation.ArgumentsChecker;
import fr.gouv.esante.apim.checkrules.services.rulesvalidation.RulesChecker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Point d'entrée de l'application
 */
@Component
@Slf4j
public class CheckRulesRunner implements ApplicationRunner {

    private final ArgumentsChecker argsParser;
    private final ApiDefinitionLoader loader;
    private final RulesChecker rulesChecker;

    @Value("${envid}")
    private String envId;


    public CheckRulesRunner(ArgumentsChecker argsParser,
                            ApiDefinitionLoader loader,
                            RulesChecker rulesChecker) {
        this.argsParser = argsParser;
        this.loader = loader;
        this.rulesChecker = rulesChecker;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("Start checking rules with args : {}", Arrays.toString(args.getSourceArgs()));
        argsParser.verifyArgs(args);
        reportCheckResults();
        log.info("Finished checking rules");
    }

    private void reportCheckResults() {
        List<GraviteeApiDefinition> apis = loader.loadApiDefinitions();
        Map<String, ApiDefinitionCheckResult> globalCheckResults = checkRulesForEachApi(apis);
        log.info("Global check results: {}", globalCheckResults);
    }

    private Map<String, ApiDefinitionCheckResult> checkRulesForEachApi(List<GraviteeApiDefinition> apis) {
        Map<String, ApiDefinitionCheckResult> checkResults = new HashMap<>();
        for (GraviteeApiDefinition apiDefinition : apis) {
            checkResults.put(
                    apiDefinition.getApiName(),
                    rulesChecker.checkRules(apiDefinition)
            );
        }
        return checkResults;
    }
}
