/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.services.notification;


import fr.gouv.esante.apim.checkrules.model.results.Report;


public interface Notifier {

    void notify(Report report);

}
