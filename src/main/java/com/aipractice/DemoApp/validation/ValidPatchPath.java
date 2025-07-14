package com.aipractice.DemoApp.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = PathFieldValidator.class)
@Target({ FIELD })
@Retention(RUNTIME)
public @interface ValidPatchPath {
    String message() default "Invalid field for patching.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}