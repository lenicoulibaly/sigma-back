package lenicorp.admin.security.controller.services.specs;

import lenicorp.admin.security.model.dtos.AuthResponse;
import lenicorp.admin.security.model.dtos.AuthorityDTO;
import lenicorp.admin.security.model.dtos.UserProfileAssoDTO;
import lenicorp.admin.security.model.views.VProfile;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface IAuthorityService
{
    Set<String> getAuthoritiesByUsername(String username);

    AuthorityDTO createPrivilege(AuthorityDTO authorityDTO);

    @Transactional
    void createPrivilege(List<AuthorityDTO> dtos);

    AuthorityDTO updatePrivilege(AuthorityDTO authorityDTO);

    AuthorityDTO createRole(AuthorityDTO authorityDTO);

    AuthorityDTO updateRole(AuthorityDTO authorityDTO);

    AuthorityDTO createProfile(AuthorityDTO authorityDTO);

    AuthorityDTO updateProfile(AuthorityDTO authorityDTO);

    @Transactional
    void addProfileToUser(UserProfileAssoDTO dto);

    /**
     * Update a user profile assignment
     * @param dto The DTO containing the updated information
     * @return The updated UserProfileAssoDTO
     */
    @Transactional
    UserProfileAssoDTO updateUserProfileAssignment(UserProfileAssoDTO dto);

    /**
     * Revoke a profile assignment by setting its status to inactive
     * @param id The ID of the AuthAssociation to revoke
     */
    @Transactional
    void revokeProfileAssignment(Long id);

    /**
     * Restore a revoked profile assignment by setting its status to active
     * @param id The ID of the AuthAssociation to restore
     */
    @Transactional
    void restoreProfileAssignment(Long id);

    /**
     * Change the default profile for a user
     * @param id The ID of the AuthAssociation to set as default
     */
    @Transactional
    AuthResponse changeDefaultProfile(Long id);

    Page<AuthorityDTO> searchPrivileges(String key, List<String> privilegeTypeCodes, Pageable pageable);
    List<AuthorityDTO> searchPrivilegesByRoleCode(String roleCode, String key, List<String> privilegeTypeCodes);
    List<AuthorityDTO> searchPrivilegesByProfileleCode(String roleCode, String key, List<String> privilegeTypeCodes);

    Page<AuthorityDTO> searchRoles(String key, Pageable pageable);
    List<AuthorityDTO> searchRolesByProfileCode(String profileCode, String key);

    Page<AuthorityDTO> searchProfiles(String key, Pageable pageable);
    Page<AuthorityDTO> searchProfilesByUserId(Long userId, String key, Pageable pageable);

    List<AuthorityDTO> getPrivilegesListByTypeCode(List<String> privilegeTypeCodes);

    List<AuthorityDTO> getPrivilegesListByRoleCodes(List<String> roleCodes);

    /**
     * Get all profiles as VProfile entities
     * @return List of all profiles
     */
    List<VProfile> getAllProfiles();

    /**
     * Search for user profile assignments with pagination and multiple criteria
     * @param userId Optional user ID filter
     * @param strId Optional structure ID filter to filter profiles by structure chain sigle
     * @param profileCode Optional profile code filter
     * @param key Search term for name, email, etc.
     * @param pageable Pagination parameters
     * @return Page of UserProfileAssoDTO objects
     */
    Page<UserProfileAssoDTO> searchUserProfileAssignations(Long userId, Long strId, String profileCode, String key, Pageable pageable);

    /**
     * Find active and current profiles for a specific user
     * @param userId The user ID
     * @return List of UserProfileAssoDTO objects representing active and current profiles
     */
    List<UserProfileAssoDTO> findActiveAndCurrentProfilesByUserId(Long userId);
}
