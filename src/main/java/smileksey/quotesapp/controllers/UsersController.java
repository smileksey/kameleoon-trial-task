package smileksey.quotesapp.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import smileksey.quotesapp.dto.UserDto;
import smileksey.quotesapp.exceptions.UserNotRegisteredException;
import smileksey.quotesapp.models.User;
import smileksey.quotesapp.services.UsersService;
import smileksey.quotesapp.util.ErrorResponse;
import smileksey.quotesapp.util.UserValidator;
import smileksey.quotesapp.util.ValidationErrorMessage;

@RestController
@RequestMapping("/users")
public class UsersController {

    private final UsersService usersService;
    private final UserValidator validator;

    @Autowired
    public UsersController(UsersService usersService, UserValidator validator) {
        this.usersService = usersService;
        this.validator = validator;
    }

    @PostMapping("/register")
    public ResponseEntity<HttpStatus> register(@RequestBody @Valid UserDto userDto, BindingResult bindingResult) {

        validator.validate(userDto, bindingResult);

        if (bindingResult.hasErrors()) {
            String errorMessage = ValidationErrorMessage.createMessage(bindingResult.getFieldErrors());
            throw new UserNotRegisteredException(errorMessage);
        }

        usersService.createUser(converToUser(userDto));

        return ResponseEntity.ok(HttpStatus.OK);
    }

    private User converToUser(UserDto userDto) {
        User user = new User();

        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());

        return user;
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(UserNotRegisteredException e) {

        ErrorResponse response = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
