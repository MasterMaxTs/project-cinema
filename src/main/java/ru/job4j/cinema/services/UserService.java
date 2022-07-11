package ru.job4j.cinema.services;

import org.springframework.stereotype.Service;
import ru.job4j.cinema.models.User;
import ru.job4j.cinema.persistences.UsersDBStore;

import java.util.Optional;

@Service
public class UserService {

    private final UsersDBStore store;

    public UserService(UsersDBStore store) {
        this.store = store;
    }

    public Optional<User> addUser(User user) {
        return store.addUser(user);
    }
}
