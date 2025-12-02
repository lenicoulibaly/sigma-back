package lenicorp.admin.security.controller.repositories;

import lenicorp.admin.security.model.dtos.AuthorityDTO;
import lenicorp.admin.security.model.dtos.UserProfileAssoDTO;
import lenicorp.admin.security.model.entities.AppAuthority;
import lenicorp.admin.security.model.entities.AuthAssociation;
import lenicorp.admin.security.model.validators.ExistingUserId;
import lenicorp.admin.security.model.views.VUserProfile;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface AuthAssoRepo extends JpaRepository<AuthAssociation, Long>
{
    @Query("select vp from VUserProfile vp where vp.email = :username and vp.assStatusCode = 'STA_ASS_CUR'")
    VUserProfile findUserCurrentProfile(@Param("username") String username);

    @Query("select ass.privilege from AuthAssociation ass where ass.role.code = :roleCode and ass.type.code = 'ROL_PRV'")
    List<AppAuthority> findRolePrivilegesEntities(@Param("roleCode") String roleCode);

    @Query("select ass.role from AuthAssociation ass where ass.profile.code = :profileCode and ass.type.code = 'PRFL_ROL'")
    List<AppAuthority> findProfileRolesEntities(@Param("profileCode") String profileCode);

    @Query(value = """
        select distinct vpp.privilege_code as authority_code
        from v_profile_privilege vpp 
        where vpp.profile_code = :profileCode
        union
        select distinct vpr.role_code as authority_code
        from v_profile_role vpr 
        where vpr.profile_code = :profileCode
        """, nativeQuery = true)
    Set<String> findAuthoritiesByProfileCode(@Param("profileCode") String profileCode);

    @Query("select count(a) > 0 from AuthAssociation a where a.role.code = :roleCode and a.privilege.code = :privilegeCode")
    boolean existsByRoleCodeAndPrivilegeCode(@Param("roleCode") String roleCode, @Param("privilegeCode") String privilegeCode);

    @Query("select count(a) > 0 from AuthAssociation a where a.profile.code = :profileCode and a.role.code = :roleCode")
    boolean existsByProfileCodeAndRoleCode(@Param("profileCode") String profileCode, @Param("roleCode") String roleCode);

    @Modifying
    @Query(value = """
            DELETE FROM auth_association 
            WHERE role_code = :roleCode 
            AND privilege_code = :privilegeCode 
            AND type_code = 'ROL_PRV'
            """, nativeQuery = true)
    void removePrivilegeToRole(@Param("roleCode") String roleCode, @Param("privilegeCode") String privilegeCode);

    @Modifying
    @Query(value = """
            DELETE FROM auth_association 
            WHERE profile_code = :profileCode 
            AND role_code = :roleCode 
            AND type_code = 'PRFL_ROL'
            """, nativeQuery = true)
    void removeRoleToProfile(@Param("profileCode") String profileCode, @Param("roleCode") String roleCode);

    @Query("select count(vup) > 0 from VUserProfile vup where vup.userId = :userId and vup.profileCode = :profileCode and vup.assStrId = :strId")
    boolean existsByUserIdAndProfileCodeAndStrId(@Param("userId") Long userId, @Param("profileCode") String profileCode, @Param("strId") Long strId);

    @Query("select count(vup.userId) > 0 from VUserProfile vup where vup.userId = :userId and vup.assStatusCode <> 'STA_ASS_INACT'")
    boolean userHasAnyProfile(@Param("userId") @ExistingUserId @NotNull(message = "L'utilisateur est obligatoire") Long userId);

    // The following methods are for supporting service layer implementations

    // Helper method to find authorities by role code
    default Set<String> findAuthoritiesByRoleCode(String roleCode) {
        if (roleCode == null) return Set.of();
        return findRolePrivilegesEntities(roleCode).stream()
                .map(AppAuthority::getCode)
                .collect(java.util.stream.Collectors.toSet());
    }

    // Helper method to find authorities by username
    default Set<String> findAuthoritiesByUsername(String username) {
        VUserProfile profile = findUserCurrentProfile(username);
        if (profile == null) return Set.of();
        return findAuthoritiesByProfileCode(profile.getProfileCode());
    }

    // Helper method to convert AppAuthority entities to AuthorityDTO objects
    default List<AuthorityDTO> findRolePrivileges(String roleCode) {
        if (roleCode == null) return List.of();
        return findRolePrivilegesEntities(roleCode).stream()
                .map(privilege -> new AuthorityDTO(
                    privilege.getCode(),
                    privilege.getName(),
                    privilege.getDescription(),
                    "PRV",
                    "Privilège",
                    privilege.getPrivilegeType() != null ? privilege.getPrivilegeType().code : null,
                    privilege.getPrivilegeType() != null ? privilege.getPrivilegeType().name : null,
                    "PRV"
                ))
                .collect(java.util.stream.Collectors.toList());
    }

    // Helper method to convert AppAuthority entities to AuthorityDTO objects
    default List<AuthorityDTO> findProfileRoles(String profileCode) {
        if (profileCode == null) return List.of();
        return findProfileRolesEntities(profileCode).stream()
                .map(role -> new AuthorityDTO(
                    role.getCode(),
                    role.getName(),
                    role.getDescription(),
                    "ROL",
                    "Rôle",
                    "ROL"
                ))
                .collect(java.util.stream.Collectors.toList());
    }

    // Native query to find privileges to add on role
    @Query(value = """
            SELECT DISTINCT vp.code, vp.name, vp.description, 
            'PRV' as type_code, 'Privilège' as type_name, vp.privilege_type_code, vp.privilege_type_name  
            FROM v_privilege vp 
            WHERE 
                vp.code IN (:privilegeCodes) 
                AND vp.code NOT IN 
                    (SELECT vrp.privilege_code FROM v_role_privilege vrp WHERE vrp.role_code = :roleCode)
            """, nativeQuery = true)
    List<Object[]> findPrivilegesToAddOnRoleRaw(@Param("roleCode") String roleCode, @Param("privilegeCodes") List<String> privilegeCodes);

    // Helper method to convert raw query results to AuthorityDTO objects
    default List<AuthorityDTO> findPrivilesToAddOnRole(String roleCode, List<String> privilegeCodes) {
        if (privilegeCodes == null || privilegeCodes.isEmpty()) return List.of();
        return findPrivilegesToAddOnRoleRaw(roleCode, privilegeCodes).stream()
                .map(row -> new AuthorityDTO(
                    (String) row[0], // code
                    (String) row[1], // name
                    (String) row[2], // description
                    (String) row[3], // type_code
                    (String) row[4], // type_name
                    (String) row[5], // privilege_type_code
                    (String) row[6], // privilege_type_name
                    "PRV" // authType
                ))
                .collect(java.util.stream.Collectors.toList());
    }

    // Native query to find privileges to remove on role
    @Query(value = """
            SELECT DISTINCT vp.code, vp.name, vp.description, 
            'PRV' as type_code, 'Privilège' as type_name, vp.privilege_type_code, vp.privilege_type_name
            FROM v_privilege vp 
            WHERE 
                vp.code NOT IN (:privilegeCodes) 
                AND vp.code IN (select vrp.privilege_code from v_role_privilege vrp where vrp.role_code = :roleCode)
            """, nativeQuery = true)
    List<Object[]> findPrivilegesToRemoveOnRoleRaw(@Param("roleCode") String roleCode, @Param("privilegeCodes") List<String> privilegeCodes);

    // Helper method to convert raw query results to AuthorityDTO objects
    default List<AuthorityDTO> findPrivilesToRemoveOnRole(String roleCode, List<String> privilegeCodes) {
        if (privilegeCodes == null || privilegeCodes.isEmpty()) return List.of();
        return findPrivilegesToRemoveOnRoleRaw(roleCode, privilegeCodes).stream()
                .map(row -> new AuthorityDTO(
                    (String) row[0], // code
                    (String) row[1], // name
                    (String) row[2], // description
                    (String) row[3], // type_code
                    (String) row[4], // type_name
                    (String) row[5], // privilege_type_code
                    (String) row[6], // privilege_type_name
                    "PRV" // authType
                ))
                .collect(java.util.stream.Collectors.toList());
    }

    // Native query to find roles to add on profile
    @Query(value = """
            SELECT DISTINCT rol.code as code, rol.name as name, rol.description as description, 
            t.code as type_code, t.name as type_name 
            FROM app_authority rol 
                LEFT JOIN type t ON t.code = rol.type_code 
            WHERE 
                rol.code IN (:roleCodes) 
                AND rol.code NOT IN (select vpr.role_code from v_profile_role vpr where vpr.profile_code = :profileCode)
            """, nativeQuery = true)
    List<Object[]> findRolesToAddOnProfileRaw(@Param("profileCode") String profileCode, @Param("roleCodes") List<String> roleCodes);

    // Helper method to convert raw query results to AuthorityDTO objects
    default List<AuthorityDTO> findRolesToAddOnProfile(String profileCode, List<String> roleCodes) {
        if (roleCodes == null || roleCodes.isEmpty()) return List.of();
        return findRolesToAddOnProfileRaw(profileCode, roleCodes).stream()
                .map(row -> new AuthorityDTO(
                    (String) row[0], // code
                    (String) row[1], // name
                    (String) row[2], // description
                    (String) row[3], // type_code
                    (String) row[4], // type_name
                    "ROL" // authType
                ))
                .collect(java.util.stream.Collectors.toList());
    }

    // Native query to find roles to remove on profile
    @Query(value = """
            select DISTINCT rol.code as code, rol.name as name, rol.description as description, 
            t.code as type_code, t.name as type_name from app_authority rol 
            left join type t on t.code = rol.type_code 
            where t.code = 'ROL' 
            and rol.code in (select vpr.role_code from v_profile_role vpr where vpr.profile_code = :profileCode)
            and rol.code NOT IN (:roleCodes) 
            """, nativeQuery = true)
    List<Object[]> findRolesToRemoveOnProfileRaw(@Param("profileCode") String profileCode, @Param("roleCodes") List<String> roleCodes);

    // Helper method to convert raw query results to AuthorityDTO objects
    default List<AuthorityDTO> findRolesToRemoveOnProfile(String profileCode, List<String> roleCodes) {
        if (roleCodes == null || roleCodes.isEmpty()) return List.of();
        return findRolesToRemoveOnProfileRaw(profileCode, roleCodes).stream()
                .map(row -> new AuthorityDTO(
                    (String) row[0], // code
                    (String) row[1], // name
                    (String) row[2], // description
                    (String) row[3], // type_code
                    (String) row[4], // type_name
                    "ROL" // authType
                ))
                .collect(java.util.stream.Collectors.toList());
    }

    // Native query to find active and current profiles by user ID
    @Query(value = """
            select vup.ass_id, vup.user_id, vup.email, vup.first_name, vup.last_name, 
            vup.profile_code, vup.profile_name, vup.profile_description,
            vup.ass_str_id, vup.ass_str_name, vup.user_profile_ass_type_code, 
            vup.user_profile_ass_type_name, vup.libelle, vup.starting_date, vup.ending_date,
            vup.ass_status_code, vup.ass_status_name, vup.ordre
            from v_user_profile vup 
            where vup.user_id = :userId
            and vup.ass_status_code in ('STA_ASS_ACT', 'STA_ASS_CUR')
            order by vup.ordre, vup.profile_name
            """, nativeQuery = true)
    List<Object[]> findActiveAndCurrentProfilesByUserIdRaw(@Param("userId") Long userId);

    // Helper method to convert raw query results to UserProfileAssoDTO objects
    default List<UserProfileAssoDTO> findActiveAndCurrentProfilesByUserId(Long userId) {
        if (userId == null) return List.of();
        return findActiveAndCurrentProfilesByUserIdRaw(userId).stream()
                .map(row -> {
                    UserProfileAssoDTO dto = new UserProfileAssoDTO();
                    dto.setId((Long) row[0]);
                    dto.setUserId((Long) row[1]);
                    dto.setEmail((String) row[2]);
                    dto.setFirstName((String) row[3]);
                    dto.setLastName((String) row[4]);
                    dto.setProfileCode((String) row[5]);
                    dto.setProfileName((String) row[6]);
                    dto.setStrId((Long) row[8]);
                    dto.setStrName((String) row[9]);
                    dto.setUserProfileAssTypeCode((String) row[10]);
                    dto.setUserProfileAssTypeName((String) row[11]);
                    dto.setLibelle((String) row[12]);
                    dto.setStartingDate(row[13] != null ? ((java.sql.Date) row[13]).toLocalDate() : null);
                    dto.setEndingDate(row[14] != null ? ((java.sql.Date) row[14]).toLocalDate() : null);
                    dto.setAssStatusCode((String) row[15]);
                    dto.setAssStatusName((String) row[16]);
                    dto.setOrdre(((Number) row[17]).longValue());
                    return dto;
                })
                .collect(java.util.stream.Collectors.toList());
    }

    @Query("""
            select new lenicorp.admin.security.model.dtos.AuthorityDTO
            (vp.code, vp.name, vp.description, 'PRV', 'PRIVILEGE', vp.privilegeTypeCode, vp.privilegeTypeName)
            from VPrivilege vp 
            where (upper(function('unaccent', vp.name) ) like upper(function('unaccent',concat('%',:key, '%')))
                  or upper(function('unaccent', vp.code) ) like upper(function('unaccent',concat('%',:key, '%')))
                  or upper(function('unaccent', vp.description)) like upper(function('unaccent',concat('%',:key, '%'))))
            and (:hasPrivilegeCodesFilter = false or vp.privilegeTypeCode in (:privilegeTypeCodes))
            order by vp.name
            """)
    Page<AuthorityDTO> searchPrivileges(@Param("key") String key,
                                           @Param("privilegeTypeCodes") List<String> privilegeTypeCodes,
                                           @Param("hasPrivilegeCodesFilter") boolean hasPrivilegeCodesFilter,
                                           Pageable pageable);

    @Query("""
        select new lenicorp.admin.security.model.dtos.AuthorityDTO(vrp.privilegeCode, vrp.privilegeName, vrp.privilegeDescription, 'PRV', 'PRIVILEGE', vrp.privilegeTypeCode, vrp.privilegeTypeName)
        from VRolePrivilege vrp where vrp.roleCode = :roleCode and vrp.privilegeTypeCode in :privilegeTypeCodes 
        and (
        locate(upper(coalesce(:key, '') ), upper(cast(function('unaccent',  coalesce(vrp.privilegeCode, '') ) as string))) >0 or 
        locate(upper(coalesce(:key, '') ), upper(cast(function('unaccent',  coalesce(vrp.privilegeName, '') ) as string))) >0
        ) 
    """)
    List<AuthorityDTO> searchPrivilegesByRoleCode(@Param("roleCode") String roleCode,
                                                  @Param("key") String key,
                                                  @Param("privilegeTypeCodes")List<String> privilegeTypeCodes);

    @Query("""
    select new lenicorp.admin.security.model.dtos.AuthorityDTO(vpp.privilegeCode, vpp.privilegeName, vpp.privilegeDescription, 'PRV', 'PRIVILEGE', vpp.privilegeTypeCode, vpp.privilegeTypeName)
    from VProfilePrivilege vpp where vpp.profileCode = :profileCode and vpp.privilegeTypeCode in :privilegeTypeCodes 
    and (
    locate(upper(coalesce(:key, '') ), upper(cast(function('unaccent',  coalesce(vpp.privilegeCode, '') ) as string))) >0 or 
    locate(upper(coalesce(:key, '') ), upper(cast(function('unaccent',  coalesce(vpp.privilegeName, '') ) as string))) >0
    ) 
    """)
    List<AuthorityDTO> searchPrivilegesByProfileCode(@Param("profileCode")String profileCode,
                                                     @Param("key") String key,
                                                     @Param("privilegeTypeCodes")List<String> privilegeTypeCodes);
    @Query("""
    select new lenicorp.admin.security.model.dtos.AuthorityDTO(vr.code, vr.name, vr.description, 'ROLE', 'ROLE', '', '')
    from VRole vr where locate(upper(coalesce(:key, '') ), upper(cast(function('unaccent',  coalesce(vr.code, '') ) as string))) >0 or
    locate(upper(coalesce(:key, '') ), upper(cast(function('unaccent',  coalesce(vr.name, '') ) as string))) >0 or
    locate(upper(coalesce(:key, '') ), upper(cast(function('unaccent',  coalesce(vr.description, '') ) as string))) >0
""")
    Page<AuthorityDTO> searchRoles(@Param("key") String key, Pageable pageable);

    @Query("""
    select new lenicorp.admin.security.model.dtos.AuthorityDTO(vpr.roleCode, vpr.roleName, vpr.roleDescription, 'ROLE', 'ROLE', '', '')
    from VProfileRole vpr where vpr.profileCode = :profileCode and 
        (locate(upper(coalesce(:key, '') ), upper(cast(function('unaccent',  coalesce(vpr.roleCode, '') ) as string))) >0 or
         locate(upper(coalesce(:key, '') ), upper(cast(function('unaccent',  coalesce(vpr.roleName, '') ) as string))) >0 or
         locate(upper(coalesce(:key, '') ), upper(cast(function('unaccent',  coalesce(vpr.roleDescription, '') ) as string))) >0)
    """)
    List<AuthorityDTO> searchRolesByProfileCode(@Param("profileCode")String profileCode,
                                                @Param("key") String key);

    @Query("""
     select new lenicorp.admin.security.model.dtos.AuthorityDTO(vp.code, vp.name, vp.description, 'PRFL', 'PROFILE', '', '')
     from VProfile vp where 
     locate(upper(coalesce(:key, '') ), upper(cast(function('unaccent',  coalesce(vp.code, '') ) as string))) >0 or
     locate(upper(coalesce(:key, '') ), upper(cast(function('unaccent',  coalesce(vp.name, '') ) as string))) >0 or
     locate(upper(coalesce(:key, '') ), upper(cast(function('unaccent',  coalesce(vp.description, '') ) as string))) >0
    """)
    Page<AuthorityDTO> searchProfiles(@Param("key") String key, Pageable pageable);

    @Query("""
    select new lenicorp.admin.security.model.dtos.AuthorityDTO(vup.profileCode, vup.profileName, vup.profileDescription, 'PRFL', 'PROFILE', '', '')
     from VUserProfile vup where vup.userId = :userId and
     (
        locate(upper(coalesce(:key, '') ), upper(cast(function('unaccent',  coalesce(vup.profileCode, '') ) as string))) >0 or
        locate(upper(coalesce(:key, '') ), upper(cast(function('unaccent',  coalesce(vup.profileName, '') ) as string))) >0 or
        locate(upper(coalesce(:key, '') ), upper(cast(function('unaccent',  coalesce(vup.profileDescription, '') ) as string))) >0
     )
    """)
    Page<AuthorityDTO> searchProfilesByUserId(@Param("userId") Long userId, @Param("key") String key, Pageable pageable);

    @Query("""
    select new lenicorp.admin.security.model.dtos.AuthorityDTO(vp.code, vp.name, vp.description, 'PRV', 'PRIVILEGE', vp.privilegeTypeCode, vp.privilegeTypeName)
    from VPrivilege vp where vp.privilegeTypeCode in ?1
    """)
    List<AuthorityDTO> getPrivilegesListByTypeCode(List<String> privilegeTypeCodes);

    @Query("""
    select new lenicorp.admin.security.model.dtos.AuthorityDTO(vp.code, vp.name, vp.description, 'PRV', 'PRIVILEGE', vp.privilegeTypeCode, vp.privilegeTypeName)
    from VPrivilege vp
    """)
    List<AuthorityDTO> getAllPrivileges();

    @Query("""
    select new lenicorp.admin.security.model.dtos.AuthorityDTO(vrp.privilegeCode, vrp.privilegeName, vrp.privilegeDescription, 'PRIVILEGE', 'PRIVILEGE', '', '')
    from VRolePrivilege vrp where vrp.roleCode in ?1
    """)
    List<AuthorityDTO> getPrivilegesListByRoleCodes(List<String> roleCodes);
    @Query("""
    select new lenicorp.admin.security.model.dtos.AuthorityDTO(vr.code, vr.name, vr.description, 'ROLE', 'ROLE', '', '')
    from VRole vr
    """)
    List<AuthorityDTO> getAllPrivileges(List<String> roleCodes);

    //TODO créer la fonction getStrChaineSigles en base
    @Query(value = """
    select new lenicorp.admin.security.model.dtos.UserProfileAssoDTO(vup.assId, vup.libelle
        , vup.userId, vup.email, u.matricule, grade.code, vup.profileCode, vup.profileName
        , vup.assStrId, vup.assStrName, vup.userProfileAssTypeCode, vup.userProfileAssTypeName
        , vup.startingDate, vup.endingDate, vup.assStatusCode, ass.name , ass.ordre
        , vup.firstName, vup.lastName)
        from VUserProfile vup 
            join AppUser u on vup.userId = u.userId 
            left join u.grade grade 
            left join Type ass on vup.assStatusCode = ass.code
        where vup.userId = coalesce(:userId, vup.userId) 
            and vup.profileCode = coalesce(:profileCode, vup.profileCode) 
            and locate(cast(function('getStrChaineSigles',  :strId) as string), vup.assStrChaineSigles) = 1 and 
            (
                locate(upper(coalesce(:key, '') ), upper(cast(function('unaccent',  coalesce(vup.profileCode, '') ) as string))) >0 or
                locate(upper(coalesce(:key, '') ), upper(cast(function('unaccent',  coalesce(vup.profileName, '') ) as string))) >0 or
                locate(upper(coalesce(:key, '') ), upper(cast(function('unaccent',  coalesce(vup.profileDescription, '') ) as string))) >0 or
                locate(upper(coalesce(:key, '') ), upper(cast(function('unaccent',  coalesce(vup.firstName, '') ) as string))) >0 or
                locate(upper(coalesce(:key, '') ), upper(cast(function('unaccent',  coalesce(vup.lastName, '') ) as string))) >0 or    
                locate(upper(coalesce(:key, '') ), upper(cast(function('unaccent',  coalesce(vup.email, '') ) as string))) >0 or
                locate(upper(coalesce(:key, '') ), upper(cast(function('unaccent',  coalesce(vup.libelle, '') ) as string))) >0 or
                locate(upper(coalesce(:key, '') ), upper(cast(function('unaccent',  coalesce(vup.assStrName, '') ) as string))) >0 or
                locate(upper(coalesce(:key, '') ), upper(cast(function('unaccent',  coalesce(vup.assStrSigles, '') ) as string))) >0 or   
                locate(upper(coalesce(:key, '') ), upper(cast(function('unaccent',  coalesce(vup.assStrSigles, '') ) as string))) >0
            )   
    """, countQuery = """
    select count (vup.assId)
    from VUserProfile vup 
            join AppUser u on vup.userId = u.userId 
            left join u.grade grade 
            left join Type ass on vup.assStatusCode = ass.code
        where vup.userId = coalesce(:userId, vup.userId) 
            and vup.profileCode = coalesce(:profileCode, vup.profileCode) 
            and locate(cast(function('getStrChaineSigles',  :strId) as string), vup.assStrChaineSigles) = 1 and 
            (
                locate(upper(coalesce(:key, '') ), upper(cast(function('unaccent',  coalesce(vup.profileCode, '') ) as string))) >0 or
                locate(upper(coalesce(:key, '') ), upper(cast(function('unaccent',  coalesce(vup.profileName, '') ) as string))) >0 or
                locate(upper(coalesce(:key, '') ), upper(cast(function('unaccent',  coalesce(vup.profileDescription, '') ) as string))) >0 or
                locate(upper(coalesce(:key, '') ), upper(cast(function('unaccent',  coalesce(vup.firstName, '') ) as string))) >0 or
                locate(upper(coalesce(:key, '') ), upper(cast(function('unaccent',  coalesce(vup.lastName, '') ) as string))) >0 or    
                locate(upper(coalesce(:key, '') ), upper(cast(function('unaccent',  coalesce(vup.email, '') ) as string))) >0 or
                locate(upper(coalesce(:key, '') ), upper(cast(function('unaccent',  coalesce(vup.libelle, '') ) as string))) >0 or
                locate(upper(coalesce(:key, '') ), upper(cast(function('unaccent',  coalesce(vup.assStrName, '') ) as string))) >0 or
                locate(upper(coalesce(:key, '') ), upper(cast(function('unaccent',  coalesce(vup.assStrSigles, '') ) as string))) >0 or   
                locate(upper(coalesce(:key, '') ), upper(cast(function('unaccent',  coalesce(vup.assStrSigles, '') ) as string))) >0
            )   
""")
    Page<UserProfileAssoDTO> searchUserProfileAssignments(Long userId, Long strId, String profileCode, String key, Pageable pageable);

    @Query("""
    select vup from VUserProfile vup 
        where vup.userId = coalesce(:userId, vup.userId) 
            and vup.profileCode = coalesce(:profileCode, vup.profileCode) and  
            (
                locate(upper(coalesce(:key, '') ), upper(cast(function('unaccent',  coalesce(vup.profileCode, '') ) as string))) >0 or
                locate(upper(coalesce(:key, '') ), upper(cast(function('unaccent',  coalesce(vup.profileName, '') ) as string))) >0 or
                locate(upper(coalesce(:key, '') ), upper(cast(function('unaccent',  coalesce(vup.profileDescription, '') ) as string))) >0 or
                locate(upper(coalesce(:key, '') ), upper(cast(function('unaccent',  coalesce(vup.firstName, '') ) as string))) >0 or
                locate(upper(coalesce(:key, '') ), upper(cast(function('unaccent',  coalesce(vup.lastName, '') ) as string))) >0 or    
                locate(upper(coalesce(:key, '') ), upper(cast(function('unaccent',  coalesce(vup.email, '') ) as string))) >0 or
                locate(upper(coalesce(:key, '') ), upper(cast(function('unaccent',  coalesce(vup.libelle, '') ) as string))) >0 or
                locate(upper(coalesce(:key, '') ), upper(cast(function('unaccent',  coalesce(vup.assStrName, '') ) as string))) >0 or
                locate(upper(coalesce(:key, '') ), upper(cast(function('unaccent',  coalesce(vup.assStrSigles, '') ) as string))) >0
            )   
    """)
    Page<UserProfileAssoDTO> searchUserProfileAssignments(Long userId, String profileCode, String key, Pageable pageable);

    @Query("select vup from VUserProfile vup where vup.userId = ?1 and vup.assStatusCode = 'STA_ASS_CUR'")
    List<VUserProfile> getCurrentAssociationList(Long userId);
}
