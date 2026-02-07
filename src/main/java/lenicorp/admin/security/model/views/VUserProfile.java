package lenicorp.admin.security.model.views;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "v_user_profile") @Immutable
public class VUserProfile
{
    @Id
    private Long rowNum;
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private Boolean activated;
    private Long userStrId;
    private String tel;
    private String profileCode;
    private String profileName;
    private String profileTypeCode;
    private String profileTypeName;
    private String assStatusCode;
    private String profileDescription;
    private Long assStrId;
    private String assStrName;
    private String assStrSigles;
    private String assStrChaineSigles;
    private Long profileMaxAssignation;
    private String userProfileAssTypeCode;
    private String userProfileAssTypeName;

    @Size(max = 255) @Column(name = "libelle")
    private String libelle;
    @Column(name = "ass_id")
    private Long assId;
    @Column(name = "starting_date")
    private LocalDate startingDate;
    @Column(name = "ending_date")
    private LocalDate endingDate;
    @Size(max = 255)
    @Column(name = "association_type")
    private String associationType;

    @Size(max = 100)
    @Column(name = "ass_status_name", length = 100)
    private String assStatusName;

    @Column(name = "ordre")
    private Integer ordre;

    @Column(name = "asso_id")
    private Long assoId;

    @Column(name = "asso_name")
    private String assoName;

    @Column(name = "section_id")
    private Long sectionId;

    @Column(name = "section_name")
    private String sectionName;
}
