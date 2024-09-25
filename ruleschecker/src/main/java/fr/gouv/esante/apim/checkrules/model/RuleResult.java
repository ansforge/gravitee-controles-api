package fr.gouv.esante.apim.checkrules.model;

import lombok.Getter;

import java.time.Instant;


/**
 * Résultat complet de la vérification d'une règle pour une API
 */
@Getter
public class RuleResult {

    /**
     * Définition de la règle vérifiée
     */
    private String ruleName;

    /**
     * Résultat de la vérification
     */
    private boolean success;
    /**
     * Description verbeuse du résultat
     */
    private String message;
    /**
     * Date de vérification de la règle au format GMT
     */
    private String timestamp;

    public RuleResult(String ruleName, boolean success, String message) {
        this.ruleName = ruleName;
        this.success = success;
        this.message = message;
        this.timestamp = Instant.now().toString();
    }
}
