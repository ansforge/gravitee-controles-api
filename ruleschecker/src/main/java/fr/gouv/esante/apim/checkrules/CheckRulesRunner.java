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

    @Value("${env}")
    private String envId;

    @Value("${apikey}")
    private String apiKey;

    @Value("${recipients.filepath:local.file.path}")
    private String recipients;

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
        check();
        log.info("Finished checking rules");
    }

    private void check() {
        List<GraviteeApiDefinition> apis = loader.loadApiDefinitions();
        Map<String, ApiDefinitionCheckResult> checkResults = checkRulesForEachApis(apis);
        // Send notifications
        log.info("Sending results to {} : \n{}", recipients, checkResults);
    }

    private Map<String, ApiDefinitionCheckResult> checkRulesForEachApis(List<GraviteeApiDefinition> apis) {
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
