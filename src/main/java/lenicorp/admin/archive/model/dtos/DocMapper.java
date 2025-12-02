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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class DocMapper
{
    @Autowired
    protected TypeRepo typeRepo;

    protected Type mapDocType(String typeCode)
    {
        return typeRepo.findById(typeCode).orElseThrow(() -> new AppException("Type de document inconnu"));
    }

    protected byte[] mapToBytes(MultipartFile file)
    {
        if (file == null) return null;
        try
        {
            return file.getBytes();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    protected AppUser mapToUser(Long userId)
    {
        return new AppUser(userId);
    }


    @Mapping(target = "docType", expression = "java(mapDocType(dto.getDocTypeCode()))")
    @Mapping(target = "file", expression = "java(mapToBytes(dto.getFile()))")
    public abstract Document mapToDocument(UploadDocReq dto);
}
