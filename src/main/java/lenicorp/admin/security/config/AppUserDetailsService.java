package lenicorp.admin.security.config;

import lenicorp.admin.security.controller.repositories.AuthAssoRepo;
import lenicorp.admin.security.controller.repositories.UserRepo;
import lenicorp.admin.security.model.dtos.UserDTO;
import lenicorp.admin.security.model.entities.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService
{
    private final UserRepo userRepo;
    private final AuthAssoRepo authAssoRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        UserDTO user = userRepo.findMinimalByUsername(username);

        if (user == null)
        {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        // Check if user is activated and not blocked
        if (!user.isActivated())
        {
            throw new UsernameNotFoundException("User account is not activated");
        }

        if (!user.isNotBlocked())
        {
            throw new UsernameNotFoundException("User account is blocked");
        }

        // Get user authorities
        Set<String> authorities = authAssoRepo.findAuthoritiesByUsername(username);

        // Convert to Spring Security SimpleGrantedAuthority
        Set<SimpleGrantedAuthority> grantedAuthorities = authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        // Create Spring Security User
        return new User(
                user.getEmail(),
                user.getPassword(),
                user.isActivated(),
                true, // account not expired
                true, // credentials not expired
                user.isNotBlocked(),
                grantedAuthorities
        );
    }
}
