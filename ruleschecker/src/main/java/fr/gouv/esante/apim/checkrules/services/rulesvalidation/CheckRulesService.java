/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.services.rulesvalidation;

import fr.gouv.esante.apim.checkrules.exception.ApimRulecheckerException;
import fr.gouv.esante.apim.checkrules.model.definition.GraviteeApiDefinition;
import fr.gouv.esante.apim.checkrules.model.results.ApiDefinitionCheckResult;
import fr.gouv.esante.apim.checkrules.model.results.Report;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CheckRulesService {

    @Value("${envid}")
    private String envId;

    private final ApiDefinitionLoader loader;
    private final RulesChecker rulesChecker;

    public CheckRulesService(ApiDefinitionLoader loader, RulesChecker rulesChecker) {
        this.loader = loader;
        this.rulesChecker = rulesChecker;
    }

    public Report check() throws ApimRulecheckerException {
        log.info("Checking rules for env :{}", envId);
        // Chargement de toutes les définitions d'API de l'APIM
        List<GraviteeApiDefinition> apis = loader.loadApiDefinitions();
        // Controle de la conformité des APIs
        Map<String, ApiDefinitionCheckResult> results = checkRulesForEachApi(apis);
        // Génération du rapport de controle
        Report report = new Report(results, Instant.now().toString(), envId);
        log.info("Finished checking rules, preparing to send notifications : {}", report);
        return report;
    }

    /**
     * Vérification des règles d'implémentation d'une API dans l'APIM.
     *
     * @param apis List of API definitions to check
     * @return checkresults
     */
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
