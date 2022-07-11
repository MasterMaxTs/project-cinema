package ru.job4j.cinema.persistences;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import ru.job4j.cinema.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Repository
public class UsersDBStore {

    private final BasicDataSource pool;
    private static final Logger LOG = Logger.getLogger(UsersDBStore.class);

    public UsersDBStore(BasicDataSource pool) {
        this.pool = pool;
    }

    public Optional<User> addUser(User user) {
        Optional<User> userInDb = findOldUserInDb(user);
        if (userInDb.isPresent()) {
            return userInDb;
        }
        Optional<User> rsl;
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
                rsl = Optional.of(user);
            }
        } catch (SQLException e) {
            LOG.error("Ошибка: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
        LOG.info("Успешно");
        return rsl;
    }

    public Optional<User> findUserInDbByEmailAndPhone(User user) {
        Optional<User> rsl = Optional.empty();
        String sql = "SELECT * FROM users WHERE email = ? AND phone = ?";
        LOG.info("Попытка найти пользователя по email или телефону в таблице users из БД");
        try (Connection cn = pool.getConnection()) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getPhone());
            ps.execute();
            try (ResultSet it = ps.executeQuery()) {
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
        } catch (SQLException e) {
            LOG.error("Ошибка: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
        LOG.info("Успешно");
        return rsl;
    }

    private Optional<User> findOldUserInDb(User user) {
        return findUserInDbByEmailAndPhone(user);
    }
}
