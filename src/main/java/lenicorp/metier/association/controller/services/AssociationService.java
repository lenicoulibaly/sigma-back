package lenicorp.metier.association.controller.services;

import jakarta.transaction.Transactional;
import lenicorp.admin.archive.controller.repositories.DocumentRepository;
import lenicorp.admin.archive.controller.service.IDocumentService;
import lenicorp.admin.archive.model.dtos.request.UploadDocReq;
import lenicorp.admin.archive.model.dtos.response.ReadDocDTO;
import lenicorp.admin.archive.model.entities.Document;
import lenicorp.admin.exceptions.AppException;
import lenicorp.admin.utilities.StringUtils;
import lenicorp.metier.association.controller.repositories.AssoRepo;
import lenicorp.metier.association.controller.repositories.AssociationStructureRepo;
import lenicorp.metier.association.model.dtos.CreateAssociationDTO;
import lenicorp.metier.association.model.dtos.CreateSectionDTO;
import lenicorp.metier.association.model.dtos.ReadAssociationDTO;
import lenicorp.metier.association.model.dtos.UpdateAssociationDTO;
import lenicorp.metier.association.model.dtos.PieceAdhesionDTO;
import lenicorp.metier.association.model.entities.Association;
import lenicorp.metier.association.model.entities.AssociationStructure;
import lenicorp.metier.association.model.mappers.AssoMapper;
import lenicorp.admin.structures.controller.repositories.StrRepo;
import lenicorp.admin.structures.model.entities.Structure;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AssociationService implements IAssociationService
{
    private final AssoRepo assoRepo;
    private final AssoMapper assoMapper;
    private final ISectionService sectionService;
    private final DocumentRepository docRepo;
    private final IDocumentService docService;
    private final IPieceAdhesionService pieceAdhesionService;
    private final AssociationStructureRepo associationStructureRepo;
    private final StrRepo strRepo;

    @Override
    @Transactional
    public Association createAssociation(CreateAssociationDTO dto) throws IOException
    {
        Association association = assoMapper.mapToAssociation(dto);
        association = assoRepo.save(association);
        Long assoId = association.getAssoId();

        List<CreateSectionDTO> createSectionDTOS = dto.getCreateSectionDTOS();

        if (createSectionDTOS == null || createSectionDTOS.isEmpty())
        {
            sectionService.createSectionDeBase(association);
        } else
        {
            createSectionDTOS.stream()
                    .filter(Objects::nonNull)
                    .forEach(createSectionDTO ->
                    {
                        createSectionDTO.setAssoId(assoId);
                        sectionService.createSection(createSectionDTO);
                    });
        }
        // Créer les pièces à fournir
        List<PieceAdhesionDTO> pieces = dto.getPiecesAFournir();
        if(pieces != null && !pieces.isEmpty())
        {
            pieces.stream()
                    .filter(Objects::nonNull)
                    .forEach(p -> {
                        p.setAssoId(assoId);
                        pieceAdhesionService.create(p);
                    });
        }

        if(dto.getLogo() != null)
        {
            UploadDocReq docReq = new UploadDocReq(assoId, "LOGO", null, "Logo", "Logo de l'association", dto.getLogo(), "ASSOCIATION", null, null);
            docService.uploadDocument(docReq);
        }

        // Lier l'association aux structures passées dans dto.strIds
        List<Long> strIds = dto.getStrIds();
        if (strIds != null && !strIds.isEmpty()) {
            strIds.stream()
                    .filter(Objects::nonNull)
                    .distinct()
                    .forEach(strId -> {
                        // Vérifier l'existence de la structure
                        if (!strRepo.existsById(strId)) {
                            throw new AppException("Structure introuvable: " + strId);
                        }
                        // Éviter les doublons
                        boolean exists = associationStructureRepo.existsByAssoIdAndStrId(assoId, strId);
                        if (!exists) {
                            AssociationStructure link = new AssociationStructure(new Association(assoId), new Structure(strId));
                            associationStructureRepo.save(link);
                        }
                    });
        }
        return association;
    }

    @Override @Transactional
    public Association updateAssociation(UpdateAssociationDTO dto)
    {
        Association association = assoRepo.findById(dto.getAssoId()).orElseThrow(()->new AppException("Association introuvable"));
        if (association == null) {
            throw new AppException("Association introuvable");
        }

        association.setAssoName(dto.getAssoName());
        association.setSigle(dto.getSigle());
        association.setDroitAdhesion(dto.getDroitAdhesion());
        association.setSituationGeo(dto.getSituationGeo());
        return association;
    }

    @Override
    public Page<ReadAssociationDTO> searchAssociations(String key, PageRequest pageRequest)
    {
        key = "%" + StringUtils.stripAccentsToUpperCase(key) + "%";
        //return assoRepo.searchAssociations(key, pageRequest);

        // The following code is commented out because the document-related services are not yet migrated to Quarkus

        Page<ReadAssociationDTO> assoPage = assoRepo.searchAssociations(key, pageRequest);
        List<ReadAssociationDTO> assoList = assoPage.getContent();
        assoList.forEach(a->
        {
            byte[] logo = this.getAssoLogo(a.getAssoId());
            a.setLogo(logo);
        });
        return new PageImpl<>(assoList, pageRequest, assoPage.getTotalElements());
    }

    @Override
    public List<ReadAssociationDTO> searchAssociationsList(String key)
    {
        key = "%" +StringUtils.stripAccentsToUpperCase(key) + "%";
        return assoRepo.searchAssociationsList(key);
    }

    @Override
    public byte[] getAssoLogo(Long assoId)
    {
        List<byte[]>logos  = docService.getFileByObjectIdAndTableNameAndTypeCode(assoId, "ASSOCIATION", "LOGO");
        byte[] logo = logos.isEmpty() ? null : logos.get(0);
        return logo;
    }
    @Override
    public ReadAssociationDTO findById(Long assoId)
    {
        if(assoId == null) throw new AppException("L'ID de l'association ne peut être nul");
        ReadAssociationDTO dto = assoRepo.findReadAssoDtoById(assoId);
        List<PieceAdhesionDTO> piecesAdhesion = pieceAdhesionService.search("", assoId, PageRequest.of(0, 100)).toList();
        dto.setPiecesAFournir(piecesAdhesion);
        List<byte[]>logos = docService.getFileByObjectIdAndTableNameAndTypeCode(assoId, "ASSOCIATION", "LOGO");
        byte[] logo = logos.isEmpty() ? null : logos.get(0);
        dto.setLogo(logo);
        return dto;
    }

    @Override
    public Association createAssociation(CreateAssociationDTO dto, File logo) throws IOException
    {
        // Simple implementation that ignores the logo for now
        // This will need to be updated once the document-related services are migrated to Quarkus
        return this.createAssociation(dto);

        /*
        Association association = this.createAssociation(dto);
        // Code to handle the logo file would go here
        return association;
        */
    }

    @Override
    public byte[] generateFicheAdhesion(Long assoId) throws Exception {
        // This is a stub implementation that will need to be updated
        // once the report-related services are migrated to Quarkus
        throw new AppException("Cette fonctionnalité n'est pas encore disponible");

        /*
        Map<String, Object> parameters = new HashMap<>();

        String assoSigle = assoRepo.getSigleByAssoId(assoId);
        String qrText = "Fiche d'adhésion " + assoSigle;
        parameters.put("ASSO_ID", assoId);

        ReadDocDTO logoDoc = docRepo.getAssoLogo(assoId);
        if(logoDoc != null)
        {
            InputStream logo = resourceLoader.getLocalImages(logoDoc.getDocPath());
            parameters.put("LOGO", logo);
        }

        return reportService.generateReport("FicheAdhesion.jrxml", parameters, Collections.EMPTY_LIST, qrText);
        */
    }
}
