package lenicorp.admin.security.controller.repositories;

import lenicorp.admin.security.model.dtos.UserDTO;
import lenicorp.admin.security.model.entities.AppUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepo extends JpaRepository<AppUser, Long>
{
    @Query("SELECT u FROM AppUser u WHERE u.email = :username")
    AppUser findByUsername(@Param("username") String username);

    @Query("SELECT COUNT(u) > 0 FROM AppUser u WHERE upper(u.email) = upper(:email)")
    boolean existsByEmail(@Param("email") String email);

    @Query("SELECT COUNT(u) > 0 FROM AppUser u WHERE u.email = :email AND u.userId <> :userId")
    boolean existsByEmail(@Param("email") String email, @Param("userId") Long userId);

    @Query("SELECT COUNT(u) > 0 FROM AppUser u WHERE u.tel = :tel AND u.userId <> :userId")
    boolean existsByTel(@Param("tel") String tel, @Param("userId") Long userId);

    @Query("SELECT COUNT(u) > 0 FROM AppUser u WHERE u.tel = :tel")
    boolean existsByTel(@Param("tel") String tel);

    @Query("SELECT u.activated FROM AppUser u WHERE u.userId = :userId")
    boolean userIsActive(@Param("userId") Long userId);

    @Query("SELECT u.activated FROM AppUser u WHERE u.email = :username")
    boolean userIsActive(@Param("username") String username);

    @Query("SELECT u.notBlocked FROM AppUser u WHERE u.userId = :userId")
    boolean userIsNotBlocked(@Param("userId") Long userId);

    @Query("SELECT u.notBlocked FROM AppUser u WHERE u.email = :username")
    boolean userIsNotBlocked(@Param("username") String username);

    @Query("SELECT u.password FROM AppUser u WHERE u.email = :username")
    String getPasswordByUsername(@Param("username") String username);

    @Query("SELECT COUNT(u) > 0 FROM AppUser u WHERE u.matricule = :matricule")
    boolean existsByMatricule(@Param("matricule") String matricule);

    @Query("SELECT COUNT(u) > 0 FROM AppUser u WHERE u.matricule = :matricule AND u.userId <> :userId")
    boolean existsByMatricule(@Param("matricule") String matricule, @Param("userId") Long userId);

    // The following methods will be implemented in the service layer
    // Raw query to support searchUsers in service layer
    @Query(value = """
            SELECT u.user_id, u.email, u.first_name, u.last_name, u.tel, u.matricule, 
            u.activated, u.not_blocked, u.str_id, s.name as str_name, s.sigle as str_sigle,
            g.code as grade_code, g.name as grade_name
            FROM app_user u
            LEFT JOIN structure s ON u.str_id = s.str_id
            LEFT JOIN type g ON u.grade_code = g.code
            WHERE (
                UPPER(UNACCENT(u.email)) LIKE UPPER(UNACCENT(:key))
                OR UPPER(UNACCENT(u.first_name)) LIKE UPPER(UNACCENT(:key))
                OR UPPER(UNACCENT(u.last_name)) LIKE UPPER(UNACCENT(:key))
                OR UPPER(UNACCENT(u.tel)) LIKE UPPER(UNACCENT(:key))
                OR UPPER(UNACCENT(u.matricule)) LIKE UPPER(UNACCENT(:key))
            )
            AND (:strId IS NULL OR s.chaine_sigles LIKE (SELECT CONCAT(s2.chaine_sigles, '%') FROM structure s2 WHERE s2.str_id = :strId))
            ORDER BY u.last_name, u.first_name
            """, nativeQuery = true)
    List<Object[]> searchUsersRaw(@Param("key") String key, @Param("strId") Long strId);

    // Count query to support pagination in searchUsers
    @Query(value = """
            SELECT COUNT(*)
            FROM app_user u
            LEFT JOIN structure s ON u.str_id = s.str_id
            WHERE (
                UPPER(UNACCENT(u.email)) LIKE UPPER(UNACCENT(:key))
                OR UPPER(UNACCENT(u.first_name)) LIKE UPPER(UNACCENT(:key))
                OR UPPER(UNACCENT(u.last_name)) LIKE UPPER(UNACCENT(:key))
                OR UPPER(UNACCENT(u.tel)) LIKE UPPER(UNACCENT(:key))
                OR UPPER(UNACCENT(u.matricule)) LIKE UPPER(UNACCENT(:key))
            )
            AND (:strId IS NULL OR s.chaine_sigles LIKE (SELECT CONCAT(s2.chaine_sigles, '%') FROM structure s2 WHERE s2.str_id = :strId))
            """, nativeQuery = true)
    Long countSearchUsers(@Param("key") String key, @Param("strId") Long strId);



    // Raw query to support getUsersByStructure in service layer
    @Query(value = """
            SELECT u.user_id, u.email, u.first_name, u.last_name, u.tel, u.matricule, 
            u.activated, u.not_blocked, u.str_id, s.name as str_name, s.sigle as str_sigle,
            g.code as grade_code, g.name as grade_name
            FROM app_user u
            LEFT JOIN structure s ON u.str_id = s.str_id
            LEFT JOIN type g ON u.grade_code = g.code
            WHERE (:strId IS NULL OR s.chaine_sigles LIKE (SELECT CONCAT(s2.chaine_sigles, '%') FROM structure s2 WHERE s2.str_id = :strId))
            ORDER BY u.last_name, u.first_name
            """, nativeQuery = true)
    List<Object[]> getUsersByStructureRaw(@Param("strId") Long strId);

    //TODO créer la fonction getStrChaineSigles en base
    @Query("""
    select new lenicorp.admin.security.model.dtos.UserDTO(
    u.userId, u.email, u.firstName, u.lastName, u.tel, u.changePasswordDate, u.activated, 
    u.notBlocked, u.lastLogin, u.strId, u.strName, u.strSigle, u.chaineSigles
    )
    from VUser u where locate(:key, u.searchText) > 0 
    and (:strId is null or locate(function('getStrChaineSigles', :strId), u.chaineSigles) = 1)
""")
    Page<UserDTO> searchUsers(@Param("key") String key, @Param("strId") Long strId, Pageable pageable);

    @Query("""
    select new lenicorp.admin.security.model.dtos.UserDTO(
    u.userId, u.email, u.firstName, u.lastName, u.tel, u.changePasswordDate, u.activated, 
    u.notBlocked, u.lastLogin, u.strId, u.strName, u.strSigle, u.chaineSigles
    )
    from VUser u where (:strId is null or locate(function('getStrChaineSigles', :strId), u.chaineSigles) = 1)
    """)
    List<UserDTO> getUsersByStructure(Long strId);

    @Query("""
    SELECT new lenicorp.admin.security.model.dtos.UserDTO(u.userId, u.email, u.password, u.activated, u.notBlocked)
    FROM AppUser u
    WHERE u.email = :username
""")
    UserDTO findMinimalByUsername(String username);
}