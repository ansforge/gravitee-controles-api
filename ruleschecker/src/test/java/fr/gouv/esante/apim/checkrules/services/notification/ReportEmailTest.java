/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.services.notification;

import fr.gouv.esante.apim.checkrules.model.notification.ReportEmail;
import fr.gouv.esante.apim.checkrules.model.results.ApiDefinitionCheckResult;
import fr.gouv.esante.apim.checkrules.model.results.Report;
import fr.gouv.esante.apim.checkrules.model.results.RuleResult;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ActiveProfiles({ "test" })
@Slf4j
class ReportEmailTest {

    @Test
    public void notifyEmailCheckFailureTest() throws IOException {
        String now = "2024-10-16T13:39:34.347787200Z";
        // Construction des résultats de contrôles de 3 règles quelconques, dont 1 en échec
        RuleResult rule1Result = new RuleResult("rule1", true, "Success message 1", now);
        RuleResult rule2Result = new RuleResult("rule2", false, "Failure message 2", now);
        RuleResult rule3Result = new RuleResult("rule3", true, "Success message 3", now);

        ApiDefinitionCheckResult apiResult = new ApiDefinitionCheckResult();
        apiResult.setTimestamp(now);
        apiResult.setApiDefinitionName("API under test");
        apiResult.setRuleResults(List.of(rule1Result, rule2Result, rule3Result));

        Map<String, ApiDefinitionCheckResult> apiResultsMap = new HashMap<>();
        apiResultsMap.put(apiResult.getApiDefinitionName(), apiResult);
        // Construction des Strings attendues dans le mail :
        String expectedSubject = "[APIM] Echec du contrôle des règles d'implémentation sur l'environnement test env";
        String expectedBody = """
                Synthèse des résultats :
                Au moins une infraction aux règles d'implémentation dans l'APIM a été détectée
                
                API under test : Echec - Non conforme aux règles d'implementation""";
        String expectedReport = Files.readAllLines(Paths.get("src/test/resources/__files/report-ko.json")).get(0);
        // Construction du mail correspondant au rapport assemblé
        Report report = new Report(apiResultsMap, now, "test env");
        ReportEmail email = new ReportEmail(report);

        assertNotNull(email);
        assertEquals("noreply@esante.gouv.fr", email.getFrom());
        // L'objet du mail dépend du résultat des contrôles
        assertEquals(expectedSubject, email.getSubject());
        // Le corps du mail dépend des résultats de chaque API testée
        assertEquals(expectedBody, email.getBody());
        assertNotNull(email.getAttachment());

        String actualReport = Files.readString(email.getAttachment().toPath());
        assertEquals(expectedReport, actualReport);
    }

    @Test
    public void notifyEmailCheckSuccessTest() throws IOException {
        String now = "2024-10-16T13:39:34.347787200Z";
        // Construction des résultats de contrôles de 3 règles quelconques, tous en succès
        RuleResult rule1Result = new RuleResult("rule1", true, "Success message 1", now);
        RuleResult rule2Result = new RuleResult("rule2", true, "Success message 2", now);
        RuleResult rule3Result = new RuleResult("rule3", true, "Success message 3", now);

        ApiDefinitionCheckResult apiResult = new ApiDefinitionCheckResult();
        apiResult.setTimestamp(now);
        apiResult.setApiDefinitionName("API under test");
        apiResult.setRuleResults(List.of(rule1Result, rule2Result, rule3Result));

        Map<String, ApiDefinitionCheckResult> apiResultsMap = new HashMap<>();
        apiResultsMap.put(apiResult.getApiDefinitionName(), apiResult);
        // Construction des Strings attendues dans le mail :
        String expectedSubject = "[APIM] Succès du contrôle des règles d'implémentation sur l'environnement test env";
        String expectedBody = """
                Synthèse des résultats :
                Toutes les APIs contrôlées sont conformes aux règles d'implémentation dans l'APIM
                
                API under test : Succès - Respecte toutes les règles d'implementation""";
        String expectedReport = Files.readAllLines(Paths.get("src/test/resources/__files/report-ok.json")).get(0);
        // Construction du mail correspondant au rapport assemblé
        Report report = new Report(apiResultsMap, now, "test env");
        ReportEmail email = new ReportEmail(report);

        // L'objet du mail dépend du résultat des contrôles
        assertEquals(expectedSubject, email.getSubject());
        // Le corps du mail dépend des résultats de chaque API testée
        assertEquals(expectedBody, email.getBody());

        String actualReport = Files.readString(email.getAttachment().toPath());
        assertEquals(expectedReport, actualReport);

    }
}