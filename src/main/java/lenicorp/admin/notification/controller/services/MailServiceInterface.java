package lenicorp.admin.notification.controller.services;

import lenicorp.admin.notification.model.dto.MailRequest;
import lenicorp.admin.notification.model.dto.MailResponse;

import java.util.concurrent.CompletableFuture;

public interface MailServiceInterface
{
    /**
     * Envoie un email pour activer un compte utilisateur.
     *
     * @param destinataire   L'adresse email du destinataire.
     * @param nomDestinataire Le nom du destinataire.
     * @param lienActivation Le lien pour activer le compte.
     * @return Un CompletableFuture de MailResponse décrivant le résultat de l'envoi.
     */
    CompletableFuture<MailResponse> envoyerEmailActivation(String destinataire, String nomDestinataire, String lienActivation);

    /**
     * Envoie un email pour réinitialiser le mot de passe.
     *
     * @param destinataire   L'adresse email du destinataire.
     * @param nomDestinataire Le nom du destinataire.
     * @param lienReset      Le lien pour réinitialiser le mot de passe.
     * @return Un CompletableFuture de MailResponse décrivant le résultat de l'envoi.
     */
    CompletableFuture<MailResponse> envoyerEmailReinitialisation(String destinataire, String nomDestinataire, String lienReset);

    /**
     * Envoie un email de notification d'adhésion approuvée.
     *
     * @param destinataire    L'adresse email du destinataire.
     * @param nomDestinataire Le nom du destinataire.
     * @param nomAssociation  Le nom de l'association.
     * @param lienConnexion   Le lien vers l'espace membre.
     * @return Un CompletableFuture de MailResponse décrivant le résultat de l'envoi.
     */
    CompletableFuture<MailResponse> envoyerEmailAdhesionApprouvee(String destinataire, String nomDestinataire, String nomAssociation, String lienConnexion);

    /**
     * Envoie un email pour informer un membre qu'il a été classé comme non actif.
     *
     * @param destinataire    L'adresse email du destinataire.
     * @param nomDestinataire Le nom du destinataire.
     * @param nomAssociation  Le nom de l'association.
     * @return Un CompletableFuture de MailResponse décrivant le résultat de l'envoi.
     */
    CompletableFuture<MailResponse> envoyerEmailMembreNonActif(String destinataire, String nomDestinataire, String nomAssociation);

    /**
     * Envoie un email pour informer un membre de son exclusion.
     *
     * @param destinataire    L'adresse email du destinataire.
     * @param nomDestinataire Le nom du destinataire.
     * @param nomAssociation  Le nom de l'association.
     * @return Un CompletableFuture de MailResponse décrivant le résultat de l'envoi.
     */
    CompletableFuture<MailResponse> envoyerEmailMembreExclu(String destinataire, String nomDestinataire, String nomAssociation);

    /**
     * Envoie un email pour informer un membre qu'il est désormais actif.
     *
     * @param destinataire    L'adresse email du destinataire.
     * @param nomDestinataire Le nom du destinataire.
     * @param nomAssociation  Le nom de l'association.
     * @param lienConnexion   Le lien vers l'espace membre.
     * @return Un CompletableFuture de MailResponse décrivant le résultat de l'envoi.
     */
    CompletableFuture<MailResponse> envoyerEmailMembreActif(String destinataire, String nomDestinataire, String nomAssociation, String lienConnexion);

    /**
     * Envoie un email pour informer un membre que sa demande d'adhésion est acceptée et qu'il doit payer.
     *
     * @param destinataire    L'adresse email du destinataire.
     * @param nomDestinataire Le nom du destinataire.
     * @param nomAssociation  Le nom de l'association.
     * @param montantDroitAdhesion Le montant du droit d'adhésion.
     * @param lienConnexion   Le lien vers l'espace membre pour le paiement.
     * @return Un CompletableFuture de MailResponse décrivant le résultat de l'envoi.
     */
    CompletableFuture<MailResponse> envoyerEmailAdhesionAccepteePaiement(String destinataire, String nomDestinataire, String nomAssociation, double montantDroitAdhesion, String lienConnexion);

    /**
     * Envoie un email avec le contenu spécifié.
     *
     * @param mailRequest Les informations sur l'email à envoyer.
     * @return Un CompletableFuture de MailResponse décrivant le résultat de l'envoi.
     */
    CompletableFuture<MailResponse> sendMailAsync(MailRequest mailRequest);

}
