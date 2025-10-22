package lenicorp.admin.archive.controller.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import lenicorp.admin.archive.model.dtos.request.UploadDocReq;
import lenicorp.admin.archive.model.entities.Document;

@Service
@Qualifier("membre")
public class MembreDocUploader extends AbstractDocumentService
{
	@Override
	protected Document mapToDocument(UploadDocReq dto) {
		return docMapper.mapToMembreDoc(dto);
	}
}
