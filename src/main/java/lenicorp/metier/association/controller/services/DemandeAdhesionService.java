package lenicorp.metier.association.controller.services;

import jakarta.transaction.Transactional;
import lenicorp.admin.archive.controller.service.IDocumentService;
import lenicorp.admin.archive.model.dtos.request.UploadDocReq;
import lenicorp.admin.exceptions.AppException;
import lenicorp.admin.security.controller.services.specs.IUserService;
import lenicorp.admin.security.controller.services.specs.IJwtService;
import lenicorp.admin.types.controller.repositories.TypeRepo;
import lenicorp.admin.types.model.entities.Type;
import lenicorp.admin.utilities.StringUtils;
import lenicorp.metier.association.controller.repositories.AssoRepo;
import lenicorp.metier.association.controller.repositories.DemandeAdhesionRepo;
import lenicorp.metier.association.controller.repositories.AdhesionRepo;
import lenicorp.metier.association.model.dtos.AdhesionDTO;
import lenicorp.admin.security.model.dtos.CreateUserDTO;
import lenicorp.metier.association.model.dtos.CreateDemandeAdhesionDTO;
import lenicorp.metier.association.model.dtos.ReadDemandeAdhesionDTO;
import lenicorp.metier.association.model.entities.Adhesion;
import lenicorp.metier.association.model.entities.DemandeAdhesion;
import lenicorp.metier.association.model.mappers.DemandeAdhesionMapper;
import lenicorp.metier.association.model.mappers.AdhesionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DemandeAdhesionService implements IDemandeAdhesionService {

    private final DemandeAdhesionRepo repo;
    private final AdhesionRepo adhesionRepo;
    private final AssoRepo assoRepo;
    private final TypeRepo typeRepo;
    private final IJwtService jwtService;
    private final DemandeAdhesionMapper demandeAdhesionMapper;
    private final AdhesionMapper adhesionMapper;
    private final IUserService userService;
    private final IDocumentService documentService;

    // Statut codes (doivent exister dans le référentiel TYPE groupé adéquat)
    private static final String EN_ATTENTE = "EN_ATTENTE";
    private static final String EN_ETUDE = "EN_ETUDE";
    private static final String COMPLEMENTS_REQUIS = "COMPLEMENTS_REQUIS";
    private static final String EN_ATTENTE_PAIEMENT = "EN_ATTENTE_PAIEMENT";
    private static final String APPROUVEE = "APPROUVEE";
    private static final String REJETEE = "REJETEE";
    private static final String ANNULEE = "ANNULEE";

    private Type type(String code) {
        return typeRepo.findById(code).orElseThrow(() -> new AppException("Code statut inconnu " + code));
    }

    @Override
    @Transactional
    public ReadDemandeAdhesionDTO create(CreateDemandeAdhesionDTO dto) {
        if (dto.assoId() == null) throw new AppException("Association requise");
        if (!dto.accepteRgpd()) throw new AppException("Veuillez accepter le RGPD");
        // Validation conditionnelle de la charte et des statuts/règlements via méthodes repository (existence)
        boolean charteExiste = documentService.existsByTypeAndObject(dto.assoId(), "ASSOCIATION", "CHRT_ADH");
        boolean statutsReglementsExiste = documentService.existsByTypeAndObject(dto.assoId(), "ASSOCIATION", "DOC_ASSO_STATUTS_REGLEMENTS");

        if (charteExiste && !dto.accepteCharte()) {
            throw new AppException("Vous devez accepter la charte d'adhésion");
        }
        if (statutsReglementsExiste && !dto.accepteStatutsReglements()) {
            throw new AppException("Vous devez approuver les statuts et règlements");
        }
        // MapStruct mapping from DTO -> Entity, with current user from jwt
        DemandeAdhesion d = demandeAdhesionMapper.mapTopDemandeAdhesion(dto, jwtService);
        d.setStatut(type(EN_ATTENTE));
        // reference basique; à améliorer si besoin
        d.setReference("DEM-" + System.currentTimeMillis());

        d = repo.save(d);

        // Uploader les documents joints via CreateDemandeAdhesionDTO
        uploadDemandeDocuments(dto.documents(), d.getDemandeId());

        return demandeAdhesionMapper.mapTopDemandeAdhesionReadDTO(d);
    }

    @Override
    @Transactional
    public ReadDemandeAdhesionDTO createUserWithDemandeAdhesion(AdhesionDTO adhesionDTO) {
        // 1) Créer l'utilisateur avec profil
        CreateUserDTO createUserDTO = adhesionMapper.mapToCreateUserDto(adhesionDTO);
        createUserDTO.setProfileCode("PRFL_MBR_ASSO");
        createUserDTO.setUserProfileAssTypeCode("TITULAIRE");
        userService.createUserWithProfile(createUserDTO);

        // 2) Créer la demande d'adhésion
        CreateDemandeAdhesionDTO createDemandeDTO = adhesionMapper.mapToCreateDemandeAdhesionDto(adhesionDTO);
        ReadDemandeAdhesionDTO read = create(createDemandeDTO);

        // 3) Uploader les documents
        uploadDemandeDocuments(adhesionDTO.getDocuments(), read.demandeId());

        return read;
    }

    @Override
    @Transactional
    public ReadDemandeAdhesionDTO prendreEnEtude(Long demandeId) {
        DemandeAdhesion d = repo.lockById(demandeId)
                .orElseThrow(() -> new AppException("Demande introuvable"));
        String st = d.getStatut() == null ? null : d.getStatut().code;
        if (!(Objects.equals(st, EN_ATTENTE) || Objects.equals(st, COMPLEMENTS_REQUIS)))
            throw new AppException("Transition interdite");
        d.setStatut(type(EN_ETUDE));
        d = repo.save(d);
        return demandeAdhesionMapper.mapTopDemandeAdhesionReadDTO(d);
    }

    @Override
    @Transactional
    public ReadDemandeAdhesionDTO approuver(Long demandeId) {
        DemandeAdhesion d = repo.lockById(demandeId)
                .orElseThrow(() -> new AppException("Demande introuvable"));
        ensureState(d, EN_ETUDE);

        // Règle paiement selon droitAdhesion
        BigDecimal droit = assoRepo.findById(d.getAssociation().getAssoId())
                .orElseThrow(() -> new AppException("Association introuvable"))
                .getDroitAdhesion();

        d.setDateDecision(LocalDateTime.now());

        if (droit != null && droit.compareTo(BigDecimal.ZERO) > 0) {
            d.setStatut(type(EN_ATTENTE_PAIEMENT));
            d.setMontantCotisationEstime(droit);
            d = repo.save(d);
            return demandeAdhesionMapper.mapTopDemandeAdhesionReadDTO(d);
        }

        // Pas de paiement requis -> créer l'adhésion
        Adhesion adh = new Adhesion(null, d.getAssociation(), d.getSection(), true,
                d.getUser() == null ? null : String.valueOf(d.getUser().getUserId()));
        adh = adhesionRepo.save(adh);
        d.setAdhesionCreee(adh);
        d.setStatut(type(APPROUVEE));
        d = repo.save(d);
        return demandeAdhesionMapper.mapTopDemandeAdhesionReadDTO(d);
    }

    @Override
    @Transactional
    public ReadDemandeAdhesionDTO confirmerPaiement(Long demandeId) {
        DemandeAdhesion d = repo.lockById(demandeId)
                .orElseThrow(() -> new AppException("Demande introuvable"));
        ensureState(d, EN_ATTENTE_PAIEMENT);
        Adhesion adh = new Adhesion(null, d.getAssociation(), d.getSection(), true,
                d.getUser() == null ? null : String.valueOf(d.getUser().getUserId()));
        adh = adhesionRepo.save(adh);
        d.setAdhesionCreee(adh);
        d.setStatut(type(APPROUVEE));
        d.setDateDecision(LocalDateTime.now());
        d = repo.save(d);
        return demandeAdhesionMapper.mapTopDemandeAdhesionReadDTO(d);
    }

    @Override
    @Transactional
    public ReadDemandeAdhesionDTO rejeter(Long demandeId, String motifRefus) {
        if (motifRefus == null || motifRefus.trim().isEmpty())
            throw new AppException("Motif de refus obligatoire");
        DemandeAdhesion d = repo.lockById(demandeId)
                .orElseThrow(() -> new AppException("Demande introuvable"));
        ensureState(d, EN_ETUDE);
        d.setMotifRefus(motifRefus);
        d.setDateDecision(LocalDateTime.now());
        d.setStatut(type(REJETEE));
        d = repo.save(d);
        return demandeAdhesionMapper.mapTopDemandeAdhesionReadDTO(d);
    }

    @Override
    @Transactional
    public ReadDemandeAdhesionDTO annuler(Long demandeId) {
        DemandeAdhesion d = repo.lockById(demandeId)
                .orElseThrow(() -> new AppException("Demande introuvable"));
        String st = d.getStatut() == null ? null : d.getStatut().code;
        if (Objects.equals(st, APPROUVEE) || Objects.equals(st, REJETEE))
            throw new AppException("Une décision a déjà été prise");
        d.setStatut(type(ANNULEE));
        d = repo.save(d);
        return demandeAdhesionMapper.mapTopDemandeAdhesionReadDTO(d);
    }

    @Override
    public Page<ReadDemandeAdhesionDTO> search(String key, Long assoId, List<String> statutCodes, Pageable pageable) {
        key = StringUtils.stripAccentsToUpperCase(key);
        boolean hasStatus = statutCodes != null && !statutCodes.isEmpty();
        Pageable sorted = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.ASC, "dateSoumission"));
        return repo.search(key, assoId, statutCodes, hasStatus, sorted);
    }

    private void ensureState(DemandeAdhesion d, String expectedCode) {
        String st = d.getStatut() == null ? null : d.getStatut().code;
        if (!Objects.equals(st, expectedCode))
            throw new AppException("Statut attendu: " + expectedCode + ", actuel: " + st);
    }

    private void uploadDemandeDocuments(List<UploadDocReq> documents, Long demandeId) {
        if (documents == null || documents.isEmpty()) return;
        for (UploadDocReq doc : documents) {
            if (doc == null) continue;
            doc.setObjectTableName("DEMANDE_ADHESION");
            doc.setObjectId(demandeId);
            try {
                documentService.uploadDocument(doc);
            } catch (Exception e) {
                throw new AppException("Erreur lors de l'upload du document: " + e.getMessage());
            }
        }
    }

}
