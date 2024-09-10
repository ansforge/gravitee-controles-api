package fr.gouv.esante.apim.checkrules;

import fr.gouv.esante.apim.checkrules.model.ApiDefinitionCheckResult;
import fr.gouv.esante.apim.checkrules.model.GraviteeApiDefinition;
import fr.gouv.esante.apim.checkrules.services.RulesChecker;
import fr.gouv.esante.apim.client.api.ApiPlansApi;
import fr.gouv.esante.apim.client.api.ApisApi;
import fr.gouv.esante.apim.client.model.ApiGravitee;
import fr.gouv.esante.apim.client.model.ApisResponseGravitee;
import fr.gouv.esante.apim.client.model.PlanGravitee;
import fr.gouv.esante.apim.client.model.PlansResponseGravitee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class CheckRulesRunner implements CommandLineRunner {

    String defaultMessage = "Implémenter les contrôles";

    private static final String ENV_ID = "environment identfier";

    private final ApisApi apisApi;

    private final ApiPlansApi apiPlansApi;

    private final RulesChecker rulesChecker;

    public CheckRulesRunner(ApisApi apisApi, ApiPlansApi apiPlansApi, RulesChecker rulesChecker) {
        this.apisApi = apisApi;
        this.apiPlansApi = apiPlansApi;
        this.rulesChecker = rulesChecker;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info(defaultMessage);
        // get data from args
        // run checks
        check();
        log.info(defaultMessage);
    }

    private void check() {
        // Initialiser la liste de résultats
        Map<String, ApiDefinitionCheckResult> allResults = new HashMap<>();

        try {
            // Appel de chaque API definition
            ApisResponseGravitee apisResponse = apisApi.listApis(
                    ENV_ID,
                    1,
                    100,
                    null
            );
            List<ApiGravitee> apis = apisResponse.getData();

            // Récupération des plans de chaque API
            for (ApiGravitee apiGravitee : apis) {
                ApiGravitee apiDefinition = apisApi.getApi(ENV_ID, apiGravitee.getId());
                PlansResponseGravitee plansResponse = apiPlansApi.listApiPlans(
                        ENV_ID,
                        apiGravitee.getId(),
                        null,
                        null,
                        null,
                        1,
                        100
                );
                List<PlanGravitee> plans = plansResponse.getData();
                List<PlanGravitee> fullPlans = plansResponse.getData();

                // (Vérifier si cette étape est vraiment nécessaire)
                for (PlanGravitee planDetails : plans) {
                    // Get (again) API plans from their ids and store in 2nd list
                    fullPlans.add(
                            apiPlansApi.getApiPlan(ENV_ID, apiGravitee.getId(), planDetails.getId())
                    );
                }

                // Build an object GraviteeApiDefinition
                GraviteeApiDefinition graviteeApiDefinition = new GraviteeApiDefinition(apiDefinition, fullPlans);

                // Pour chaque API on lance RuleChecker
                allResults.put(apiDefinition.getName(), rulesChecker.checkRules(graviteeApiDefinition));
            }
        } catch (Exception e) {
            log.error("CAUGHT ERROR");
            log.error(e.getMessage());
            log.error("END ERROR");
        }

        // Collecter résultats et lancer les notifs
        log.info(allResults.toString());

    }
}
