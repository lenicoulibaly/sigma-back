package lenicorp.metier.association.controller.services;

import jakarta.transaction.Transactional;
import lenicorp.admin.exceptions.AppException;
import lenicorp.admin.types.controller.repositories.TypeRepo;
import lenicorp.admin.types.model.entities.Type;
import lenicorp.admin.utilities.StringUtils;
import lenicorp.metier.association.controller.repositories.AssoRepo;
import lenicorp.metier.association.controller.repositories.PieceAdhesionRepo;
import lenicorp.metier.association.model.dtos.PieceAdhesionDTO;
import lenicorp.metier.association.model.entities.Association;
import lenicorp.metier.association.model.entities.PieceAdhesion;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PieceAdhesionService implements IPieceAdhesionService
{
    private final PieceAdhesionRepo pieceRepo;
    private final AssoRepo assoRepo;
    private final TypeRepo typeRepo;

    @Override
    @Transactional
    public PieceAdhesionDTO create(PieceAdhesionDTO dto)
    {
        if(dto == null) throw new AppException("Le corps de la requête est null");
        if(dto.getAssoId() == null) throw new AppException("L'association est obligatoire");
        if(dto.getTypePieceCode() == null) throw new AppException("Le type de pièce est obligatoire");
        if(dto.getStatutObligationCode() == null) throw new AppException("Le statut d'obligation est obligatoire");

        PieceAdhesion piece = new PieceAdhesion();
        piece.setAssociation(new Association(dto.getAssoId()));
        piece.setTypePiece(new Type(dto.getTypePieceCode()));
        piece.setStatutObligation(new Type(dto.getStatutObligationCode()));
        piece.setDescription(dto.getDescription());

        piece = pieceRepo.save(piece);
        return mapToDTO(piece);
    }

    @Override
    @Transactional
    public PieceAdhesionDTO update(PieceAdhesionDTO dto)
    {
        if(dto == null) throw new AppException("Le corps de la requête est null");
        if(dto.getPieceId() == null) throw new AppException("L'identifiant de la pièce est obligatoire");
        PieceAdhesion piece = pieceRepo.findById(dto.getPieceId()).orElseThrow(() -> new AppException("Pièce introuvable"));

        if(dto.getAssoId() != null) piece.setAssociation(new Association(dto.getAssoId()));
        if(dto.getTypePieceCode() != null) piece.setTypePiece(new Type(dto.getTypePieceCode()));
        if(dto.getStatutObligationCode() != null) piece.setStatutObligation(new Type(dto.getStatutObligationCode()));
        piece.setDescription(dto.getDescription());

        piece = pieceRepo.save(piece);
        return mapToDTO(piece);
    }

    @Override
    public Page<PieceAdhesionDTO> search(String key, Long assoId, Pageable pageable)
    {
        key = StringUtils.stripAccentsToUpperCase(key);
        return pieceRepo.search(key, assoId, pageable);
    }

    private PieceAdhesionDTO mapToDTO(PieceAdhesion piece)
    {
        PieceAdhesionDTO dto = new PieceAdhesionDTO();
        dto.setPieceId(piece.getPieceId());

        if(piece.getAssociation() != null)
        {
            Long assoId = piece.getAssociation().getAssoId();
            dto.setAssoId(assoId);
            dto.setAssoName(assoId == null ? null : assoRepo.findById(assoId).map(Association::getAssoName).orElse(null));
        }
        if(piece.getTypePiece() != null)
        {
            String code = piece.getTypePiece().code;
            dto.setTypePieceCode(code);
            dto.setTypePieceName(code == null ? null : typeRepo.findById(code).map(t -> t.name).orElse(null));
        }
        if(piece.getStatutObligation() != null)
        {
            String code = piece.getStatutObligation().code;
            dto.setStatutObligationCode(code);
            dto.setStatutObligationName(code == null ? null : typeRepo.findById(code).map(t -> t.name).orElse(null));
        }
        dto.setDescription(piece.getDescription());
        return dto;
    }
}
