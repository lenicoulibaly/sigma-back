package lenicorp.admin.utilities;

import jakarta.validation.ConstraintValidatorContext;
import lenicorp.admin.workflowengine.controller.repositories.TransitionSideEffectRepository;
import lenicorp.admin.workflowengine.model.dtos.TransitionSideEffectDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UniqueSideEffectNameValidatorTest {

    @Mock
    private TransitionSideEffectRepository repository;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext nodeBuilder;

    @InjectMocks
    private UniqueSideEffectNameValidator validator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(context.getDefaultConstraintMessageTemplate()).thenReturn("Un effet de bord avec ce nom existe déjà");
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        when(violationBuilder.addPropertyNode(anyString())).thenReturn(nodeBuilder);
        when(nodeBuilder.addConstraintViolation()).thenReturn(context);
    }

    @Test
    void isValid_WhenDtoIsNull_ShouldReturnTrue() {
        assertTrue(validator.isValid(null, context));
    }

    @Test
    void isValid_WhenCreationAndNameExists_ShouldReturnFalse() {
        TransitionSideEffectDTO dto = new TransitionSideEffectDTO();
        dto.setName("Duplicate Name");
        dto.setTransitionId(1L);
        dto.setId(null);

        when(repository.existsByTransitionTransitionIdAndName(1L, "Duplicate Name")).thenReturn(true);

        assertFalse(validator.isValid(dto, context));
        verify(context).disableDefaultConstraintViolation();
    }

    @Test
    void isValid_WhenCreationAndNameDoesNotExist_ShouldReturnTrue() {
        TransitionSideEffectDTO dto = new TransitionSideEffectDTO();
        dto.setName("Unique Name");
        dto.setTransitionId(1L);
        dto.setId(null);

        when(repository.existsByTransitionTransitionIdAndName(1L, "Unique Name")).thenReturn(false);

        assertTrue(validator.isValid(dto, context));
    }

    @Test
    void isValid_WhenUpdateAndNameExistsForOtherId_ShouldReturnFalse() {
        TransitionSideEffectDTO dto = new TransitionSideEffectDTO();
        dto.setId(2L);
        dto.setName("Existing Name");
        dto.setTransitionId(1L);

        when(repository.existsByTransitionTransitionIdAndNameAndIdNot(1L, "Existing Name", 2L)).thenReturn(true);

        assertFalse(validator.isValid(dto, context));
        verify(context).disableDefaultConstraintViolation();
    }

    @Test
    void isValid_WhenUpdateAndNameExistsForSameId_ShouldReturnTrue() {
        TransitionSideEffectDTO dto = new TransitionSideEffectDTO();
        dto.setId(2L);
        dto.setName("Current Name");
        dto.setTransitionId(1L);

        when(repository.existsByTransitionTransitionIdAndNameAndIdNot(1L, "Current Name", 2L)).thenReturn(false);

        assertTrue(validator.isValid(dto, context));
    }
}
