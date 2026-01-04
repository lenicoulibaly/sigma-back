package lenicorp.admin.workflowengine.controller.web;

import jakarta.validation.Valid;
import lenicorp.admin.workflowengine.controller.service.TransitionSideEffectService;
import lenicorp.admin.workflowengine.model.dtos.TransitionSideEffectDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/side-effects")
@RequiredArgsConstructor
public class TransitionSideEffectController
{
    private final TransitionSideEffectService service;
    @PostMapping
    public ResponseEntity<TransitionSideEffectDTO> create(@Valid @RequestBody TransitionSideEffectDTO dto)
    {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransitionSideEffectDTO> update(@PathVariable Long id, @Valid @RequestBody TransitionSideEffectDTO dto)
    {
        try
        {
            return ResponseEntity.ok(service.update(id, dto));
        }
        catch (NoSuchElementException e)
        {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id)
    {
        try
        {
            service.delete(id);
            return ResponseEntity.noContent().build();
        }
        catch (NoSuchElementException e)
        {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/by-transition/{transitionId}")
    public List<TransitionSideEffectDTO> listByTransition(@PathVariable Long transitionId)
    {
        return service.findByTransitionId(transitionId);
    }

    @GetMapping("/by-transition/{transitionId}/search")
    public Page<TransitionSideEffectDTO> search(
            @PathVariable Long transitionId,
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size)
    {
        return service.search(transitionId, key, PageRequest.of(page, size));
    }
}