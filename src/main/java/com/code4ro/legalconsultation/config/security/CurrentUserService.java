package com.code4ro.legalconsultation.config.security;

import com.code4ro.legalconsultation.model.persistence.ApplicationUser;
import com.code4ro.legalconsultation.service.impl.ApplicationUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    private final ApplicationUserService applicationUserService;

    @Autowired
    public CurrentUserService(ApplicationUserService applicationUserService) {
        this.applicationUserService = applicationUserService;
    }

    public ApplicationUser getCurrentUser() {
        final SecurityContext securityContext = SecurityContextHolder.getContext();
        final Authentication auth = securityContext.getAuthentication();
        if (auth == null) {
            return null;
        }
        final Object principal = auth.getPrincipal();
        if (principal instanceof UserDetails) {
            return applicationUserService.getByUsernameOrEmail(((UserDetails) principal).getUsername());
        }
        return null;
    }
}
