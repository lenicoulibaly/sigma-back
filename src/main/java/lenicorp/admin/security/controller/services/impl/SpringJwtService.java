package lenicorp.admin.security.controller.services.impl;

import lenicorp.admin.exceptions.AppException;
import lenicorp.admin.security.controller.repositories.AuthAssoRepo;
import lenicorp.admin.security.controller.repositories.UserRepo;
import lenicorp.admin.security.controller.services.specs.IJwtService;
import lenicorp.admin.security.model.dtos.AuthResponse;
import lenicorp.admin.security.model.entities.AppUser;
import lenicorp.admin.security.model.views.VUserProfile;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class SpringJwtService implements IJwtService
{
    private final UserRepo userRepo;
    private final AuthAssoRepo authAssoRepo;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.access-token-duration:3600}")
    private long accessTokenDuration;

    @Value("${jwt.refresh-token-duration:604800}")
    private long refreshTokenDuration;

    @Override
    public String generateAccessToken(AppUser user)
    {
        user = checkUserNullity(user);
        Long userId = user.getUserId();
        String username = user.getEmail();
        Long strId = user.getStructure() != null ? user.getStructure().getStrId() : null;

        Set<String> authorities = authAssoRepo.findAuthoritiesByUsername(username);
        VUserProfile userProfile = authAssoRepo.findUserCurrentProfile(username);

        Map<String, Object> claims = new HashMap<>();
        claims.put("firstName", user.getFirstName());
        claims.put("lastName", user.getLastName());
        claims.put("userStrId", strId);
        claims.put("authorities", authorities);

        if (userProfile != null)
        {
            claims.put("currentProfileCode", userProfile.getProfileCode());
            claims.put("currentProfileName", userProfile.getProfileName());
            claims.put("currentProfileStrId", userProfile.getUserStrId());
            claims.put("profileStrId", userProfile.getAssStrId());
            claims.put("profileStrName", userProfile.getAssStrName());
            claims.put("profileStrSigles", userProfile.getAssStrSigles());
            claims.put("profileStrChaineSigles", userProfile.getAssStrChaineSigles());
            claims.put("connexionId", UUID.randomUUID().toString());
        }

        return buildToken(claims, username, userId.toString(), accessTokenDuration * 24 * 30 * 12);
    }

    @Override
    public String generateRefreshToken(AppUser user)
    {
        user = checkUserNullity(user);
        return buildToken(new HashMap<>(), user.getEmail(), user.getUserId().toString(), refreshTokenDuration);
    }

    private String buildToken(Map<String, Object> extraClaims, String username, String subject, long expiration) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(subject)
                .setIssuer("sigma-app")
                .claim("upn", username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSigningKey()
    {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private AppUser checkUserNullity(AppUser user)
    {
        if (user == null || (user.getUserId() == null && user.getEmail() == null))
            throw new AppException("User must not be null and must have a valid userId");
        if (user.getEmail() == null) user = userRepo.findById(user.getUserId()).orElseThrow(()-> new AppException("User must have an email"));
        if (user.getUserId() == null) user = userRepo.findByUsername(user.getEmail());
        return user;
    }

    @Override
    public AuthResponse getTokens(AppUser user)
    {
        String accessToken = generateAccessToken(user);
        String refreshToken = generateRefreshToken(user);
        return new AuthResponse(accessToken, refreshToken);
    }

    @Override
    public AppUser getCurrentUser()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof UserDetails) {
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            return userRepo.findByUsername(username);
        }
        AppUser anonymousUser = new AppUser();
        anonymousUser.setUserId(-1L);
        anonymousUser.setEmail("anonymous");
        anonymousUser.setActivated(true);
        anonymousUser.setNotBlocked(true);
        anonymousUser.setFirstName("anonymous");
        anonymousUser.setLastName("anonymous");
        return anonymousUser;
    }

    @Override
    public VUserProfile getCurrentUserProfile()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof UserDetails) {
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            return authAssoRepo.findUserCurrentProfile(username);
        }
        return null;
    }

    @Override
    public Long getCurrentUserProfileStrId()
    {
        VUserProfile userProfile = getCurrentUserProfile();
        return userProfile == null ? null : userProfile.getAssStrId();
    }

    @Override
    public Object getClaimFromToken(String claimName)
    {
        String token = getCurrentToken();
        if (token == null) return null;

        try {
            Claims claims = extractAllClaims(token);
            return claims.get(claimName);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getCurrentToken()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getDetails() instanceof String) {
            return (String) authentication.getDetails();
        }
        return null;
    }

    @Override
    public boolean hasPrivilege(String privilegeCode)
    {
        if (privilegeCode == null || privilegeCode.isBlank()) return true;
        Object groupsClaim = getClaimFromToken("authorities");
        if (groupsClaim instanceof Collection<?> authorities) {
            return authorities.contains(privilegeCode);
        }
        return false;
    }

    // Additional methods for token validation and extraction
    public String extractUsername(String token)
    {
        return extractClaim(token, claims -> claims.get("upn", String.class));
    }

    public String extractSubject(String token)
    {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token)
    {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver)
    {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token)
    {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenValid(String token, UserDetails userDetails)
    {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token)
    {
        return extractExpiration(token).before(new Date());
    }
}