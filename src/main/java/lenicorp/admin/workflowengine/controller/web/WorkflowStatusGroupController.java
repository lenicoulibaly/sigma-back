package lenicorp.admin.workflowengine.controller.web;

import lenicorp.admin.workflowengine.controller.service.WorkflowStatusGroupService;
import lenicorp.admin.workflowengine.model.dtos.WorkflowStatusGroupDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/workflow-status-groups")
@RequiredArgsConstructor
public class WorkflowStatusGroupController {

    private final WorkflowStatusGroupService service;

    @GetMapping("/search/{workflowId}")
    public Page<WorkflowStatusGroupDTO> search(
            @PathVariable(required = true) Long workflowId,
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return service.search(key, workflowId, PageRequest.of(page, size));
    }

    @GetMapping("/accessible-list/{workflowCode}")
    public List<WorkflowStatusGroupDTO> getAccessibleWorkflowStatusGroupByWorkflowCode(
            @PathVariable(required = true) String workflowCode)
    {
        return service.getAccessibleWorkflowStatusGroupByWorkflowCode(workflowCode);
    }

    @PostMapping
    public WorkflowStatusGroupDTO create(@Valid @RequestBody WorkflowStatusGroupDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public WorkflowStatusGroupDTO update(@PathVariable Long id, @Valid @RequestBody WorkflowStatusGroupDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/{id}/authority-codes")
    public List<String> getAuthorityCodes(@PathVariable Long id) {
        return service.getAuthorityCodes(id);
    }

    @GetMapping("/is-status-visible")
    public boolean isStatusVisibleByGroup(
            @RequestParam("groupCode") String groupCode,
            @RequestParam("statusCode") String statusCode) {
        return service.isStatusVisibleByGroup(groupCode, statusCode);
    }
}
