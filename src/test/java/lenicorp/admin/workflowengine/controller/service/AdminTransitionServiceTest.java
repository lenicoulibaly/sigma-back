package lenicorp.admin.workflowengine.controller.service;

import lenicorp.admin.security.controller.repositories.AuthorityRepo;
import lenicorp.admin.workflowengine.controller.repositories.TransitionRepository;
import lenicorp.admin.workflowengine.controller.repositories.TransitionRuleRepository;
import lenicorp.admin.workflowengine.controller.repositories.WorkflowRepository;
import lenicorp.admin.workflowengine.engine.rules.RuleEvaluationService;
import lenicorp.admin.workflowengine.model.dtos.TransitionDTO;
import lenicorp.admin.workflowengine.model.dtos.mapper.TransitionMapper;
import lenicorp.admin.workflowengine.model.entities.Transition;
import lenicorp.admin.workflowengine.model.entities.TransitionValidationConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminTransitionServiceTest {

    @Mock
    private TransitionRepository transitionRepo;
    @Mock
    private TransitionRuleRepository ruleRepo;
    @Mock
    private WorkflowRepository workflowRepo;
    @Mock
    private RuleEvaluationService ruleEngine;
    @Mock
    private TransitionMapper mapper;
    @Mock
    private AuthorityRepo authorityRepo;

    @InjectMocks
    private AdminTransitionServiceImpl adminTransitionService;

    private TransitionDTO transitionDTO;

    @BeforeEach
    void setUp() {
        transitionDTO = new TransitionDTO();
        transitionDTO.setPrivilegeCode("TEST_PRIV");
        transitionDTO.setLibelle("Test Transition");
    }

    @Test
    void create_WithInvalidPrivilege_ShouldThrowException() {
        when(authorityRepo.existsByCodeAndType("TEST_PRIV", "PRV")).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            adminTransitionService.create(transitionDTO);
        });

        assertEquals("Le code privilège TEST_PRIV est invalide ou n'est pas de type PRV", exception.getMessage());
        verify(transitionRepo, never()).save(any());
    }

    @Test
    void create_WithNullPrivilege_ShouldSucceed() {
        transitionDTO.setPrivilegeCode(null);
        when(mapper.toEntity(any())).thenReturn(new Transition());
        when(transitionRepo.save(any())).thenReturn(new Transition());

        adminTransitionService.create(transitionDTO);

        verify(authorityRepo, never()).existsByCodeAndType(anyString(), anyString());
        verify(transitionRepo, times(1)).save(any());
    }

    @Test
    void create_WithValidPrivilege_ShouldSucceed() {
        when(authorityRepo.existsByCodeAndType("TEST_PRIV", "PRV")).thenReturn(true);
        when(mapper.toEntity(any())).thenReturn(new Transition());
        when(transitionRepo.save(any())).thenReturn(new Transition());

        adminTransitionService.create(transitionDTO);

        verify(transitionRepo, times(1)).save(any());
    }

    @Test
    void update_WithInvalidPrivilege_ShouldThrowException() {
        when(authorityRepo.existsByCodeAndType("TEST_PRIV", "PRV")).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            adminTransitionService.update(1L, transitionDTO);
        });

        assertEquals("Le code privilège TEST_PRIV est invalide ou n'est pas de type PRV", exception.getMessage());
        verify(transitionRepo, never()).save(any());
    }

    @Test
    void create_WithValidationConfig_ShouldSetTransitionOnConfig() {
        transitionDTO.setCommentRequired(true);
        Transition transition = new Transition();
        TransitionValidationConfig config = new TransitionValidationConfig();
        transition.setValidationConfig(config);

        when(authorityRepo.existsByCodeAndType("TEST_PRIV", "PRV")).thenReturn(true);
        when(mapper.toEntity(transitionDTO)).thenReturn(transition);
        when(transitionRepo.save(any())).thenReturn(transition);

        adminTransitionService.create(transitionDTO);

        assertEquals(transition, config.getTransition());
        verify(transitionRepo).save(transition);
    }

    @Test
    void update_WithValidationConfig_ShouldSetTransitionOnConfig() {
        transitionDTO.setCommentRequired(true);
        Transition transition = new Transition();
        TransitionValidationConfig config = new TransitionValidationConfig();
        transition.setValidationConfig(config);

        when(authorityRepo.existsByCodeAndType(anyString(), anyString())).thenReturn(true);
        when(transitionRepo.findById(1L)).thenReturn(Optional.of(transition));
        when(transitionRepo.save(any())).thenReturn(transition);
        when(mapper.toDto(any())).thenReturn(new TransitionDTO());

        adminTransitionService.update(1L, transitionDTO);

        assertEquals(transition, config.getTransition());
        verify(transitionRepo).save(transition);
    }
}
