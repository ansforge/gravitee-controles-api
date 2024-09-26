/*
 * (c) Copyright 1998-2024, ANS. All rights reserved.
 */
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
    private final String ruleName;

    /**
     * Résultat de la vérification
     */
    private final boolean success;
    /**
     * Description verbeuse du résultat
     */
    private final String message;
    /**
     * Date de vérification de la règle au format GMT
     */
    private final String timestamp;

    public RuleResult(String ruleName, boolean success, String message) {
        this.ruleName = ruleName;
        this.success = success;
        this.message = message;
        this.timestamp = Instant.now().toString();
    }
}
