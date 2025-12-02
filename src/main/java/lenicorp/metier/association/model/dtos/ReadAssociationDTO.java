package lenicorp.metier.association.model.dtos;

import lenicorp.admin.types.model.dtos.TypeDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ReadAssociationDTO
{
    private Long assoId;
    private String assoName;
    private String situationGeo;
    private String sigle;
    private BigDecimal droitAdhesion;
    private String email;
    private String tel;
    private String adresse;
    private byte[] logo;
    private Long nbrMembres;
    private String structures;
    private String conditionsAdhesion;
    private List<PieceAdhesionDTO> piecesAFournir;

    public ReadAssociationDTO(Long assoId, String assoName, String situationGeo, String sigle, BigDecimal droitAdhesion, String email, String tel, String adresse, String conditionsAdhesion)
    {
        this.assoId = assoId;
        this.assoName = assoName;
        this.situationGeo = situationGeo;
        this.sigle = sigle;
        this.droitAdhesion = droitAdhesion;
        this.email = email;
        this.tel = tel;
        this.adresse = adresse;
        this.conditionsAdhesion = conditionsAdhesion;
    }

    public ReadAssociationDTO(Long assoId, String assoName, String situationGeo, String sigle, BigDecimal droitAdhesion)
    {
        this.assoId = assoId;
        this.assoName = assoName;
        this.situationGeo = situationGeo;
        this.sigle = sigle;
        this.droitAdhesion = droitAdhesion;
    }
}
