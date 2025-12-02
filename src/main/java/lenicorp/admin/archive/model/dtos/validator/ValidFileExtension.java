package lenicorp.admin.archive.model.dtos.validator;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import lenicorp.admin.archive.controller.repositories.DocumentValidationRuleRepo;
import lenicorp.admin.archive.model.dtos.request.UploadDocReq;
import lenicorp.admin.archive.model.utils.FileUtils;
import lenicorp.admin.exceptions.AppException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {
        ValidFileExtension.ValidFileExtensionValidatorOnDTO.class
        , ValidFileExtension.ValidFileExtensionValidatorOnBase64Url.class})
@Documented
public @interface ValidFileExtension
{
    String message() default "Type de fichier non pris en charge";
    Class<?> [] groups() default {};
    Class<? extends Payload> [] payload() default {};

    @Component
    @RequiredArgsConstructor
    class ValidFileExtensionValidatorOnBase64Url implements ConstraintValidator<ValidFileExtension, String>
    {
        @Autowired
        DocumentValidationRuleRepo documentValidationRuleRepo;

        @Value("${document.default.type:DOC}")
        String defaultDocType;

        @Override
        public boolean isValid(String extension, ConstraintValidatorContext context)
        {
            if(extension == null || extension.isEmpty()) return true;

            // Use default document type for validation
            return documentValidationRuleRepo.isExtensionAllowedForType(defaultDocType, extension);
        }
    }

    @Component
    @RequiredArgsConstructor
    class ValidFileExtensionValidatorOnDTO implements ConstraintValidator<ValidFileExtension, UploadDocReq>
    {
        @Autowired
        DocumentValidationRuleRepo documentValidationRuleRepo;

        @Override
        public boolean isValid(UploadDocReq dto, ConstraintValidatorContext context)
        {
            if(dto.getFile() == null) return true;

            String extension;
            MultipartFile file = dto.getFile();
            extension = FileUtils.getExtensionFromMimeType(file.getContentType());
            if(dto.getDocTypeCode() == null) return true;

            return documentValidationRuleRepo.isExtensionAllowedForType(dto.getDocTypeCode(), extension);
        }
    }
}
