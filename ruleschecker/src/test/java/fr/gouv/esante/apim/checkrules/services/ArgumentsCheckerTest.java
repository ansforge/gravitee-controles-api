/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.services;

import fr.gouv.esante.apim.checkrules.exception.ApimFileNotFoundException;
import fr.gouv.esante.apim.checkrules.exception.ApimMissingArgumentException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest(classes = ArgumentsCheckerTest.class)
@ActiveProfiles({ "test" })
@Slf4j
class ArgumentsCheckerTest {

    @Test
    void verifyArgsNominalTest() {
        ArgumentsChecker argsChecker = new ArgumentsChecker();
        ApplicationArguments args = new DefaultApplicationArguments(
                "--envid=myTestEnv",
                "--apikey=myApiKey",
                "--recipients.filepath=src/test/resources/utils/recipients-mails"
        );
        argsChecker.verifyArgs(args);
    }

    @Test
    void verifyArgUnusedArgsTest() {
        ArgumentsChecker argsChecker = new ArgumentsChecker();
        ApplicationArguments args = new DefaultApplicationArguments(
                "--unusedArg1",
                "--envid=myTestEnv",
                "--unusedArg2=",
                "--apikey=myApiKey",
                "--recipients.filepath=src/test/resources/utils/recipients-mails",
                "--unusedArg3=42"
        );
        argsChecker.verifyArgs(args);
    }

    @ParameterizedTest
    @CsvSource({
            "'','',''",
            "'',--apikey=myApiKey, --recipients.filepath=src/test/resources/utils/recipients-mails",
            "--envid, --apikey=myApiKey, --recipients.filepath=src/test/resources/utils/recipients-mails",
            "--envid=, --apikey=myApiKey, --recipients.filepath=src/test/resources/utils/recipients-mails",
            "--envid=myTestEnv, '', --recipients.filepath=src/test/resources/utils/recipients-mails",
            "--envid=myTestEnv, --apikey, --recipients.filepath=src/test/resources/utils/recipients-mails",
            "--envid=myTestEnv, --apikey=, --recipients.filepath=src/test/resources/utils/recipients-mails",
            "--envid=myTestEnv, --apikey=myApiKey, ''",
            "--envid=myTestEnv, --apikey=myApiKey, --recipients.filepath",
            "--envid=myTestEnv, --apikey=myApiKey, --recipients.filepath=",
    })
    void verifyArgsMissingArgTest(String envid, String apiKey, String recipientsFilepath) {
        ArgumentsChecker argsChecker = new ArgumentsChecker();
        ApplicationArguments args = new DefaultApplicationArguments(envid, apiKey, recipientsFilepath);
        assertThrows(ApimMissingArgumentException.class, () -> argsChecker.verifyArgs(args));
    }

    @Test
    void verifyArgsEnvIsDoubledTest() {
        ArgumentsChecker argsChecker = new ArgumentsChecker();
        ApplicationArguments args = new DefaultApplicationArguments(
                "env",
                "--envid=myTestEnv",
                "--apikey=myApiKey",
                "--recipients.filepath=src/test/resources/utils/recipients-mails"
        );
        argsChecker.verifyArgs(args);
    }

    @Test
    void verifyArgsApikeyIsDoubledTest() {
        ArgumentsChecker argsChecker = new ArgumentsChecker();
        ApplicationArguments args = new DefaultApplicationArguments(
                "--envid=myTestEnv",
                "--apikey=myApiKey",
                "apikey",
                "--recipients.filepath=src/test/resources/utils/recipients-mails"
        );
        argsChecker.verifyArgs(args);
    }

    @Test
    void verifyArgsFilePathIsDoubledTest() {
        ArgumentsChecker argsChecker = new ArgumentsChecker();
        ApplicationArguments args = new DefaultApplicationArguments(
                "--envid=myTestEnv",
                "--apikey=myApiKey",
                "--recipients.filepath=src/test/resources/utils/recipients-mails",
                "recipients.filepath"
        );
        argsChecker.verifyArgs(args);
    }

    @Test
    void verifyArgsFileExistsAndIsReadableTest() throws ApimFileNotFoundException {
        ArgumentsChecker argsChecker = new ArgumentsChecker();
        ApplicationArguments args = new DefaultApplicationArguments(
                "--envid=myTestEnv",
                "--apikey=myApiKey",
                "--recipients.filepath=src/test/resources/utils/recipients-mails"
        );
        argsChecker.verifyArgs(args);
    }

    @Test
    void verifyArgsFileDoesntExistsTest() throws ApimFileNotFoundException {
        ArgumentsChecker argsChecker = new ArgumentsChecker();
        ApplicationArguments args = new DefaultApplicationArguments(
                "--envid=myTestEnv",
                "--apikey=myApiKey",
                "--recipients.filepath=src/test/resources/utils/not-existing-recipients-mails"
        );
        assertThrows(ApimFileNotFoundException.class, () -> argsChecker.verifyArgs(args));
    }
}