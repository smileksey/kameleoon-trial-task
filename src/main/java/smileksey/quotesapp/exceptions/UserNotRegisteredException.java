package smileksey.quotesapp.exceptions;

public class UserNotRegisteredException extends RuntimeException{
    public UserNotRegisteredException(String message) {
        super(message);
    }
}
