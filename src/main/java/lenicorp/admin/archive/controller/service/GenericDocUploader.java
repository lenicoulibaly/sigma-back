package lenicorp.admin.archive.controller.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import lenicorp.admin.archive.model.dtos.request.UploadDocReq;
import lenicorp.admin.archive.model.entities.Document;

@Service
@Qualifier("generic")
@Order(1) @Primary
public class GenericDocUploader extends AbstractDocumentService
{
	@Override
	protected Document mapToDocument(UploadDocReq dto)
	{
		return docMapper.mapToDoc(dto);
	}
}
