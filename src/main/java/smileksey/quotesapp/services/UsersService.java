package smileksey.quotesapp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smileksey.quotesapp.models.User;
import smileksey.quotesapp.repositories.UsersRepository;

import java.util.Date;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UsersService {

    private final UsersRepository usersRepository;

    @Autowired
    public UsersService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Transactional
    public void createUser(User user) {
        enrichUserData(user);
        usersRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return usersRepository.findByEmail(email);
    }



    private void enrichUserData(User user) {
        user.setDateOfCreation(new Date());
    }
}
