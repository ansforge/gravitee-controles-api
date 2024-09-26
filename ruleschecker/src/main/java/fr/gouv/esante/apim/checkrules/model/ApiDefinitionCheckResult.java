/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


/**
 * Résultat des contrôles de règles sur une API hébergée sur l'API Manager
 */
@Getter
@Setter
public class ApiDefinitionCheckResult {

    /**
     * Liste des résultats des vérifications de chaque règle
     */
    private List<RuleResult> ruleResults;

    /**
     * Date et heure du début de la vérification de l'API
     */
    private String timestamp;

    /**
     * Nom de l'API contrôlée
     */
    private String apiDefinitionName;

}
