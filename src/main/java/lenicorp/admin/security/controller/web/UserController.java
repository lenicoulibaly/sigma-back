package lenicorp.admin.security.controller.web;

import lenicorp.admin.archive.model.dtos.validator.OnCreate;
import lenicorp.admin.security.controller.services.specs.IUserService;
import lenicorp.admin.security.model.dtos.AuthResponse;
import lenicorp.admin.security.model.dtos.CreateUserDTO;
import lenicorp.admin.security.model.dtos.UserDTO;
import lenicorp.admin.utilities.validatorgroups.*;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.validation.groups.ConvertGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UserController
{
    private final IUserService userService;

    @PostMapping(value = "/open/login", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public AuthResponse login(@Valid @ConvertGroup(to = LoginGroup.class) @RequestBody UserDTO user)
    {
        return userService.login(user);
    }

    @GetMapping(value = "/refresh-token/{userId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public AuthResponse refreshToken(@PathVariable("userId") Long userId)
    {
        return userService.refreshToken(userId);
    }

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed("CRT_USR")
    public UserDTO createUser(@Valid @ConvertGroup(to = CreateGroup.class) @RequestBody UserDTO user)
    {
        return userService.createUser(user);
    }

    @Validated({CreateGroup.class})
    @PostMapping(value = "/create-with-profile")
    //@RolesAllowed("CRT_USR")
    public UserDTO createUserWithProfile(@Valid @RequestBody CreateUserDTO user)
    {
        return userService.createUserWithProfile(user);
    }

    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public UserDTO updateUser(@Valid @ConvertGroup(to = UpdateGroup.class) @RequestBody UserDTO user)
    {
        return userService.updateUser(user);
    }

    @PutMapping(value = "/change-password", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void changePassword(@Valid @ConvertGroup(to = ChangePasswordGroup.class) @RequestBody UserDTO user)
    {
        userService.changePassword(user);
    }

    @PutMapping(value = "/open/reset-password", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void resetPassword(@Valid @ConvertGroup(to = ResetPasswordGroup.class) @RequestBody UserDTO user)
    {
        userService.resetPassword(user);
    }

    @GetMapping("/send-activation-email/{userId}")
    public void envoyerEmailActivation(@PathVariable("userId") Long userId)
    {
        userService.sendActivationEmail(userId);
    }

    @PutMapping("/block/{userId}")
    public void blockUser(@PathVariable("userId") Long userId)
    {
        userService.blockUser(userId);
    }

    @PutMapping("/unblock/{userId}")
    public void unblockUser(@PathVariable("userId") Long userId)
    {
        userService.unblockUser(userId);
    }

    @Validated(ActivateAccountGroup.class)
    @PutMapping(value = "/activate", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void activateAccount(@Valid @RequestBody UserDTO user)
    {
        userService.activateAccount(user);
    }

    @GetMapping("/send-reset-password-email/{userId}")
    public void envoyerEmailReinitialisation(@PathVariable("userId") Long userId)
    {
        userService.sendResetPasswordEmail(userId);
    }

    @PostMapping(value = "/open/send-reset-password-email", produces = MediaType.APPLICATION_JSON_VALUE)
    public void envoyerEmailReinitialisation(@Valid @ConvertGroup(to = SendResetPasswordEmailGroup.class) @RequestBody UserDTO dto)
    {
        userService.sendResetPasswordEmail(dto.getEmail());
    }

    @GetMapping("/search")
    public Page<UserDTO> searchUsers(
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "strId", required = false) Long strId,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size)
    {
        return userService.searchUsers(key, strId, PageRequest.of(page, size));
    }

    @GetMapping("/list/visible")
    @RolesAllowed("GET_USR")
    public java.util.List<UserDTO> getVisibleUsers()
    {
        return userService.getVisibleUsers();
    }
}