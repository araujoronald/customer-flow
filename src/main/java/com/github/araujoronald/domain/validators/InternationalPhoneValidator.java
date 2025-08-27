package com.github.araujoronald.domain.validators;

import com.github.araujoronald.domain.model.InternationalPhone;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class InternationalPhoneValidator implements ConstraintValidator<InternationalPhone, String> {

    private static final Pattern INTERNATIONAL_PHONE_PATTERN =
            // Matches E.164 format: a '+' followed by 1 to 15 digits.
            Pattern.compile("^\\+[1-9]\\d{1,14}$");

    @Override
    public boolean isValid(String phone, ConstraintValidatorContext context) {
        if (phone == null || phone.trim().isEmpty()) {
            // @NotBlank should be used on the field to enforce non-empty values.
            // This validator only checks the format if a value is present.
            return true;
        }

        return INTERNATIONAL_PHONE_PATTERN.matcher(phone).matches();
    }
}
