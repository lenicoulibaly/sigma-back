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

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {ValidFileSize.ValidFileSizeValidator.class})
@Documented
public @interface ValidFileSize
{
    String message() default "Fichier trop volumineux";
    Class<?> [] groups() default {};
    Class<? extends Payload> [] payload() default {};

    @Component
    @RequiredArgsConstructor
    class ValidFileSizeValidator implements ConstraintValidator<ValidFileSize, UploadDocReq>
    {
        @Autowired
        DocumentValidationRuleRepo documentValidationRuleRepo;

        @Value("${document.default.max.size:5242880}") // 5MB default
        Long defaultMaxSize;

        @Override
        public boolean isValid(UploadDocReq dto, ConstraintValidatorContext context)
        {
            if(dto.getFile() == null) return true;

            String docTypeCode = dto.getDocTypeCode();
            if(docTypeCode == null) return true;

            String extension = null;
            InputStream file = null;
            try
            {
                file = dto.getFile().getInputStream();
                extension = FileUtils.getExtensionFromInputStream(file);
            } catch (IOException e)
            {
                e.printStackTrace();
                throw new AppException(e.getMessage());
            }
            if(extension == null || extension.isEmpty()) return true;

            // Get max file size for this document type and extension
            Long maxSize = documentValidationRuleRepo.getMaxFileSizeForTypeAndExtension(docTypeCode, extension);

            // If no specific rule exists, use default max size
            if(maxSize == null) {
                maxSize = defaultMaxSize;
            }
            FileUtils.InputStreamDetails streamDetails = null;
            try
            {
                streamDetails = FileUtils.getInputStreamDetails(file);
            } catch (IOException e)
            {
                e.printStackTrace();
                throw new AppException(e.getMessage());
            }

            return streamDetails.getSize() <= maxSize;
        }
    }
}
