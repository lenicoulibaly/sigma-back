package lenicorp.metier.association.model.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lenicorp.admin.archive.controller.service.DocumentService;
import lenicorp.admin.archive.model.dtos.request.UploadDocReq;
import lenicorp.admin.types.model.dtos.TypeDTO;
import lenicorp.metier.association.model.validators.UniqueAssoName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@NotNull(message = "Aucune donnée parvenue")
public class CreateAssociationDTO
{
    @NotNull(message = "Le nom de l'association est obligatoire")
    @NotBlank(message = "Le nom de l'association est obligatoire")
    @UniqueAssoName
    private String assoName;
    private String situationGeo;
    private String sigle;
    private BigDecimal droitAdhesion;
    private MultipartFile logo;
    private String email;
    private String tel;
    private String adresse;
    private List<Long> strIds;
    private List<UploadDocReq> piecesJointes;
    private List<CreateSectionDTO> createSectionDTOS;
    private List<PieceAdhesionDTO> piecesAFournir;
    private String conditionsAdhesion;
}