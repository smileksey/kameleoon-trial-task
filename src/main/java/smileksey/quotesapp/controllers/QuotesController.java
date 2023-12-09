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

    //добавить новую цитату
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

    //получить цитату по id
    @GetMapping("/{id}")
    public QuoteDto getQuote(@PathVariable("id") int id) {
        return convertToQuoteDto(quotesService.findById(id));
    }

    //получить случайную цитату
    @GetMapping("/random")
    public QuoteDto getRandomQuote() {
        return convertToQuoteDto(quotesService.findRandomQuote());
    }

    //получить топ 10 цитат с наилучшими оценками
    @GetMapping("/top10")
    public List<QuoteDto> getTopTen() {
        return quotesService.findTopTen().stream().map(quote -> convertToQuoteDto(quote)).collect(Collectors.toList());
    }

    //получить топ 10 цитат с наихудшими оценками
    @GetMapping("/worst10")
    public List<QuoteDto> getWorstTen() {
        return quotesService.findWorstTen().stream().map(quote -> convertToQuoteDto(quote)).collect(Collectors.toList());
    }

    //удалить конкретную цитату
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") int id) {

        quotesService.delete(id);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    //изменить существующую цитату
    @PatchMapping("/{id}")
    public ResponseEntity<HttpStatus> update(@RequestBody @Valid QuoteDto quoteDto, BindingResult bindingResult, @PathVariable("id") int id) {

        if (bindingResult.hasErrors()) {
            String errorMessage = ValidationErrorMessage.createMessage(bindingResult.getFieldErrors());
            throw new QuoteNotSavedException(errorMessage);
        }

        quotesService.update(convertToQuote(quoteDto), id);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    //голосовать "за" конкретную цитату
    @PatchMapping("/{id}/upvote")
    public ResponseEntity<HttpStatus> upvote(@PathVariable("id") int id) {

        quotesService.upvote(id);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    //голосовать "против" конкретной цитаты
    @PatchMapping("/{id}/downvote")
    public ResponseEntity<HttpStatus> downvote(@PathVariable("id") int id) {

        quotesService.downvote(id);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    //конвертировать объект QuoteDto в объект Quote
    private Quote convertToQuote(QuoteDto quoteDto) {
        Quote quote = new Quote();

        quote.setContent(quoteDto.getContent());

        User user = new User();
        user.setEmail(quoteDto.getUserEmail());
        user.setPassword(quoteDto.getUserPassword());

        quote.setUser(user);

        return quote;
    }

    //конвертировать объект Quote в объект QuoteDto
    private QuoteDto convertToQuoteDto(Quote quote) {
        QuoteDto quoteDto = new QuoteDto();

        quoteDto.setId(quote.getId());
        quoteDto.setContent(quote.getContent());
        quoteDto.setVotes(quote.getVotes());
        quoteDto.setDateOfCreation(quote.getDateOfCreation());
        quoteDto.setDateOfUpdate(quote.getDateOfUpdate());
        quoteDto.setUserName(quote.getUser().getName());
        quoteDto.setUserEmail(quote.getUser().getEmail());

        return quoteDto;
    }

    //обработка исключения QuoteNotSavedException  - отправка сообщения об ошибке клиенту
    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(QuoteNotSavedException e) {

        ErrorResponse response = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    //обработка исключения QuoteNotFoundException  - отправка сообщения об ошибке клиенту
    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(QuoteNotFoundException e) {

        ErrorResponse response = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
