
package lenicorp.admin.types.controller.web;

import lenicorp.admin.types.controller.services.ITypeService;
import lenicorp.admin.types.model.dtos.TypeDTO;
import lenicorp.admin.types.model.dtos.TypeGroupDTO;
import lenicorp.admin.utilities.validatorgroups.CreateGroup;
import lenicorp.admin.utilities.validatorgroups.SetSousTypeGroup;
import lenicorp.admin.utilities.validatorgroups.UpdateGroup;
import jakarta.validation.Valid;
import jakarta.validation.groups.ConvertGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/types")
@RequiredArgsConstructor @Validated
public class TypeController
{

    private final ITypeService typeService;

    @PostMapping @Validated({CreateGroup.class})
    public TypeDTO createType(@Valid @RequestBody TypeDTO dto)
    {
        return typeService.createType(dto);
    }

    @PutMapping @Validated(value = {UpdateGroup.class})
    public TypeDTO updateType(@Valid @RequestBody TypeDTO dto) {
        return typeService.updateType(dto);
    }

    @GetMapping("/search")
    public Page<TypeDTO> searchTypes(
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "groupCodes", required = false) List<String> groupCodes,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        return typeService.searchTypes(key, groupCodes, PageRequest.of(page, size));
    }

    @PostMapping("/groups") @Validated(value = {CreateGroup.class})
    public TypeGroupDTO createTypeGroup(@Valid @RequestBody TypeGroupDTO dto)
    {
        return typeService.createTypeGroup(dto);
    }

    @PutMapping("/groups") @Validated(value = {UpdateGroup.class})
    public TypeGroupDTO updateTypeGroup(@Valid @RequestBody TypeGroupDTO dto)
    {
        return typeService.updateTypeGroup(dto);
    }

    @GetMapping("/groups/search")
    public Page<TypeGroupDTO> searchTypeGroups(
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size)
    {
        return typeService.searchTypeGroups(key, PageRequest.of(page, size));
    }

    @GetMapping("/groups/list")
    public List<TypeGroupDTO> getAllTypeGroups()
    {
        return typeService.getAllTypeGroups();
    }

    @GetMapping("/direct-sous-types")
    public List<TypeDTO> getDirectSousTypes(@RequestParam("parentCode") String parentCode)
    {
        return typeService.getDirectSousTypes(parentCode);
    }

    @PostMapping("/set-sous-types") @Validated(value = {SetSousTypeGroup.class})
    public ResponseEntity<Void> setSousTypes(@Valid @RequestBody TypeDTO dto)
    {
        typeService.setSousTypes(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/possible-sous-types")
    public List<TypeDTO> getPossibleSousTypes(@RequestParam("parentCode") String parentCode)
    {
        return typeService.getPossibleSousTypes(parentCode);
    }

    @GetMapping("/by-group/{groupCode}")
    public List<TypeDTO> getTypesByGroupCode(@PathVariable("groupCode") String groupCode)
    {
        return typeService.getTypesByGroupCode(groupCode);
    }
}
