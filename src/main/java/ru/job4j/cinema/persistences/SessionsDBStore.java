package ru.job4j.cinema.persistences;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import ru.job4j.cinema.models.Session;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class SessionsDBStore {

    private final BasicDataSource pool;
    private static final Logger LOG = Logger.getLogger(SessionsDBStore.class);

    public SessionsDBStore(BasicDataSource pool) {
        this.pool = pool;
    }

    public List<Session> findAllSessions() {
        List<Session> sessions = new ArrayList<>();
        String sql = "SELECT * FROM sessions";
        LOG.info("Попытка получить список всех фильмов из БД");
        try (Connection cn = pool.getConnection()) {
            PreparedStatement ps = cn.prepareStatement(sql);
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    sessions.add(
                            new Session(
                                        it.getInt("id"),
                                        it.getString("name")
                                    )
                    );
                }
            }
        } catch (SQLException e) {
            LOG.error("Ошибка: " + e.getMessage(), e);
        }
        LOG.info("Успешно");
        return sessions;
    }

    public Optional<Session> findSessionById(int id) {
        Optional<Session> rsl = Optional.empty();
        String sql = "SELECT * FROM sessions WHERE id = ?";
        LOG.info("Попытка найти фильм по id из БД");
        try (Connection cn = pool.getConnection()) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setInt(1, id);
            try (ResultSet it = ps.executeQuery()) {
                if (it.next()) {
                    rsl = Optional.of(
                            new Session(
                                    it.getInt("id"),
                                    it.getString("name")
                            )
                    );
                }
            }
        } catch (SQLException e) {
            LOG.error("Ошибка: " + e.getMessage(), e);
        }
        LOG.info("Фильм найден");
        return rsl;
    }
}
