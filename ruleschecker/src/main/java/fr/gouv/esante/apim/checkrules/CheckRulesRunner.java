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
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@Profile({ "production", "test" })
public class CheckRulesRunner implements CommandLineRunner {

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
        log.info("Starting check rules");
        // get data from args
        // run checks
        check();
        log.info("Finished check rules");
    }

    private void check() {
        // Initialize results list
        Map<String, ApiDefinitionCheckResult> allResults = new HashMap<>();

        try {
            // Get API Definitions list
            ApisResponseGravitee apisResponse = apisApi.listApis(
                    ENV_ID,
                    1,
                    100,
                    Collections.emptyList()
            );
            List<ApiGravitee> apis = apisResponse.getData();

            // Test each API
            for (ApiGravitee apiGravitee : apis) {
                // Get each individual API Definition
                ApiGravitee apiDefinition = apisApi.getApi(ENV_ID, apiGravitee.getId());

                // Get plans list belonging to the API
                PlansResponseGravitee plansResponse = apiPlansApi.listApiPlans(
                        ENV_ID,
                        apiGravitee.getId(),
                        Collections.emptyList(),
                        Collections.emptyList(),
                        null,
                        1,
                        100
                );
                List<PlanGravitee> plans = plansResponse.getData();
                List<PlanGravitee> fullPlans = plansResponse.getData();

                // (Confirm with real API response if this step is really needed)
                for (PlanGravitee planDetails : plans) {
                    // Get (again) API plans from their ids and store it in fullPlans list
                    fullPlans.add(
                            apiPlansApi.getApiPlan(ENV_ID, apiGravitee.getId(), planDetails.getId())
                    );
                }

                // Build an object GraviteeApiDefinition
                GraviteeApiDefinition graviteeApiDefinition = new GraviteeApiDefinition(apiDefinition, fullPlans);

                // Execute RuleChecker for each API and collect results
                allResults.put(apiDefinition.getName(), rulesChecker.checkRules(graviteeApiDefinition));
            }
        } catch (Exception e) {
            log.error("CAUGHT ERROR");
            log.error(e.getMessage());
            log.error("END ERROR");
        }

        // Send notifications
        log.info(allResults.toString());

    }
}
