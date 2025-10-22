package lenicorp.admin.security.model.validators;

import lenicorp.admin.security.model.dtos.CreateUserDTO;
import lenicorp.admin.security.model.dtos.UserProfileAssoDTO;
import jakarta.persistence.EntityManager;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;
import java.util.Objects;

@Target({ElementType.FIELD, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {ProfileMaxAssignation.ProfileMaxAssignationValidator.class, ProfileMaxAssignation.CreateUserProfileMaxAssignationValidator.class})
@Documented
@Repeatable(ProfileMaxAssignation.List.class)
public @interface ProfileMaxAssignation {
    String message() default "Le nombre maximum d'assignations pour ce profil a été atteint";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        ProfileMaxAssignation[] value();
    }

    @Component
    @RequiredArgsConstructor
    class ProfileMaxAssignationValidator implements ConstraintValidator<ProfileMaxAssignation, UserProfileAssoDTO> {
        private final EntityManager entityManager;

        @Override
        public boolean isValid(UserProfileAssoDTO dto, ConstraintValidatorContext context) {
            if (dto == null || dto.getProfileCode() == null) return true;

            // 1. Get the profileMaxAssignation value for the profile
            String maxAssignationQuery = "SELECT vp.profileMaxAssignation FROM VProfile vp WHERE vp.code = :profileCode";
            Long maxAssignation = entityManager.createQuery(maxAssignationQuery, Long.class)
                    .setParameter("profileCode", dto.getProfileCode())
                    .getResultList()
                    .stream().filter(Objects::nonNull)
                    .findFirst()
                    .orElse(0L);

            // If maxAssignation is null or 0, there's no limit
            if (maxAssignation == null || maxAssignation == 0) {
                return true;
            }

            // 2. Count the current number of assignments for this profile
            String countQuery = "SELECT COUNT(vp) FROM VUserProfile vp WHERE vp.profileCode = :profileCode";
            Long currentAssignations = entityManager.createQuery(countQuery, Long.class)
                    .setParameter("profileCode", dto.getProfileCode())
                    .getSingleResult();

            // 3. Check if adding one more would exceed the maximum
            // If we're updating an existing assignment, don't count it twice
            if (dto.getId() != null) {
                String existingQuery = "SELECT COUNT(vp) FROM VUserProfile vp WHERE vp.profileCode = :profileCode AND vp.userId = :userId AND vp.profileStrId = :strId";
                Long existingCount = entityManager.createQuery(existingQuery, Long.class)
                        .setParameter("profileCode", dto.getProfileCode())
                        .setParameter("userId", dto.getUserId())
                        .setParameter("strId", dto.getStrId())
                        .getSingleResult();

                // If this is an existing assignment, don't count it in the total
                if (existingCount > 0) {
                    currentAssignations--;
                }
            }

            // Check if adding one more would exceed the maximum
            return currentAssignations < maxAssignation;
        }
    }

    @Component
    @RequiredArgsConstructor
    class CreateUserProfileMaxAssignationValidator implements ConstraintValidator<ProfileMaxAssignation, CreateUserDTO> {
        private final EntityManager entityManager;

        @Override
        public boolean isValid(CreateUserDTO dto, ConstraintValidatorContext context) {
            if (dto == null || dto.getProfileCode() == null) return true;

            // 1. Get the profileMaxAssignation value for the profile
            String maxAssignationQuery = "SELECT vp.profileMaxAssignation FROM VProfile vp WHERE vp.code = :profileCode";
            Long maxAssignation = entityManager.createQuery(maxAssignationQuery, Long.class)
                    .setParameter("profileCode", dto.getProfileCode())
                    .getResultList()
                    .stream().filter(Objects::nonNull)
                    .findFirst()
                    .orElse(0L);

            // If maxAssignation is null or 0, there's no limit
            if (maxAssignation == null || maxAssignation == 0) {
                return true;
            }

            // 2. Count the current number of assignments for this profile in the same structure
            String countQuery = "SELECT COUNT(vp) FROM VUserProfile vp WHERE vp.profileCode = :profileCode";
            Long currentAssignations = entityManager.createQuery(countQuery, Long.class)
                    .setParameter("profileCode", dto.getProfileCode())
                    .getSingleResult();

            // Check if adding one more would exceed the maximum
            return currentAssignations < maxAssignation;
        }
    }
}
