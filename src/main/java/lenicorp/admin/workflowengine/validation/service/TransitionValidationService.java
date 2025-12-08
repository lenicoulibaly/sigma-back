package lenicorp.admin.workflowengine.validation.service;

import java.util.List;
import java.util.Map;

public interface TransitionValidationService {

    record Violation(String messageCode, Map<String, Object> params) {}
    record Result(boolean valid, List<Violation> violations) {}

    Result validate(String transitionPrivilegeCode,
                    String objectTypeCode,
                    Long objectId,
                    String comment,
                    List<String> uploadedDocTypeCodes);
}
