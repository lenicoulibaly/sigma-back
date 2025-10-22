package lenicorp.admin.archive.controller.service;

import lenicorp.admin.exceptions.AppException;
import org.springframework.stereotype.Service;
import lenicorp.admin.types.controller.repositories.TypeRepo;
import lenicorp.admin.types.model.entities.Type;
import lombok.RequiredArgsConstructor;

@Service @RequiredArgsConstructor
public class DocServiceProvider
{
    private final AssociationDocUploader assDocUploader;
    private final SectionDocUploader sectionDocUploader;
    private final MembreDocUploader membreDocUploader;
    private final PhotoDocUploader photoDocUploader;
    private final TypeRepo typeRepo;

    public AbstractDocumentService getDocUploader(String typeDocUniqueCode)
    {
        if(typeDocUniqueCode == null) throw new AppException("Le type de document ne peut être null");
        Type typeDoc = typeRepo.findById(typeDocUniqueCode.toUpperCase()).orElseThrow(()->new AppException("Type de document inconnu"));
        if(typeDoc == null || !"DOC".equals(typeDoc.typeGroup.groupCode)) throw new AppException("Ce type de document n'est pas pris en charge par le système");

        AbstractDocumentService uploader = switch (typeDoc.code)
                {
                    case "PHT"->photoDocUploader;
                    case "DOC_MBR"->membreDocUploader;
                    case "DOC_ASS"->assDocUploader;
                    case "DOC_SEC"->sectionDocUploader;
                    default -> null;
                };

        if(uploader == null) throw new AppException("Ce type de document n'est pas pris en charge par le système");
        return uploader;
    }
}
