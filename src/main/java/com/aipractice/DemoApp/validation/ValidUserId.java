package com.aipractice.DemoApp.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidUserIdValidator.class)
@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUserId {
    String message() default "User ID must be numeric and less than 12 digits.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

