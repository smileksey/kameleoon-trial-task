package smileksey.quotesapp.services;

import org.hibernate.query.spi.Limit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smileksey.quotesapp.exceptions.QuoteNotFoundException;
import smileksey.quotesapp.exceptions.QuoteNotSavedException;
import smileksey.quotesapp.models.Quote;
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

    @Transactional
    public void add(Quote quote) {
        enrichNewQuoteData(quote);
        quotesRepository.save(quote);
    }

    public Quote findById(int id) {
        return quotesRepository.findById(id).orElse(null);
    }

    public Quote findRandomQuote() {
        //находим количество записей в таблице quote
        long quotesQty = quotesRepository.count();
        //берем случайный индекс в пределах найденного количества
        int index = (int) (Math.random() * quotesQty);
        //используем пагинацию, чтобы вытащить 1 запись с данным индексом
        Page<Quote> quotePage = quotesRepository.findAll(PageRequest.of(index, 1));

        Quote randomQuote = null;

        if (quotePage.hasContent()) {
            randomQuote = quotePage.getContent().get(0);
        }

        return randomQuote;
    }

    @Transactional
    public void update(Quote updatedQuote, int id) {

        Quote quote = quotesRepository.findById(id).orElseThrow(() -> new QuoteNotFoundException("Quote with this ID is not found"));

        quote.setContent(updatedQuote.getContent());
        quote.setDateOfUpdate(new Date());
        quotesRepository.save(quote);
    }

    @Transactional
    public void upvote(int id) {

        Quote quote = quotesRepository.findById(id).orElseThrow(() -> new QuoteNotFoundException("Quote with this ID is not found"));

        quote.setVotes(quote.getVotes() + 1);
        quotesRepository.save(quote);
    }

    @Transactional
    public void downvote(int id) {

        Quote quote = quotesRepository.findById(id).orElseThrow(() -> new QuoteNotFoundException("Quote with this ID is not found"));

        if (quote.getVotes() > 0) {
            quote.setVotes(quote.getVotes() - 1);
            quotesRepository.save(quote);
        }
    }

    public List<Quote> findTopTen() {
        return quotesRepository.findTop10ByOrderByVotesDesc();
    }

    public List<Quote> findWorstTen() {
        return quotesRepository.findTop10ByOrderByVotesAsc();
    }

    @Transactional
    public void delete(int id) {
        quotesRepository.deleteById(id);
    }

    private void enrichNewQuoteData(Quote quote) {
        quote.setDateOfCreation(new Date());
        quote.setUser(usersService.findByEmail(quote.getUser().getEmail())
                .orElseThrow(() -> new QuoteNotSavedException("User with this email is not found")));
    }




}
