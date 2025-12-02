package lenicorp.admin.structures.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ReadStrDTO
{
    private Long strId;
    private String strName;
    private String strSigle;

    private String strTypeName;
    private String strTypeCode;
    private String strTel;
    private String strAddress;
    private String situationGeo;

    private Long parentId;
    private String parentName;
    private String parentSigle;
    private Long strLevel;

    private Long respoId;
    private String respoName;
    private String respoMatricule;

    private String chaineSigles;

    public ReadStrDTO(Long strId, String strName, String strTypeName, String strSigle, String strTypeCode
            , String strTel, String strAddress, String situationGeo, Long parentId, String parentName
            , String parentSigle, Long strLevel, String chaineSigles)
    {
        this.strId = strId;
        this.strName = strName;
        this.strTypeName = strTypeName;
        this.strSigle = strSigle;
        this.strTypeCode = strTypeCode;
        this.strTel = strTel;
        this.strAddress = strAddress;
        this.situationGeo = situationGeo;
        this.parentId = parentId;
        this.parentName = parentName;
        this.parentSigle = parentSigle;
        this.strLevel = strLevel;
        this.chaineSigles = chaineSigles;
    }

    public ReadStrDTO(Long strId, String strName, String strSigle, String hierarchySigles)
    {
        this.strId = strId;
        this.strName = strName;
        this.strSigle = strSigle;
        this.chaineSigles = hierarchySigles;
    }

    @Override
    public String toString()
    {
        return this.strName + (this.strSigle == null ? "" : " ("+this.strSigle + ")");
    }
}