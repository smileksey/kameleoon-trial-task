package smileksey.quotesapp.exceptions;

public class QuoteNotSavedException extends RuntimeException {
    public QuoteNotSavedException(String message) {
        super(message);
    }
}
