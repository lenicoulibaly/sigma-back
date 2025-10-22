package lenicorp.admin.archive.model.dtos;

import lenicorp.admin.archive.model.dtos.request.UploadDocReq;
import lenicorp.admin.archive.model.entities.Document;
import lenicorp.admin.exceptions.AppException;
import lenicorp.admin.security.model.entities.AppUser;
import lenicorp.admin.types.controller.repositories.TypeRepo;
import lenicorp.admin.types.model.entities.Type;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class DocMapper
{
    @Autowired
    protected TypeRepo typeRepo;

    protected Type mapDocType(String typeCode)
    {
        return typeRepo.findById(typeCode).orElseThrow(() -> new AppException("Type de document inconnu"));
    }

    protected AppUser mapToUser(Long userId)
    {
        return new AppUser(userId);
    }

    @Mapping(target = "docDescription", expression = "java(\"Photo de profil\")")
    @Mapping(target = "docType", expression = "java(mapDocType(dto.getDocTypeCode()))")
    @Mapping(target = "user", expression = "java(mapToUser(dto.getObjectId()))")
    public abstract Document mapToPhotoDoc(UploadDocReq dto);

    @Mapping(target = "docType", expression = "java(mapDocType(dto.getDocTypeCode()))")
    //@Mapping(target = "association", expression = "java(mapToAssociation(dto.getObjectId()))")
    public abstract Document mapToAssociationDoc(UploadDocReq dto);

    @Mapping(target = "docType", expression = "java(mapDocType(dto.getDocTypeCode()))")
    //@Mapping(target = "section", expression = "java(mapToSection(dto.getObjectId()))")
    public abstract Document mapToSectionDoc(UploadDocReq dto);

    @Mapping(target = "docType", expression = "java(mapDocType(dto.getDocTypeCode()))")
    @Mapping(target = "user", expression = "java(mapToUser(dto.getObjectId()))")
    public abstract Document mapToMembreDoc(UploadDocReq dto);

    @Mapping(target = "docType", expression = "java(mapDocType(dto.getDocTypeCode()))")
    public abstract Document mapToDoc(UploadDocReq dto);
}
