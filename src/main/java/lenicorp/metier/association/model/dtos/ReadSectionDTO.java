package lenicorp.metier.association.model.dtos;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ReadSectionDTO
{
    private Long sectionId;
    private String sectionName;
    private String situationGeo;
    private String sigle;
    private Long assoId;
    private String assoName;
    private String strName;
    private String strSigle;
    private String id;
    private String label;
    private String email;
    private String tel;
    private String adresse;

    public ReadSectionDTO(Long sectionId, String sectionName, String situationGeo, String sigle, Long assoId, String assoName) {
        this.sectionId = sectionId;
        this.sectionName = sectionName;
        this.situationGeo = situationGeo;
        this.sigle = sigle;
        this.assoId = assoId;
        this.assoName = assoName;
        this.id = String.valueOf(sectionId);
        this.label = sectionName;
    }
}
