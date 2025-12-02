package lenicorp.metier.association.model.entities;

import jakarta.persistence.*;
import lenicorp.admin.types.model.entities.Type;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Audited
@Entity
public class PieceAdhesion
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ADHESION_ID_GEN")
    @SequenceGenerator(name = "ADHESION_ID_GEN", sequenceName = "ADHESION_ID_GEN", allocationSize = 10)
    private Long pieceId;
    @ManyToOne @JoinColumn(name = "TYPE_PIECE_CODE")
    private Type typePiece;
    @ManyToOne @JoinColumn(name = "ASSO_ID")
    private Association association;
    @ManyToOne @JoinColumn(name = "STA_OBLIGATION_CODE")
    private Type statutObligation;
    private String description;
}
