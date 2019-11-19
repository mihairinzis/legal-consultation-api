package com.code4ro.legalconsultation.service.impl;

import com.code4ro.legalconsultation.model.dto.UniqueEmailConstraint;
import com.code4ro.legalconsultation.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class UserEmailValidator implements ConstraintValidator<UniqueEmailConstraint, String> {
    private final UserRepository userRepository;

    @Autowired
    public UserEmailValidator(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void initialize(final UniqueEmailConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(final String email, final ConstraintValidatorContext constraintValidatorContext) {
        return userRepository.findByEmail(email).isEmpty();
    }
}
