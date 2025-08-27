package com.github.araujoronald.domain.model;

import com.github.araujoronald.domain.validators.InternationalPhoneValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = InternationalPhoneValidator.class)
public @interface InternationalPhone {
    String message() default "{internationalPhone.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
