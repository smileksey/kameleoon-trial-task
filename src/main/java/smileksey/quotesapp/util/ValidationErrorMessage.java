package smileksey.quotesapp.util;

import org.springframework.validation.FieldError;

import java.util.List;

public class ValidationErrorMessage {
    public static String createMessage(List<FieldError> errors) {
        StringBuilder errorMsg = new StringBuilder();

        for (FieldError error : errors) {
            errorMsg.append("Field: '")
                    .append(error.getField())
                    .append("' - ")
                    .append(error.getDefaultMessage())
                    .append("; ");
        }

        return errorMsg.toString();
    }
}
