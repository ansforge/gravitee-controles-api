/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.services.notification;

import fr.gouv.esante.apim.checkrules.model.notification.Email;
import fr.gouv.esante.apim.checkrules.model.results.Report;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


/**
 * Implémentation de Notifier.
 * Se charge d'envoyer un email contenant les résultats
 * de la vérification des règles à une liste de destinataires.
 * Chaque adresse de destinataire est validée contre la norme
 * RFC_5322.
 */
@Service
@Slf4j
public class EmailNotifier implements Notifier {
    
    /**
     * Allows almost all characters in the email address, excpet the pipe character (|) and single quote (‘),
     * as these present a potential SQL injection risk when passed from the client site to the server
     */
    private static final String RFC_5322_PATTERN = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";

    @Autowired
    private JavaMailSender emailSender;

    @Value("${recipients.filepath}")
    private String recipients;


    public void notify(Report report) {
        // On récupère les destinataires et on vérifie que chaque adresse est valide
        List<String> recipientsList = parseRecipientsList()
                .stream()
                .filter(this::verifyEmailAddress)
                .toList();
        // Envoi de l'email à chaque destinataire
        Email email = new Email(report);
        for (String recipient : recipientsList) {
            sendMail(email, recipient);
        }
    }

    private void sendMail(Email email, String to) {
        MimeMessage message = emailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(email.getFrom());
            helper.setTo(to);
            helper.setSubject(email.getSubject());
            helper.setText(email.getBody());
            helper.addAttachment("apim-checkrules-results.json", email.getAttachment());
            emailSender.send(message);
        } catch (MessagingException e) {
            log.error("Erreur lors de la création du mail de rapport", e);
        }
    }

    private List<String> parseRecipientsList() {
        List<String> recipientsList = new ArrayList<>();
        File recipientsFile = new File(recipients);
        // Open file and parse recipients
        if (recipientsFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(recipientsFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    recipientsList.add(line);
                }
            } catch (FileNotFoundException e) {
                log.error("Recipient list file not found", e);
            } catch (IOException e) {
                log.error("Error reading recipient list", e);
            }
        }
        if (recipientsList.isEmpty()) {
            log.error("Recipient list is empty");
        }
        return recipientsList;
    }

    private boolean verifyEmailAddress(String email) {
        return patternMatches(email, RFC_5322_PATTERN);
    }

    public static boolean patternMatches(String emailAddress, String regexPattern) {
        return Pattern.compile(regexPattern)
                .matcher(emailAddress)
                .matches();
    }

}