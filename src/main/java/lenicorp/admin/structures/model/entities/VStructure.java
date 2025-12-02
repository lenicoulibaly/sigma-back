package lenicorp.admin.structures.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

@Entity
@Table(name = "v_structure")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@DynamicUpdate
@DynamicInsert @Immutable // la vue ne sera pas modifiée
public class VStructure
{

    @Id
    @EqualsAndHashCode.Include
    private Long strId;

    private String strSigle;

    private String strName;

    private String strTypeCode;

    private String strTypeName;

    private String situationGeo;

    private String strAddress;

    private String strTel;

    private String chaineSigles;

    @Column(name = "profondeur")
    private Long strLevel;

    private Long parentId;

    private String parentName;

    private String parentSigle;

    @ToString.Exclude
    @Column(name = "search_text", length = Integer.MAX_VALUE)
    private String searchText;

    // Méthodes utilitaires existantes
    public String[] getHierarchyLevels()
    {
        return chaineSigles != null ? chaineSigles.split("/") : new String[0];
    }

    public String getParentChain()
    {
        if (chaineSigles != null && chaineSigles.contains("/"))
        {
            int lastSlash = chaineSigles.lastIndexOf("/");
            return chaineSigles.substring(0, lastSlash);
        }
        return null;
    }

    public boolean isRoot()
    {
        return strLevel != null && strLevel == 0;
    }

    public long getHierarchyDepth()
    {
        return strLevel != null ? strLevel : 0;
    }

    public String getDirectParentSigle()
    {
        return parentSigle; // Maintenant directement disponible
    }

    public boolean hasParent()
    {
        return parentId != null;
    }

    public String getFullHierarchyDisplay()
    {
        return chaineSigles != null ? chaineSigles.replace("/", " > ") : strSigle;
    }

}