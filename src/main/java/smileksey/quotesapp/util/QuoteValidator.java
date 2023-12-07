package smileksey.quotesapp.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import smileksey.quotesapp.dto.QuoteDto;
import smileksey.quotesapp.services.QuotesService;
import smileksey.quotesapp.services.UsersService;


@Component
public class QuoteValidator implements Validator {

    private final UsersService usersService;

    @Autowired
    public QuoteValidator(UsersService usersService) {
        this.usersService = usersService;
    }


    @Override
    public boolean supports(Class<?> clazz) {
        return QuoteDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        QuoteDto quote = (QuoteDto) target;

        if(usersService.findByEmail(quote.getUserEmail()).isEmpty()) {
            errors.rejectValue("userEmail", "", "User with this email is not found. Register first.");
        }
    }

}
