package com.user.app.server.filter;

import com.user.app.server.model.User;
import com.user.app.server.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.Optional;

@Component
@AllArgsConstructor
public class LoginValidator implements Validator {
    @Autowired
    private UserRepository userRepository;

    private PasswordEncoder encoder;

    @Override
    public boolean supports(Class<?> aClass) {
        return User.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        User user = (User) o;

        Optional<User> serviceUser = userRepository.findByUsername(user.getUsername());

        boolean passwordEquals;
        passwordEquals = serviceUser
                .filter(value ->
                        encoder.matches(user.getPassword(), value.getPassword())
                ).isPresent();

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "NotEmpty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "NotEmpty");
        if (serviceUser.isEmpty() || !passwordEquals) {
            errors.rejectValue("username", "error");
        }
    }
}