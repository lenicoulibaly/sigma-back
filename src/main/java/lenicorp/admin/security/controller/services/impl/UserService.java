package lenicorp.admin.security.controller.services.impl;

import lenicorp.admin.exceptions.AppException;
import lenicorp.admin.notification.controller.services.MailServiceInterface;
import lenicorp.admin.security.controller.repositories.AuthTokenRepo;
import lenicorp.admin.security.controller.repositories.UserRepo;
import lenicorp.admin.security.controller.services.specs.IAuthorityService;
import lenicorp.admin.security.controller.services.specs.IJwtService;
import lenicorp.admin.security.controller.services.specs.IUserService;
import lenicorp.admin.security.model.dtos.AuthResponse;
import lenicorp.admin.security.model.dtos.CreateUserDTO;
import lenicorp.admin.security.model.dtos.UserDTO;
import lenicorp.admin.security.model.dtos.UserProfileAssoDTO;
import lenicorp.admin.security.model.entities.AppUser;
import lenicorp.admin.security.model.entities.AuthToken;
import lenicorp.admin.security.model.mappers.UserMapper;
import jakarta.annotation.security.RolesAllowed;
import lenicorp.admin.utilities.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService
{
    private final MailServiceInterface mailService;
    private final UserRepo userRepo;
    private final UserMapper userMapper;
    private final AuthTokenRepo authTokenRepo;
    private final IJwtService jwtService;
    private final IAuthorityService authorityService;
    @Value("${front.adress}")
    private String frontAddress;
    private final PasswordEncoder passwordEncoder;


    @Override @Transactional
    public UserDTO createUser(UserDTO dto)
    {
        AppUser user = userMapper.mapToAppUser(dto);
        user = userRepo.save(user);
        sendActivationEmail(user.getUserId());
        return userMapper.mapToUserDTO(user);
    }

    @Override @Transactional
    public UserDTO createUserWithProfile(CreateUserDTO dto)
    {
        // Création de l'utilisateur
        AppUser user = userMapper.mapToAppUser(dto);
        user = userRepo.save(user);

        // Assignation du profil à l'utilisateur
        if (dto.getProfileCode() != null)
        {
            UserProfileAssoDTO profileAssoDTO = userMapper.mapToUserProfileAssoDTO(dto);
            profileAssoDTO.setUserId(user.getUserId());
            authorityService.addProfileToUser(profileAssoDTO);
        }

        // Envoi de l'email d'activation
        sendActivationEmail(user.getUserId());

        return userMapper.mapToUserDTO(user);
    }

    @Override @Transactional
    public UserDTO updateUser(UserDTO dto)
    {
        AppUser user = userRepo.findById(dto.getUserId()).orElseThrow(() -> new AppException("User not found"));
        user = userMapper.updateUser(dto, user);
        user = userRepo.save(user);
        return userMapper.mapToUserDTO(user);
    }

    @Override @Transactional
    public void changePassword(UserDTO dto)
    {
        AppUser user = userRepo.findById(dto.getUserId()).orElseThrow(() -> new AppException("User not found"));
        String oldPassword = user.getPassword();
        if (oldPassword != null && !passwordEncoder.matches(dto.getOldPassword(), oldPassword)) throw new AppException("L'ancien mot de passe est incorrect");
        if (oldPassword != null && passwordEncoder.matches(dto.getPassword(), oldPassword)) throw new AppException("Le nouveau mot de passe doit être différent de l'ancien");
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        userRepo.save(user);
    }

    @Override @Transactional
    public void resetPassword(UserDTO dto)
    {
        AppUser user = userRepo.findById(dto.getUserId()).orElseThrow(() -> new AppException("User not found"));
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        userRepo.save(user);
        invalidateAuthToken(dto.getAuthToken());
    }

    @Override @Transactional
    public void blockUser(Long userId)
    {
        AppUser user = findUserById(userId);
        if (user == null) return;
        if(!user.isNotBlocked()) return;
        user.setNotBlocked(false);
        userRepo.save(user);
    }

    private AppUser findUserById(Long userId)
    {
        if(userId == null) return null;
        AppUser user = userRepo.findById(userId).orElseThrow(() -> new AppException("User not found"));
        if(user == null) throw new AppException("L'utilisateur n'existe pas");
        return user;
    }

    @Override @Transactional
    public void unblockUser(Long userId)
    {
        AppUser user = findUserById(userId);
        if (user == null) return;
        if(user.isNotBlocked()) return;
        user.setNotBlocked(true);
        userRepo.save(user);
    }

    @Override @Transactional
    public void sendResetPasswordEmail(Long userId)
    {
        AuthToken authToken = this.generateAuthToken(userId);
        AppUser user = userRepo.findById(userId).orElseThrow(() -> new AppException("User not found"));
        if(user == null) return;
        mailService.envoyerEmailReinitialisation(user.getEmail(), user.getLastName(), frontAddress + "/reset-password-form?userId=" + userId + "&token=" + authToken.getToken()).exceptionally(throwable ->
        {
            throwable.printStackTrace();
            throw new AppException(throwable.getMessage());
        });
        authToken.setEmailSent(true);
    }

    @Override @Transactional
    public void sendResetPasswordEmail(String email)
    {
        AppUser user = userRepo.findByUsername(email);
        if(user == null) throw new AppException("Aucun utilisateur trouvé avec cet email: " + email);
        sendResetPasswordEmail(user.getUserId());
    }

    @Override @Transactional
    public void sendActivationEmail(Long userId)
    {
        AuthToken authToken = this.generateAuthToken(userId);
        AppUser user = userRepo.findById(userId).orElseThrow(() -> new AppException("Utilisateur introuvable"));
        if(user == null) return;
        mailService.envoyerEmailActivation(user.getEmail(), user.getLastName(), "/activate-account?token=" + authToken.getToken()).exceptionally(throwable ->
        {
            throwable.printStackTrace();
            throw new AppException(throwable.getMessage());
        });
        authToken.setEmailSent(true);
    }

    @Override @Transactional
    public void activateAccount(UserDTO dto)
    {
        AppUser user = userRepo.findById(dto.getUserId()).orElseThrow(()->new AppException("Utilisateur introuvable"));
        user.setActivated(true);
        user.setNotBlocked(true);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        userRepo.save(user);
        invalidateAuthToken(dto.getAuthToken());
    }

    @Override @Transactional @RolesAllowed("GET_USR")
    public Page<UserDTO> searchUsers(String key, Long strId, Pageable pageable)
    {
        key = StringUtils.stripAccentsToUpperCase(key);
        Long currentProfileStrId = jwtService.getCurrentUserProfile() == null ? null : jwtService.getCurrentUserProfile().getAssStrId();
        if(strId != null) {
            return userRepo.searchUsers(key, strId, pageable);
        }
        if(currentProfileStrId == null) return userRepo.searchUsers(key, null, pageable);
        return userRepo.searchUsers(key, currentProfileStrId, pageable);
    }

    @Override @Transactional @RolesAllowed("GET_USR")
    public List<UserDTO> getVisibleUsers()
    {
        Long currentProfileStrId = jwtService.getCurrentUserProfile() == null ? null : jwtService.getCurrentUserProfile().getAssStrId();
        if(currentProfileStrId == null) {
            // If no current profile, return empty list
            return List.of();
        }
        // Get all users belonging to the current profile's structure
        return userRepo.getUsersByStructure(currentProfileStrId);
    }

    @Override
    public UserDTO findByUsername(String username)
    {
        AppUser user = userRepo.findByUsername(username);
        return userMapper.mapToUserDTO(user);
    }

    @Override
    public AuthToken generateAuthToken(Long userId)
    {
        AuthToken authToken = new AuthToken();
        authToken.setToken(UUID.randomUUID().toString());
        authToken.setUser(new AppUser(userId));
        authToken.setExpirationDate(LocalDateTime.now().plusDays(1));
        authToken.setAlreadyUsed(false);
        authToken.setEmailSent(false);
        authToken = authTokenRepo.save(authToken);
        return authToken;
    }

    @Override
    public void invalidateAuthToken(String token)
    {
        AuthToken authToken = authTokenRepo.findByToken(token);
        authToken.setAlreadyUsed(true);
        authToken.setUsageDate(LocalDateTime.now());
        authTokenRepo.save(authToken);
    }

    @Transactional @Override
    public AuthResponse login(UserDTO dto)
    {
        AppUser user = userRepo.findByUsername(dto.getEmail());
        if(user == null) throw new AppException("nom d'utilisateur ou mot de passe incorrect");

        user.setLastLogin(LocalDateTime.now());
        user = userRepo.save(user);
        return jwtService.getTokens(user);
    }

    @Transactional @Override
    public AuthResponse refreshToken(Long userId)
    {
        AppUser user = userRepo.findById(userId).orElseThrow(()->new AppException("Utilisateur introuvable"));
        if (user == null) throw new AppException("UserId incorrect ou inexistant : " + userId);

        user.setLastLogin(LocalDateTime.now());
        user = userRepo.save(user);
        return jwtService.getTokens(user);
    }
}
