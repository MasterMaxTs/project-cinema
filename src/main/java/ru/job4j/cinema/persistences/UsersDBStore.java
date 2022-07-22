package ru.job4j.cinema.persistences;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import ru.job4j.cinema.models.Session;
import ru.job4j.cinema.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class UsersDBStore {

    private final BasicDataSource pool;
    private static final Logger LOG = Logger.getLogger(UsersDBStore.class);

    public UsersDBStore(BasicDataSource pool) {
        this.pool = pool;
    }

    public Optional<User> addUser(User user) {
        LOG.info("Попытка найти пользователя в БД");
        Optional<User> userInDb = updateUser(user);
        if (userInDb.isPresent()) {
            return userInDb;
        }
        LOG.info("Пользователь не найден в БД");
        String sql = "INSERT INTO users (username, email, phone)"
                + "VALUES (? , ? , ?)";
        LOG.info("Попытка добавить пользователя в БД");
        try (Connection cn = pool.getConnection()) {
            PreparedStatement ps = cn.prepareStatement(
                    sql, PreparedStatement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            ps.execute();
            try (ResultSet it = ps.getGeneratedKeys()) {
                if (it.next()) {
                    user.setId(it.getInt("id"));
                }
                userInDb = Optional.of(user);
            }
        } catch (SQLException e) {
            LOG.error("Ошибка: " + e.getMessage(), e);
        }
        LOG.info("Добавлен новый пользователь в БД");
        return userInDb;
    }

    private Optional<User> updateUser(User user) {
        Optional<User> rsl = Optional.empty();
        String sql = "UPDATE users SET phone = ? WHERE email = ?";
        LOG.info("Попытка обновить телефон зарегистрированному пользователю в БД");
        try (Connection cn = pool.getConnection()) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setString(1, user.getPhone());
            ps.setString(2, user.getEmail());
            if (ps.executeUpdate() > 0) {
                sql = "SELECT * FROM users WHERE email = ?";
                PreparedStatement st = cn.prepareStatement(sql);
                st.setString(1, user.getEmail());
                try (ResultSet it = st.executeQuery()) {
                    if (it.next()) {
                        rsl = Optional.of(
                                new User(it.getInt("id"),
                                        it.getString("username"),
                                        it.getString("email"),
                                        it.getString("phone")
                                )
                        );
                    }
                }
                LOG.info("Телефон пользователя обновлен в БД");
            }
        } catch (SQLException e) {
            LOG.error("Ошибка: " + e.getMessage(), e);
        }
        return rsl;
    }


    public List<User> findAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        LOG.info("Попытка получить список всех пользователей из БД");
        try (Connection cn = pool.getConnection()) {
            PreparedStatement ps = cn.prepareStatement(sql);
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    users.add(
                            new User(
                                    it.getInt("id"),
                                    it.getString("name"),
                                    it.getString("email"),
                                    it.getString("phone")
                            )
                    );
                }
            }
        } catch (SQLException e) {
            LOG.error("Ошибка: " + e.getMessage(), e);
        }
        LOG.info("Успешно");
        return users;
    }
}
