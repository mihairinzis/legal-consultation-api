package com.code4ro.legalconsultation.core.authorization;

import com.code4ro.legalconsultation.security.model.CurrentUser;
import com.code4ro.legalconsultation.user.model.persistence.UserRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
public class Authorization {

    public boolean isSuperUser() {
        final UserDetails currentUser = getCurrentUser();
        if (currentUser == null) {
            return false;
        }
        return currentUser.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(auth -> auth.equals(UserRole.ADMIN.name()) || auth.equals(UserRole.OWNER.name()));
    }

    public boolean isSuperUserOrMe(final UUID userId) {
        return isSuperUser() || Objects.equals(userId, getCurrentUserId());
    }

    public UUID getCurrentUserId() {
        final UserDetails currentUser = getCurrentUser();
        return currentUser instanceof CurrentUser ? ((CurrentUser) currentUser).getId() : null;
    }

    private UserDetails getCurrentUser() {
        final SecurityContext securityContext = SecurityContextHolder.getContext();
        final Authentication auth = securityContext.getAuthentication();
        if (auth == null) {
            return null;
        }
        final Object principal = auth.getPrincipal();
        return principal instanceof UserDetails ? (UserDetails) principal : null;
    }
}
