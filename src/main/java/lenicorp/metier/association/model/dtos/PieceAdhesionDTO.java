package lenicorp.metier.association.model.dtos;

import jakarta.persistence.*;
import lenicorp.admin.types.model.entities.Type;
import lenicorp.metier.association.model.entities.Association;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class PieceAdhesionDTO
{
    private Long pieceId;
    private String typePieceCode;
    private String typePieceName;
    private Long assoId;
    private String assoName;
    private String statutObligationCode;
    private String statutObligationName;
    private String description;
}
