package lenicorp.admin.notification.controller.services;

import lenicorp.admin.notification.controller.dao.EmailNotificationRepo;
import lenicorp.admin.notification.model.dto.MailAttachment;
import lenicorp.admin.notification.model.dto.MailMapper;
import lenicorp.admin.notification.model.dto.MailRequest;
import lenicorp.admin.notification.model.dto.MailResponse;
import lenicorp.admin.notification.model.entities.EmailNotification;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService implements MailServiceInterface
{
    private final JavaMailSender mailSender;
    private final MailConfig mailConfig;
    private final EmailNotificationRepo emailNotificationRepo;
    private final MailMapper mailMapper;

    @Override
    public CompletableFuture<MailResponse> envoyerEmailActivation(String destinataire, String nomDestinataire, String lienActivation)
    {
        String sujet = "Activation de votre compte e-courrier";

        // Charger le fichier HTML d'activation
        String corpsHtml = chargerTemplateHtml("templates/emails/activation-email.html")
                .replace("${nomDestinataire}", nomDestinataire)
                .replace("${lienActivation}", lienActivation);

        MailRequest mailRequest = MailRequest.builder()
                .to(destinataire)
                .subject(sujet)
                .content(corpsHtml)
                .isHtml(true)
                .build();

        return sendMailAsync(mailRequest);
    }

    @Override
    public CompletableFuture<MailResponse> envoyerEmailReinitialisation(String destinataire, String nomDestinataire, String lienReset)
    {
        String sujet = "Réinitialisation de votre mot de passe";

        // Charger le fichier HTML de réinitialisation
        String corpsHtml = chargerTemplateHtml("templates/emails/password-reset-email.html")
                .replace("${nomDestinataire}", nomDestinataire)
                .replace("${lienReset}", lienReset);

        MailRequest mailRequest = MailRequest.builder()
                .to(destinataire)
                .subject(sujet)
                .content(corpsHtml)
                .isHtml(true)
                .build();

        return sendMailAsync(mailRequest);
    }

    @Override
    public CompletableFuture<MailResponse> envoyerEmailAdhesionApprouvee(String destinataire, String nomDestinataire, String nomAssociation, String lienConnexion)
    {
        String sujet = "Félicitations ! Votre demande d'adhésion a été approuvée";

        // Charger le fichier HTML de notification d'adhésion approuvée
        String corpsHtml = chargerTemplateHtml("templates/emails/demande-adhesion-approuvee.html")
                .replace("${nomDestinataire}", nomDestinataire)
                .replace("${nomAssociation}", nomAssociation)
                .replace("${lienConnexion}", lienConnexion);

        MailRequest mailRequest = MailRequest.builder()
                .to(destinataire)
                .subject(sujet)
                .content(corpsHtml)
                .isHtml(true)
                .build();

        return sendMailAsync(mailRequest);
    }

    @Override
    public CompletableFuture<MailResponse> envoyerEmailMembreNonActif(String destinataire, String nomDestinataire, String nomAssociation)
    {
        String sujet = "Notification : Statut de membre non actif";

        // Charger le fichier HTML de notification de membre non actif
        String corpsHtml = chargerTemplateHtml("templates/emails/membre-non-actif.html")
                .replace("${nomDestinataire}", nomDestinataire)
                .replace("${nomAssociation}", nomAssociation);

        MailRequest mailRequest = MailRequest.builder()
                .to(destinataire)
                .subject(sujet)
                .content(corpsHtml)
                .isHtml(true)
                .build();

        return sendMailAsync(mailRequest);
    }

    @Override
    public CompletableFuture<MailResponse> envoyerEmailMembreExclu(String destinataire, String nomDestinataire, String nomAssociation)
    {
        String sujet = "Notification importante : Exclusion de l'association";

        // Charger le fichier HTML de notification d'exclusion
        String corpsHtml = chargerTemplateHtml("templates/emails/membre-exclu.html")
                .replace("${nomDestinataire}", nomDestinataire)
                .replace("${nomAssociation}", nomAssociation);

        MailRequest mailRequest = MailRequest.builder()
                .to(destinataire)
                .subject(sujet)
                .content(corpsHtml)
                .isHtml(true)
                .build();

        return sendMailAsync(mailRequest);
    }

    @Override
    public CompletableFuture<MailResponse> envoyerEmailMembreActif(String destinataire, String nomDestinataire, String nomAssociation, String lienConnexion)
    {
        String sujet = "Félicitations : Vous êtes désormais membre actif";

        // Charger le fichier HTML de notification de membre actif
        String corpsHtml = chargerTemplateHtml("templates/emails/membre-actif.html")
                .replace("${nomDestinataire}", nomDestinataire)
                .replace("${nomAssociation}", nomAssociation)
                .replace("${lienConnexion}", lienConnexion);

        MailRequest mailRequest = MailRequest.builder()
                .to(destinataire)
                .subject(sujet)
                .content(corpsHtml)
                .isHtml(true)
                .build();

        return sendMailAsync(mailRequest);
    }

    @Override @Transactional
    public CompletableFuture<MailResponse> sendMailAsync(MailRequest mailRequest) 
    {
        EmailNotification notification = mailMapper.mapToToEmailNotification(mailRequest);
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Créer et envoyer l'email
                MimeMessage message = createMimeMessage(mailRequest);
                mailSender.send(message);

                // Si succès, marquer comme envoyé et persister
                notification.setSent(true);
                notification.setSendingDate(LocalDateTime.now());
                emailNotificationRepo.save(notification);

                return MailResponse.builder()
                        .success(true)
                        .sentAt(LocalDateTime.now())
                        .build();
            }
            catch (Exception e) {
                log.error("Erreur lors de l'envoi de l'email à: {}", mailRequest.getTo(), e);
                return MailResponse.builder()
                        .success(false)
                        .errorMessage(e.getMessage())
                        .sentAt(LocalDateTime.now())
                        .build();
            }
        });
    }


    private MimeMessage createMimeMessage(MailRequest mailRequest) throws MessagingException 
    {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // Définir les propriétés de base
        helper.setTo(mailRequest.getTo());
        helper.setSubject(mailRequest.getSubject());
        helper.setText(mailRequest.getContent(), mailRequest.isHtml());
        helper.setFrom(mailConfig.getUsername());

        // Ajouter les destinataires en copie
        if (mailRequest.getCc() != null && !mailRequest.getCc().isEmpty()) {
            helper.setCc(mailRequest.getCc().toArray(new String[0]));
        }

        // Ajouter les destinataires en copie cachée
        if (mailRequest.getBcc() != null && !mailRequest.getBcc().isEmpty()) {
            helper.setBcc(mailRequest.getBcc().toArray(new String[0]));
        }

        // Ajouter les pièces jointes
        if (mailRequest.getAttachments() != null && !mailRequest.getAttachments().isEmpty()) {
            for (MailAttachment attachment : mailRequest.getAttachments()) {
                ByteArrayResource resource = new ByteArrayResource(attachment.getContent());
                if (attachment.isInline() && attachment.getContentId() != null) {
                    helper.addInline(attachment.getContentId(), resource, attachment.getContentType());
                } else {
                    helper.addAttachment(attachment.getFileName(), resource, attachment.getContentType());
                }
            }
        }

        return message;
    }

    private String chargerTemplateHtml(String cheminTemplate)
    {
        try
        {
            Path chemin = Paths.get(getClass().getClassLoader().getResource(cheminTemplate).toURI());
            return Files.readString(chemin);
        } catch (IOException | NullPointerException | URISyntaxException e)
        {
            log.error("Erreur lors du chargement du template HTML : {}", cheminTemplate, e);
            throw new RuntimeException("Impossible de charger le template : " + cheminTemplate, e);
        }
    }

}
