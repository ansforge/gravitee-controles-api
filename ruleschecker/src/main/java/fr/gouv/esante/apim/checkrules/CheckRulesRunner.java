/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules;

import fr.gouv.esante.apim.checkrules.model.ApiDefinitionCheckResult;
import fr.gouv.esante.apim.checkrules.model.GraviteeApiDefinition;
import fr.gouv.esante.apim.checkrules.services.ApiDefinitionLoader;
import fr.gouv.esante.apim.checkrules.services.ArgumentsChecker;
import fr.gouv.esante.apim.checkrules.services.RulesChecker;

import lombok.extern.slf4j.Slf4j;
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
    private final RulesChecker rulesChecker;
    private final ApiDefinitionLoader loader;


    public CheckRulesRunner(ArgumentsChecker argsParser, ApiDefinitionLoader loader, RulesChecker rulesChecker) {
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
        Map<String, ApiDefinitionCheckResult> checkResults = checkRulesForEachApi(apis);
        // Send notifications
        log.info("Generate report and send notifications :\n{}", checkResults);
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
