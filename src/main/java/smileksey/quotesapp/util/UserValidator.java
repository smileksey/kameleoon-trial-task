package smileksey.quotesapp.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import smileksey.quotesapp.dto.UserDto;
import smileksey.quotesapp.services.UsersService;

//валидатор для приходящих от клиента объектов UserDto
@Component
public class UserValidator implements Validator {

    private final UsersService usersService;

    @Autowired
    public UserValidator(UsersService usersService) {
        this.usersService = usersService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return UserDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserDto user = (UserDto) target;

        if(usersService.findByEmail(user.getEmail()).isPresent()) {
            errors.rejectValue("email", "", "User with this e-mail already exists");
        }
    }

}
