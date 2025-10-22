package lenicorp.admin.security.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Entity
public class Menu
{
    @Id
    private String menuCode;
    @Column(unique = true)
    private String name;
    @Column(length=4000)
    private String privilegeCodesChain;
    @Transient
    private List<String> privilegeCodes;
    @Transient
    public static final String chainSeparator = "::";

    public List<String> getPrivilegeCodes()
    {
        if(this.privilegeCodesChain == null) return new ArrayList<>();
        return Arrays.asList(this.privilegeCodesChain.split(Menu.chainSeparator));
    }

    public Menu(String menuCode, String name, String prvsCodesChain)
    {
        this.menuCode = menuCode;
        this.name = name;
        this.privilegeCodesChain = prvsCodesChain;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public class MenuResp
    {
        private String menuCode;
        private String name;
    }
}
