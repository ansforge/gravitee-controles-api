/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.services;

import fr.gouv.esante.apim.checkrules.exception.ApimFileNotFoundException;
import fr.gouv.esante.apim.checkrules.exception.ApimMissingArgumentException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
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
                "--env=myTestEnv",
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
                "--env=myTestEnv",
                "--unusedArg2=",
                "--apikey=myApiKey",
                "--recipients.filepath=src/test/resources/utils/recipients-mails",
                "--unusedArg3=42"
        );
        argsChecker.verifyArgs(args);
    }

    @Test
    void verifyArgsNoArgTest() {
        ArgumentsChecker argsChecker = new ArgumentsChecker();
        ApplicationArguments args = new DefaultApplicationArguments();
        assertThrows(ApimMissingArgumentException.class, () -> argsChecker.verifyArgs(args));
    }

    @Test
    void verifyArgsEnvDoesntExistsTest() {
        ArgumentsChecker argsChecker = new ArgumentsChecker();
        ApplicationArguments args = new DefaultApplicationArguments(
                "--apikey=myApiKey",
                "--recipients.filepath=src/test/resources/utils/recipients-mails"
        );
        assertThrows(ApimMissingArgumentException.class, () -> argsChecker.verifyArgs(args));
    }

    @Test
    void verifyArgsEnvIsEmptyTest() {
        ArgumentsChecker argsChecker = new ArgumentsChecker();
        ApplicationArguments args = new DefaultApplicationArguments(
                "--env",
                "--apikey=myApiKey",
                "--recipients.filepath=src/test/resources/utils/recipients-mails"
        );
        assertThrows(ApimMissingArgumentException.class, () -> argsChecker.verifyArgs(args));
    }

    @Test
    void verifyArgsEnvHasNoValueTest() {
        ArgumentsChecker argsChecker = new ArgumentsChecker();
        ApplicationArguments args = new DefaultApplicationArguments(
                "--env=",
                "--apikey=myApiKey",
                "--recipients.filepath=src/test/resources/utils/recipients-mails"
        );
        assertThrows(ApimMissingArgumentException.class, () -> argsChecker.verifyArgs(args));
    }

    @Test
    void verifyArgsEnvIsDoubledTest() {
        ArgumentsChecker argsChecker = new ArgumentsChecker();
        ApplicationArguments args = new DefaultApplicationArguments(
                "env",
                "--env=myTestEnv",
                "--apikey=myApiKey",
                "--recipients.filepath=src/test/resources/utils/recipients-mails"
        );
        argsChecker.verifyArgs(args);
    }

    @Test
    void verifyArgsApiKeyDoesntExistsTest() {
        ArgumentsChecker argsChecker = new ArgumentsChecker();
        ApplicationArguments args = new DefaultApplicationArguments(
                "--env=myTestEnv",
                "--recipients.filepath=src/test/resources/utils/recipients-mails"
        );
        assertThrows(ApimMissingArgumentException.class, () -> argsChecker.verifyArgs(args));
    }

    @Test
    void verifyArgsApikeyIsEmptyTest() {
        ArgumentsChecker argsChecker = new ArgumentsChecker();
        ApplicationArguments args = new DefaultApplicationArguments(
                "--env=myTestEnv",
                "--apikey",
                "--recipients.filepath=src/test/resources/utils/recipients-mails"
        );
        assertThrows(ApimMissingArgumentException.class, () -> argsChecker.verifyArgs(args));
    }

    @Test
    void verifyArgsApikeyHasNoValueTest() {
        ArgumentsChecker argsChecker = new ArgumentsChecker();
        ApplicationArguments args = new DefaultApplicationArguments(
                "--env=myTestEnv",
                "--apikey=",
                "--recipients.filepath=src/test/resources/utils/recipients-mails"
        );
        assertThrows(ApimMissingArgumentException.class, () -> argsChecker.verifyArgs(args));
    }

    @Test
    void verifyArgsApikeyIsDoubledTest() {
        ArgumentsChecker argsChecker = new ArgumentsChecker();
        ApplicationArguments args = new DefaultApplicationArguments(
                "--env=myTestEnv",
                "--apikey=myApiKey",
                "apikey",
                "--recipients.filepath=src/test/resources/utils/recipients-mails"
        );
        argsChecker.verifyArgs(args);
    }

    @Test
    void verifyArgsFilePathDoesntExistsTest() {
        ArgumentsChecker argsChecker = new ArgumentsChecker();
        ApplicationArguments args = new DefaultApplicationArguments(
                "--env=myTestEnv",
                "--apikey=myApiKey"
        );
        assertThrows(ApimMissingArgumentException.class, () -> argsChecker.verifyArgs(args));
    }

    @Test
    void verifyArgsFilePathIsEmptyTest() {
        ArgumentsChecker argsChecker = new ArgumentsChecker();
        ApplicationArguments args = new DefaultApplicationArguments(
                "--env=myTestEnv",
                "--apikey=myApiKey",
                "--recipients.filepath"
        );
        assertThrows(ApimMissingArgumentException.class, () -> argsChecker.verifyArgs(args));
    }

    @Test
    void verifyArgsFilePathHasNoValueTest() {
        ArgumentsChecker argsChecker = new ArgumentsChecker();
        ApplicationArguments args = new DefaultApplicationArguments(
                "--env=myTestEnv",
                "--apikey=myApiKey",
                "--recipients.filepath="
        );
        assertThrows(ApimMissingArgumentException.class, () -> argsChecker.verifyArgs(args));
    }

    @Test
    void verifyArgsFilePathIsDoubledTest() {
        ArgumentsChecker argsChecker = new ArgumentsChecker();
        ApplicationArguments args = new DefaultApplicationArguments(
                "--env=myTestEnv",
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
                "--env=myTestEnv",
                "--apikey=myApiKey",
                "--recipients.filepath=src/test/resources/utils/recipients-mails"
        );
        argsChecker.verifyArgs(args);
    }

    @Test
    void verifyArgsFileDoesntExistsTest() throws ApimFileNotFoundException {
        ArgumentsChecker argsChecker = new ArgumentsChecker();
        ApplicationArguments args = new DefaultApplicationArguments(
                "--env=myTestEnv",
                "--apikey=myApiKey",
                "--recipients.filepath=src/test/resources/utils/not-existing-recipients-mails"
        );
        assertThrows(ApimFileNotFoundException.class, () -> argsChecker.verifyArgs(args));
    }
}