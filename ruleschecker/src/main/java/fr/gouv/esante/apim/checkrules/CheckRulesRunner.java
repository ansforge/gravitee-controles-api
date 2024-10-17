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

import fr.gouv.esante.apim.checkrules.services.notification.EmailNotifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;
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
    private final EmailNotifier emailNotifier;

    @Value("${envid}")
    private String envId;


    public CheckRulesRunner(ArgumentsChecker argsParser,
                            ApiDefinitionLoader loader,
                            RulesChecker rulesChecker, EmailNotifier emailNotifier) {
        this.argsParser = argsParser;
        this.loader = loader;
        this.rulesChecker = rulesChecker;
        this.emailNotifier = emailNotifier;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("Start checking rules with args : {}", Arrays.toString(args.getSourceArgs()));
        // Récupération et validation des arguments d'entrée
        argsParser.verifyArgs(args);
        // Lancement des vérifications des règles et génération du rapport
        Report report = reportCheckResults();
        log.info("Finished checking rules, preparing to send notifications : {}", report);
        // Préparation et envoi des notifications par mail
        emailNotifier.notify(report);
    }

    private Report reportCheckResults() {
        List<GraviteeApiDefinition> apis = loader.loadApiDefinitions();
        return new Report(checkRulesForEachApi(apis), Instant.now().toString(), envId);
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
