package lenicorp.admin.security.controller.web;

import lenicorp.admin.security.controller.services.specs.IAuthorityService;
import lenicorp.admin.security.model.dtos.AuthResponse;
import lenicorp.admin.security.model.dtos.AuthorityDTO;
import lenicorp.admin.security.model.dtos.UserProfileAssoDTO;
import lenicorp.admin.security.model.views.VProfile;
import lenicorp.admin.utilities.validatorgroups.*;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.validation.groups.ConvertGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/authorities")
@RequiredArgsConstructor
public class AuthorityController
{
    private final IAuthorityService authorityService;

    @GetMapping("/{username}")
    public Set<String> getAuthoritiesByUsername(@PathVariable("username") String username)
    {
        return authorityService.getAuthoritiesByUsername(username);
    }

    @PostMapping(value = "/privileges/create", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public AuthorityDTO createPrivilege(@Valid @ConvertGroup(to = CreatePrvGroup.class) @RequestBody AuthorityDTO authorityDTO)
    {
        return authorityService.createPrivilege(authorityDTO);
    }

    @PostMapping(value = "/privileges/creates", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed("ADMIN")
    public void createPrivilege(@RequestBody List<AuthorityDTO> dtos)
    {
        authorityService.createPrivilege(dtos);
    }

    @PutMapping(value = "/privileges/update", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public AuthorityDTO updatePrivilege(@Valid @ConvertGroup(to = UpdatPrvGroup.class) @RequestBody AuthorityDTO authorityDTO)
    {
        return authorityService.updatePrivilege(authorityDTO);
    }

    @PostMapping(value = "/roles/create", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public AuthorityDTO createRole(@Valid @ConvertGroup(to = CreateGroup.class) @RequestBody AuthorityDTO authorityDTO)
    {
        return authorityService.createRole(authorityDTO);
    }

    @PutMapping(value = "/roles/update", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public AuthorityDTO updateRole(@Valid @ConvertGroup(to = UpdateRolGroup.class) @RequestBody AuthorityDTO authorityDTO)
    {
        return authorityService.updateRole(authorityDTO);
    }

    @PostMapping(value = "/profiles/create", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public AuthorityDTO createProfile(@Valid @ConvertGroup(to = CreateGroup.class) @RequestBody AuthorityDTO authorityDTO)
    {
        return authorityService.createProfile(authorityDTO);
    }

    @PutMapping(value = "/profiles/update", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public AuthorityDTO updateProfile(@Valid @ConvertGroup(to = UpdatPrflGroup.class) @RequestBody AuthorityDTO authorityDTO)
    {
        return authorityService.updateProfile(authorityDTO);
    }

    @PostMapping(value = "/add-profile-to-user", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void addProfileToUser(@Valid @ConvertGroup(to = CreateGroup.class) @RequestBody UserProfileAssoDTO dto)
    {
        authorityService.addProfileToUser(dto);
    }

    /**
     * Update a user profile assignment
     * @param dto The DTO containing the updated information
     * @return The updated UserProfileAssoDTO
     */
    @PutMapping(value = "/update-user-profile", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public UserProfileAssoDTO updateUserProfileAssignment(@Valid @ConvertGroup(to = UpdateGroup.class) @RequestBody UserProfileAssoDTO dto)
    {
        return authorityService.updateUserProfileAssignment(dto);
    }

    /**
     * Revoke a profile assignment by setting its status to inactive
     * @param id The ID of the AuthAssociation to revoke
     */
    @PutMapping("/revoke-profile-assignment/{id}")
    public void revokeProfileAssignment(@PathVariable("id") Long id)
    {
        authorityService.revokeProfileAssignment(id);
    }

    /**
     * Restore a revoked profile assignment by setting its status to active
     * @param id The ID of the AuthAssociation to restore
     */
    @PutMapping("/restore-profile-assignment/{id}")
    public void restoreProfileAssignment(@PathVariable("id") Long id)
    {
        authorityService.restoreProfileAssignment(id);
    }

    /**
     * Change the default profile for a user
     * @param id The ID of the AuthAssociation to set as default
     */
    @PutMapping("/change-default-profile/{id}")
    public AuthResponse changeDefaultProfile(@PathVariable("id") Long id)
    {
        AuthResponse response = authorityService.changeDefaultProfile(id);
        return response;
    }

    // Endpoints pour la recherche des privilèges
    @GetMapping("/privileges/search")
    public Page<AuthorityDTO> searchPrivileges(
            @RequestParam(value = "key", defaultValue = "") String key,
            @RequestParam(value = "privilegeTypeCodes", required = false) List<String> privilegeTypeCodes,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size)
    {
        return authorityService.searchPrivileges(key, privilegeTypeCodes, PageRequest.of(page, size));
    }

    @GetMapping("/privileges/list/by-privilege-type-codes")
    public List<AuthorityDTO> getPrivilegesListByTypeCodes(
            @RequestParam(value = "privilegeTypeCodes", required = false) List<String> privilegeTypeCodes)
    {
        return authorityService.getPrivilegesListByTypeCode(privilegeTypeCodes);
    }

    @GetMapping("/privileges/list/by-role-codes")
    public List<AuthorityDTO> getPrivilegesListByRoleCodes(
            @RequestParam(value = "roleCodes", required = false) List<String> roleCodes)
    {
        return authorityService.getPrivilegesListByRoleCodes(roleCodes);
    }

    @GetMapping("/privileges/search/by-role/{roleCode}")
    public List<AuthorityDTO> searchPrivilegesByRoleCode(
            @PathVariable("roleCode") String roleCode,
            @RequestParam(value = "key", defaultValue = "") String key,
            @RequestParam(value = "privilegeTypeCodes", required = false) List<String> privilegeTypeCodes)
    {
        return authorityService.searchPrivilegesByRoleCode(roleCode, key, privilegeTypeCodes);
    }

    @GetMapping("/privileges/search/by-profile/{profileCode}")
    public List<AuthorityDTO> searchPrivilegesByProfileCode(
            @PathVariable("profileCode") String profileCode,
            @RequestParam(value = "key", defaultValue = "") String key,
            @RequestParam(value = "privilegeTypeCodes", required = false) List<String> privilegeTypeCodes)
    {
        return authorityService.searchPrivilegesByProfileleCode(profileCode, key, privilegeTypeCodes);
    }

    // Endpoints pour la recherche des rôles
    @GetMapping("/roles/search")
    public Page<AuthorityDTO> searchRoles(
            @RequestParam(value = "key", defaultValue = "") String key,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size)
    {
        return authorityService.searchRoles(key, PageRequest.of(page, size));
    }

    @GetMapping("/roles/search/by-profile/{profileCode}")
    public List<AuthorityDTO> searchRolesByProfileCode(
            @PathVariable("profileCode") String profileCode,
            @RequestParam(value = "key", defaultValue = "") String key)
    {
        return authorityService.searchRolesByProfileCode(profileCode, key);
    }

    // Endpoints pour la recherche des profils
    @GetMapping("/profiles/search")
    public Page<AuthorityDTO> searchProfiles(
            @RequestParam(value = "key", defaultValue = "") String key,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size)
    {
        return authorityService.searchProfiles(key, PageRequest.of(page, size));
    }

    @GetMapping("/profiles/search/by-user/{userId}")
    public Page<AuthorityDTO> searchProfilesByUserId(
            @PathVariable("userId") Long userId,
            @RequestParam(value = "key", defaultValue = "") String key,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size)
    {
        return authorityService.searchProfilesByUserId(userId, key, PageRequest.of(page, size));
    }

    /**
     * Get all profiles as VProfile entities
     * @return List of all profiles
     */
    @GetMapping("/profiles/all")
    public List<VProfile> getAllProfiles()
    {
        return authorityService.getAllProfiles();
    }

    /**
     * Search for user profile assignments with pagination and multiple criteria
     * @param userId Optional user ID filter
     * @param strId Optional structure ID filter to filter profiles by structure chain sigle
     * @param profileCode Optional profile code filter
     * @param key Search term for name, email, etc.
     * @param page Page number (0-based)
     * @param size Page size
     * @return Page of UserProfileAssoDTO objects
     */
    @GetMapping("/user-profiles/search")
    public Page<UserProfileAssoDTO> searchUserProfileAssignments(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "strId", required = false) Long strId,
            @RequestParam(value = "profileCode", required = false) String profileCode,
            @RequestParam(value = "key", defaultValue = "") String key,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size)
    {
        return authorityService.searchUserProfileAssignations(userId, strId, profileCode, key, PageRequest.of(page, size));
    }

    /**
     * Find active and current profiles for a specific user
     * @param userId The user ID
     * @return List of UserProfileAssoDTO objects representing active and current profiles
     */
    @GetMapping("/user-profiles/active/{userId}")
    public List<UserProfileAssoDTO> findActiveAndCurrentProfilesByUserId(
            @PathVariable("userId") Long userId)
    {
        return authorityService.findActiveAndCurrentProfilesByUserId(userId);
    }
}