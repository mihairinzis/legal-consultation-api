package com.code4ro.legalconsultation.model.dto;

import com.code4ro.legalconsultation.service.impl.UserEmailValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UserEmailValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueEmailConstraint {
    String message() default "user.save.duplicatedEmail";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
