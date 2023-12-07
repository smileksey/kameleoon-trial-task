package smileksey.quotesapp.exceptions;

public class QuoteNotFoundException extends RuntimeException{
    public QuoteNotFoundException(String message) {
        super(message);
    }
}
