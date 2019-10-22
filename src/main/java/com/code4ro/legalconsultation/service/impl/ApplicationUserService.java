package com.code4ro.legalconsultation.service.impl;

import com.code4ro.legalconsultation.common.exceptions.LegalValidationException;
import com.code4ro.legalconsultation.model.dto.SignUpRequest;
import com.code4ro.legalconsultation.model.persistence.ApplicationUser;
import com.code4ro.legalconsultation.model.persistence.User;
import com.code4ro.legalconsultation.model.persistence.UserRole;
import com.code4ro.legalconsultation.repository.ApplicationUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ApplicationUserService {
    private final ApplicationUserRepository applicationUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @Autowired
    public ApplicationUserService(final ApplicationUserRepository applicationUserRepository,
                                  final PasswordEncoder passwordEncoder,
                                  final UserService userService) {
        this.applicationUserRepository = applicationUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    @Transactional
    @CachePut(cacheNames = "users")
    public ApplicationUser save(SignUpRequest signUpRequest) throws LegalValidationException {
        if (applicationUserRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new LegalValidationException("register.Duplicate.username", HttpStatus.CONFLICT);
        }
        final ApplicationUser applicationUser = new ApplicationUser(signUpRequest.getName(),
                signUpRequest.getUsername(), signUpRequest.getPassword());
        applicationUser.setPassword(passwordEncoder.encode(applicationUser.getPassword()));
        final User user = getUser(signUpRequest.getEmail());
        applicationUser.setUser(user);
        return applicationUserRepository.save(applicationUser);
    }

    @Transactional(readOnly = true)
    public ApplicationUser getByUsernameOrEmail(final String usernameOrEmail) {
        return applicationUserRepository.findByUsernameOrEmail(usernameOrEmail).orElseThrow(() ->
                new LegalValidationException("login.Bad.credentials", HttpStatus.UNAUTHORIZED)
        );
    }

    private User getUser(final String email) {
        final User user = userService.findByEmail(email);
        if (user == null) {
            return userService.saveEntity(new User(email, UserRole.CONTRIBUTOR));
        }
        if (applicationUserRepository.findById(user.getId()).isPresent()) {
            // there's already an application user linked to the user with the provided email
            throw new LegalValidationException("register.Duplicate.email", HttpStatus.CONFLICT);
        }
        return user;
    }

}
