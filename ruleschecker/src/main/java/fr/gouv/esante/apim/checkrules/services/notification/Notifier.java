/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.services.notification;


import fr.gouv.esante.apim.checkrules.model.results.Report;

/**
 * Interface que doivent implémenter tous les notifiers
 * afin de publier le rapport de résultats
 */
public interface Notifier {

    void notify(Report report);

}
