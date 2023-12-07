package smileksey.quotesapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import smileksey.quotesapp.models.Quote;

import java.util.List;

@Repository
public interface QuotesRepository extends JpaRepository<Quote, Integer> {
    List<Quote> findTop10ByOrderByVotesDesc();
    List<Quote> findTop10ByOrderByVotesAsc();

}
