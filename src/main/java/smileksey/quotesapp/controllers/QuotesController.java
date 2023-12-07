package smileksey.quotesapp.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import smileksey.quotesapp.dto.QuoteDto;
import smileksey.quotesapp.exceptions.QuoteNotFoundException;
import smileksey.quotesapp.exceptions.QuoteNotSavedException;
import smileksey.quotesapp.models.Quote;
import smileksey.quotesapp.models.User;
import smileksey.quotesapp.services.QuotesService;
import smileksey.quotesapp.services.UsersService;
import smileksey.quotesapp.util.ErrorResponse;
import smileksey.quotesapp.util.QuoteValidator;
import smileksey.quotesapp.util.ValidationErrorMessage;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/quotes")
public class QuotesController {

    private final QuotesService quotesService;
    private final UsersService usersService;
    private final QuoteValidator validator;

    @Autowired
    public QuotesController(QuotesService quotesService, UsersService usersService, QuoteValidator validator) {
        this.quotesService = quotesService;
        this.usersService = usersService;
        this.validator = validator;
    }

    @PostMapping("/add")
    public ResponseEntity<HttpStatus> addQuote(@RequestBody @Valid QuoteDto quoteDto, BindingResult bindingResult) {

        validator.validate(quoteDto, bindingResult);

        if (bindingResult.hasErrors()) {
            String errorMessage = ValidationErrorMessage.createMessage(bindingResult.getFieldErrors());
            throw new QuoteNotSavedException(errorMessage);
        }

        quotesService.add(convertToQuote(quoteDto));

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public QuoteDto getQuote(@PathVariable("id") int id) {
        return convertToQuoteDto(quotesService.findById(id));
    }

    @GetMapping("/random")
    public QuoteDto getRandomQuote() {
        return convertToQuoteDto(quotesService.findRandomQuote());
    }

    @GetMapping("/top10")
    public List<QuoteDto> getTopTen() {
        return quotesService.findTopTen().stream().map(quote -> convertToQuoteDto(quote)).collect(Collectors.toList());
    }

    @GetMapping("/worst10")
    public List<QuoteDto> getWorstTen() {
        return quotesService.findWorstTen().stream().map(quote -> convertToQuoteDto(quote)).collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") int id) {

        quotesService.delete(id);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<HttpStatus> update(@RequestBody @Valid QuoteDto quoteDto, BindingResult bindingResult, @PathVariable("id") int id) {

        validator.validate(quoteDto, bindingResult);

        if (bindingResult.hasErrors()) {
            String errorMessage = ValidationErrorMessage.createMessage(bindingResult.getFieldErrors());
            throw new QuoteNotSavedException(errorMessage);
        }

        quotesService.update(convertToQuote(quoteDto), id);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PatchMapping("/{id}/upvote")
    public ResponseEntity<HttpStatus> upvote(@PathVariable("id") int id) {

        quotesService.upvote(id);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PatchMapping("/{id}/downvote")
    public ResponseEntity<HttpStatus> downvote(@PathVariable("id") int id) {

        quotesService.downvote(id);

        return ResponseEntity.ok(HttpStatus.OK);
    }


    private Quote convertToQuote(QuoteDto quoteDto) {
        Quote quote = new Quote();

        quote.setContent(quoteDto.getContent());

        User user = new User();
        user.setEmail(quoteDto.getUserEmail());

        quote.setUser(user);

        return quote;
    }

    private QuoteDto convertToQuoteDto(Quote quote) {
        QuoteDto quoteDto = new QuoteDto();

        quoteDto.setContent(quote.getContent());
        quoteDto.setVotes(quote.getVotes());
        quoteDto.setDateOfCreation(quote.getDateOfCreation());
        quoteDto.setDateOfUpdate(quote.getDateOfUpdate());
        quoteDto.setUserName(quote.getUser().getName());
        quoteDto.setUserEmail(quote.getUser().getEmail());

        return quoteDto;
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(QuoteNotSavedException e) {

        ErrorResponse response = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(QuoteNotFoundException e) {

        ErrorResponse response = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
