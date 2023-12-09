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

    //сохранение нового пользователя в БД
    @Transactional
    public void createUser(User user) {
        enrichUserData(user);
        usersRepository.save(user);
    }

    //получить зарегестрированного пользователя из БД по email
    public Optional<User> findByEmail(String email) {
        return usersRepository.findByEmail(email);
    }


    //добавление недостающей информации в объект User - дата регистрации
    private void enrichUserData(User user) {
        user.setDateOfCreation(new Date());
    }
}
