package lenicorp.admin.security.controller.services.specs;

import lenicorp.admin.security.model.dtos.AuthResponse;
import lenicorp.admin.security.model.dtos.CreateUserDTO;
import lenicorp.admin.security.model.dtos.UserDTO;
import lenicorp.admin.security.model.entities.AuthToken;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IUserService
{
    UserDTO createUser(UserDTO user);

    UserDTO createUserWithProfile(CreateUserDTO user);

    UserDTO updateUser(UserDTO user);

    void changePassword(UserDTO user);

    void resetPassword(UserDTO user);

    void blockUser(Long userId);

    void unblockUser(Long userId);

    void sendResetPasswordEmail(Long userId);

    void sendResetPasswordEmail(String email);

    void sendActivationEmail(Long userId);

    void activateAccount(UserDTO user);

    Page<UserDTO> searchUsers(String key, Long strId, Pageable pageable);

    List<UserDTO> getVisibleUsers();

    UserDTO findByUsername(String username);

    AuthToken generateAuthToken(Long userId);
    void invalidateAuthToken(String token);

    @Transactional
    AuthResponse login(UserDTO dto);

    @Transactional
    AuthResponse refreshToken(Long userId);
}
