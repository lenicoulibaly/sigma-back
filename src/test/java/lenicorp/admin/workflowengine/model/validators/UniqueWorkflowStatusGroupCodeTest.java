package lenicorp.admin.workflowengine.model.validators;

import lenicorp.admin.workflowengine.controller.repositories.WorkflowStatusGroupRepository;
import lenicorp.admin.workflowengine.model.dtos.WorkflowStatusGroupDTO;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UniqueWorkflowStatusGroupCodeTest {

    @Mock
    private WorkflowStatusGroupRepository repository;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private UniqueWorkflowStatusGroupCode annotation;

    @InjectMocks
    private UniqueWorkflowStatusGroupCode.Validator validator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(annotation.allowNull()).thenReturn(true);
        validator.initialize(annotation);
    }

    @Test
    void isValid_ShouldReturnTrue_WhenCodeDoesNotExist() {
        // Given
        String code = "NEW_CODE";
        when(repository.existsByCode(code.toUpperCase())).thenReturn(false);

        // When
        boolean result = validator.isValid(code, context);

        // Then
        assertTrue(result);
    }

    @Test
    void isValid_ShouldReturnTrue_WhenUpdatingWithSameCode() {
        // Given
        Long id = 1L;
        String code = "EXISTING_CODE";
        WorkflowStatusGroupDTO dto = new WorkflowStatusGroupDTO();
        dto.setId(id);
        dto.setCode(code);

        when(repository.existsByCodeIgnoreCaseAndIdNot(code, id)).thenReturn(false);

        // When
        boolean result = validator.isValid(dto, context);

        // Then
        assertTrue(result);
        verify(repository).existsByCodeIgnoreCaseAndIdNot(code, id);
    }

    @Test
    void isValid_ShouldReturnFalse_WhenUpdatingWithCodeUsedByOther() {
        // Given
        Long id = 1L;
        String code = "OTHER_CODE";
        WorkflowStatusGroupDTO dto = new WorkflowStatusGroupDTO();
        dto.setId(id);
        dto.setCode(code);

        when(repository.existsByCodeIgnoreCaseAndIdNot(code, id)).thenReturn(true);

        // When
        boolean result = validator.isValid(dto, context);

        // Then
        assertFalse(result);
        verify(repository).existsByCodeIgnoreCaseAndIdNot(code, id);
    }
}
