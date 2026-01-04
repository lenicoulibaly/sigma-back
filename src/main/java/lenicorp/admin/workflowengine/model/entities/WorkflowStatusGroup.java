package lenicorp.admin.workflowengine.model.entities;

import lenicorp.admin.security.model.entities.AppAuthority;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "workflow_status_group")
@Data @NoArgsConstructor @AllArgsConstructor
public class WorkflowStatusGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WF_STA_GRP_ID_GEN")
    @SequenceGenerator(name = "WF_STA_GRP_ID_GEN", sequenceName = "WF_STA_GRP_ID_GEN", allocationSize = 10)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    private String description;

    private String color;

    @ManyToMany(mappedBy = "groups")
    private List<WorkflowStatus> statuses;

    @ManyToMany
    @JoinTable(
            name = "wf_status_group_authorities",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "authority_code")
    )
    private List<AppAuthority> authorities;
}
