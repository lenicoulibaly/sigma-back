package lenicorp.metier.association.controller.services;

import jakarta.transaction.Transactional;
import lenicorp.admin.archive.controller.repositories.DocumentRepository;
import lenicorp.admin.archive.model.dtos.response.ReadDocDTO;
import lenicorp.admin.exceptions.AppException;
import lenicorp.admin.security.controller.repositories.UserRepo;
import lenicorp.admin.security.model.entities.AppUser;
import lenicorp.admin.security.controller.services.specs.IUserService;
import lenicorp.admin.utilities.SelectOption;
import lenicorp.admin.utilities.StringUtils;
import lenicorp.metier.association.controller.repositories.AdhesionRepo;
import lenicorp.metier.association.model.dtos.AdhesionDTO;
import lenicorp.metier.association.model.entities.Adhesion;
import lenicorp.metier.association.model.entities.Association;
import lenicorp.metier.association.controller.repositories.DemandeAdhesionRepository;
import lenicorp.metier.association.model.entities.DemandeAdhesion;
import lenicorp.metier.association.model.entities.Section;
import lenicorp.metier.association.model.mappers.AdhesionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service @RequiredArgsConstructor @Validated
public class AdhesionService implements IAdhesionService
{
    private final AdhesionMapper adhesionMapper;
    private final AdhesionRepo adhesionRepo;
    private final UserRepo userRepo;
    private final DocumentRepository docRepo;
    private final DemandeAdhesionRepository demandeAdhesionRepo;
    private final IUserService userService; // may still be used elsewhere in future methods

    @Override
    @Transactional
    public Adhesion createUserAndAdhesion(AdhesionDTO dto)
    {
        if(userRepo.existsByEmail(dto.getEmail())) throw new AppException("Email déjà attribué " + dto.getEmail());
        if(userRepo.existsByTel(dto.getTel())) throw new AppException("N° téléphone déjà attribué " + dto.getTel());
        if(userRepo.existsByMatricule(dto.getMatricule())) throw new AppException("Matricule déjà attribué " + dto.getMatricule());
        if(dto.getAssoId() == null) throw new AppException("Veuillez sélectionner l'association");

        // Create user
        AppUser user = new AppUser();
        user.setEmail(dto.getEmail());
        user.setMatricule(dto.getMatricule());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setTel(dto.getTel());
        user = userRepo.save(user);

        // Create adhesion
        Adhesion adhesion = new Adhesion(null, new Association(dto.getAssoId()),
                dto.getSectionId() == null ? null : new Section(dto.getSectionId()),
                true, user.getUserId().toString());
        adhesion = adhesionRepo.save(adhesion);

        return adhesion;
    }



    @Override
    @Transactional
    public Adhesion updateMembre(AdhesionDTO dto)
    {
        Adhesion adhesion = adhesionRepo.findById(dto.getAdhesionId())
                .orElseThrow(() -> new AppException("Membre introuvable " + dto.getAdhesionId()));

        AppUser user = userRepo.findById(Long.valueOf(dto.getUserId()))
                .orElseThrow(() -> new AppException("Utilisateur introuvable " + dto.getUserId()));

        user.setLastName(dto.getLastName());
        user.setFirstName(dto.getFirstName());
        user.setMatricule(dto.getMatricule());
        user.setTel(dto.getTel());

        adhesion.setSection(new Section(dto.getSectionId()));
        adhesion = adhesionRepo.save(adhesion);

        return adhesion;
    }

    // Méthode migrée dans DemandeAdhesionService

    @Override
    @Transactional
    public void seDesabonner(Long adhesionId)
    {
        Adhesion adhesion = adhesionRepo.findById(adhesionId)
                .orElseThrow(() -> new AppException("Adhésion introuvable"));
        adhesion.setActive(false);
        adhesionRepo.save(adhesion);
    }

    @Override
    public Page<AdhesionDTO> searchAdhsions(String key, Long assoId, Long sectionId, Pageable pageable)
    {
        key = StringUtils.stripAccentsToUpperCase(key);
        return adhesionRepo.searchAdhesions(key, assoId, sectionId, pageable);
    }

    @Override
    public AdhesionDTO getMembreDTO(String username)
    {
        username = Optional.ofNullable(username).orElse("{#}");
        AppUser user = userRepo.findByUsername(username);
        if (user == null) {
            return null;
        }
        return adhesionMapper.mapToAdhesionDto(user);
    }

    @Override
    public List<SelectOption> getOptions(Long assoId)
    {
        if(assoId == null) return Collections.emptyList();
        List<Adhesion> adhesions = adhesionRepo.getAdhesionsByAssoId(assoId);
        if(adhesions == null || adhesions.isEmpty()) return Collections.emptyList();

        List<SelectOption> selectOptions = new ArrayList<>();
        for (Adhesion adhesion : adhesions) {
            AppUser user = userRepo.findById(Long.valueOf(adhesion.getUserId())).orElse(null);
            if (user != null) {
                selectOptions.add(new SelectOption(adhesion.getAdhesionId(), user.getFirstName() + " " + user.getLastName()));
            }
        }
        return selectOptions;
    }

    @Override
    public Optional<Adhesion> findByEmailAndSection(String email, Long sectionId)
    {
        AppUser user = userRepo.findByUsername(email);
        if (user == null) {
            return Optional.empty();
        }
        return adhesionRepo.findByUserIdAndSectionId(user.getUserId().toString(), sectionId);
    }

    @Override
    public Optional<Adhesion> findByEmailAndAsso(String email, Long assoId)
    {
        AppUser user = userRepo.findByUsername(email);
        if (user == null) {
            return Optional.empty();
        }
        return adhesionRepo.findByUserIdAndAsso(user.getUserId().toString(), assoId);
    }

    @Override
    public Page<ReadDocDTO> searchObjectDocs(Long objectId, String key, Pageable pageable)
    {
        return docRepo.searchObjectDocs(objectId, "ADHESION", key, pageable);
    }

    @Override
    @Transactional
    public void createAdhesionFromDemande(Long demandeId)
    {
        DemandeAdhesion demande = demandeAdhesionRepo.findById(demandeId)
                .orElseThrow(() -> new AppException("Demande d'adhésion introuvable"));

        if (!"VALIDE".equals(demande.getStatut().code))
        {
            throw new AppException("La demande d'adhésion n'est pas validée");
        }

        if (demande.getAdhesionCreee() != null)
        {
            return; // Idempotence : l'adhésion existe déjà
        }

        Adhesion adhesion = new Adhesion();
        adhesion.setAssociation(demande.getAssociation());
        adhesion.setSection(demande.getSection());
        adhesion.setUserId(demande.getDemandeur().getUserId().toString());
        adhesion.setActive(true);

        adhesion = adhesionRepo.save(adhesion);

        demande.setAdhesionCreee(adhesion);
        demandeAdhesionRepo.save(demande);
    }
}
