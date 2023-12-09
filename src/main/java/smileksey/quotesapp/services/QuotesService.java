package smileksey.quotesapp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smileksey.quotesapp.exceptions.QuoteNotFoundException;
import smileksey.quotesapp.exceptions.QuoteNotSavedException;
import smileksey.quotesapp.models.Quote;
import smileksey.quotesapp.models.User;
import smileksey.quotesapp.repositories.QuotesRepository;

import java.util.Date;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class QuotesService {

    private final QuotesRepository quotesRepository;
    private final UsersService usersService;

    @Autowired
    public QuotesService(QuotesRepository quotesRepository, UsersService usersService) {
        this.quotesRepository = quotesRepository;
        this.usersService = usersService;
    }

    //добавить новую цитату в БД
    @Transactional
    public void add(Quote newQuote) {
        enrichNewQuoteData(newQuote);
        quotesRepository.save(newQuote);
    }

    //изменить существующую цитату в БД
    @Transactional
    public void update(Quote updatedQuote, int id) {

        Quote actualQuote = quotesRepository.findById(id).orElseThrow(() -> new QuoteNotFoundException("Quote with this ID is not found"));

        if (!actualQuote.getUser().getEmail().equals(updatedQuote.getUser().getEmail())) {
            throw new QuoteNotSavedException("This quote was created by another user. You cannot modify it.");
        }

        checkCredentialsAndReturnUserFromDb(updatedQuote);

        actualQuote.setContent(updatedQuote.getContent());
        actualQuote.setDateOfUpdate(new Date());

        quotesRepository.save(actualQuote);
    }

    //получить цитату из БД по id
    public Quote findById(int id) {
        return quotesRepository.findById(id).orElseThrow(() -> new QuoteNotFoundException("Quote with this ID is not found"));
    }

    //получить случайную цитату из БД
    public Quote findRandomQuote() {
        //находим количество записей в таблице quote
        long quotesQty = quotesRepository.count();
        //вычисляем случайный индекс в пределах найденного количества записей
        int index = (int) (Math.random() * quotesQty);
        //используем пагинацию, чтобы вытащить одну запись со случайным индексом
        Page<Quote> quotePage = quotesRepository.findAll(PageRequest.of(index, 1));

        Quote randomQuote = null;

        if (quotePage.hasContent()) {
            randomQuote = quotePage.getContent().get(0);
        }

        return randomQuote;
    }

    //голосовать "за" конкретную цитату
    @Transactional
    public void upvote(int id) {

        Quote quote = quotesRepository.findById(id).orElseThrow(() -> new QuoteNotFoundException("Quote with this ID is not found"));

        quote.setVotes(quote.getVotes() + 1);
        quotesRepository.save(quote);
    }

    //голосовать "против" конкретной цитаты
    @Transactional
    public void downvote(int id) {

        Quote quote = quotesRepository.findById(id).orElseThrow(() -> new QuoteNotFoundException("Quote with this ID is not found"));

        if (quote.getVotes() > 0) {
            quote.setVotes(quote.getVotes() - 1);
            quotesRepository.save(quote);
        }
    }

    //получить топ 10 цитат с наилучшими оценками из БД
    public List<Quote> findTopTen() {
        return quotesRepository.findTop10ByOrderByVotesDesc();
    }

    //получить топ 10 цитат с наихудшими оценками из БД
    public List<Quote> findWorstTen() {
        return quotesRepository.findTop10ByOrderByVotesAsc();
    }

    //удалить конкретную цитату из БД по id
    @Transactional
    public void delete(int id) {
        quotesRepository.deleteById(id);
    }

    //Добавить недостающие данные в объект новой цитаты, пришедшей от клиента (полные данные пользователя и дату создания)
    private void enrichNewQuoteData(Quote quote) {

        User actualUser = checkCredentialsAndReturnUserFromDb(quote);
        quote.setUser(actualUser);

        quote.setDateOfCreation(new Date());
    }


    //проверить корректность данных о пользователе, которые клиент указал при создании/модификации цитаты (email и пароль)
    //если данные верны - вернуть соответсвтующий объект User из БД
    private User checkCredentialsAndReturnUserFromDb(Quote quote) {

        User specifiedByClientUser = quote.getUser();

        User actualUser = usersService.findByEmail(specifiedByClientUser.getEmail())
                .orElseThrow(() -> new QuoteNotSavedException("User with this email is not found"));

        if (!actualUser.getPassword().equals(specifiedByClientUser.getPassword())) {
            throw new QuoteNotSavedException("Incorrect password");
        }

        return actualUser;
    }





}
