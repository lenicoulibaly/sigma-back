package lenicorp.admin.workflowengine.execution.archive;

import lenicorp.admin.workflowengine.execution.dto.AttachmentRef;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ArchiveGateway {
    /**
     * Persist the given files using the archive module and return references.
     * @param files files to persist (may be null/empty)
     * @param fileTypeCodes aligned list of type codes for each file (may be null/empty)
     * @param objectId the source object id the documents should be attached to
     * @param objectTableName type/table name code of the source object
     */
    List<AttachmentRef> saveAll(List<MultipartFile> files, List<String> fileTypeCodes, Long objectId, String objectTableName);
}
