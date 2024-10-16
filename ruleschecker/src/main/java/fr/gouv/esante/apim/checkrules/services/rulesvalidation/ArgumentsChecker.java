/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.services.rulesvalidation;

import fr.gouv.esante.apim.checkrules.exception.ApimFileNotFoundException;
import fr.gouv.esante.apim.checkrules.exception.ApimMissingArgumentException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;


/**
 * Service controllant l'existence des arguments
 * donnés en ligne de commande au démarrage
 * de l'application.
 */
@Service
@Slf4j
public class ArgumentsChecker {

    private static final String ENV_ID = "envid";
    private static final String API_KEY = "apikey";
    private static final String RECIPIENTS_FILEPATH = "recipients.filepath";

    /**
     * Contrôle la présence et la validité de chaque argument obligatoire
     * donné en ligne de commande.
     * Lève une {@code ApimFileNotFoundException} contenant le détail
     * des arguments attendus si au moins l'un d'entre eux est manquant ou
     * a une valeur vide ou non valide dans le cas du fichier d'adresses mail.
     *
     * @param args - Arguments donnés en ligne de commande
     * @throws ApimFileNotFoundException
     */
    public void verifyArgs(ApplicationArguments args) throws RuntimeException {
        parseArgs(args);
        checkFile(args.getOptionValues(RECIPIENTS_FILEPATH).get(0));
    }

    private static void parseArgs(ApplicationArguments args) {
        if (args.getOptionNames().isEmpty()) {
            throw new ApimMissingArgumentException("No command line argument was found");
        }
        if (!args.containsOption(ENV_ID)) {
            throw new ApimMissingArgumentException("Missing --envid argument");
        } else if (args.getOptionValues(ENV_ID).isEmpty() ||
                        args.getOptionValues(ENV_ID).get(0).trim().isEmpty()) {
            throw new ApimMissingArgumentException("Empty --envid argument");
        } else {
            log.info("Checking APIs on environment: {}", args.getOptionValues(ENV_ID).get(0).trim());
        }

        if (!args.containsOption(API_KEY)) {
            throw new ApimMissingArgumentException("Missing --apikey argument");
        } else if (args.getOptionValues(API_KEY).isEmpty() ||
                args.getOptionValues(API_KEY).get(0).trim().isEmpty()) {
            throw new ApimMissingArgumentException("Empty --apikey argument");
        } else {
            log.info("API_KEY found");
        }

        if (!args.containsOption(RECIPIENTS_FILEPATH)) {
            throw new ApimMissingArgumentException("Missing --recipients.filepath argument");
        } else if (args.getOptionValues(RECIPIENTS_FILEPATH).isEmpty() ||
                args.getOptionValues(RECIPIENTS_FILEPATH).get(0).trim().isEmpty()) {
            throw new ApimMissingArgumentException("Empty --recipients.filepath argument");
        } else {
            log.info("Recipients list filepath found");
        }
    }

    private void checkFile(String filepath) throws ApimFileNotFoundException {
        final Path path = Path.of(filepath);
        String errorMessage = "";
        if(!Files.exists(path)) {
            errorMessage = "Recipients list file not found: %s".formatted(filepath);
        } else if(!Files.isReadable(path)) {
            errorMessage = "Recipients list file cannot be read: %s".formatted(filepath);
        }
        if(!errorMessage.trim().isEmpty()) {
            throw new ApimFileNotFoundException(errorMessage);
        }
    }
}
