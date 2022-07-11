package ru.job4j.cinema.persistences;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import ru.job4j.cinema.models.Ticket;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class TicketsDBStore {

    private final BasicDataSource pool;
    private static final Logger LOG = Logger.getLogger(TicketsDBStore.class);

    public TicketsDBStore(BasicDataSource pool) {
        this.pool = pool;
    }

    public Optional<Ticket> createTicket(Ticket ticket) {
        Optional<Ticket> rsl = Optional.empty();
        String sql = "INSERT INTO tickets (session_id, pos_row, cell, user_id)"
                + " VALUES (? , ? , ? , ?)";
        LOG.info("Попытка добавить билет в БД");
        try (Connection cn = pool.getConnection()) {
            PreparedStatement ps = cn.prepareStatement(
                    sql, PreparedStatement.RETURN_GENERATED_KEYS
            );
            ps.setInt(1, ticket.getSessionId());
            ps.setInt(2, ticket.getRow());
            ps.setInt(3, ticket.getCell());
            ps.setInt(4, ticket.getUserId());
            ps.execute();
            try (ResultSet it = ps.getGeneratedKeys()) {
                if (it.next()) {
                    ticket.setId(it.getInt("id"));
                }
                rsl = Optional.of(ticket);
            }
        } catch (SQLException e) {
            LOG.error("Ошибка! Нарушение ограничения "
                    + "уникальности <ConstrainsViolationException> в таблице "
                    + "tickets из БД: " + e.getMessage(), e);
            return rsl;
        }
        LOG.info("Успешно!");
        return rsl;
    }

    public List<Ticket> findAllTickets(int sessionId) {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM tickets WHERE session_id = ?";
        LOG.info("Попытка получить из БД по id "
                + "киносеанса все проданные билеты");
        try (Connection cn = pool.getConnection()) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setInt(1, sessionId);
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    tickets.add(
                            new Ticket(it.getInt("id"),
                                       it.getInt("session_id"),
                                       it.getInt("pos_row"),
                                       it.getInt("cell"),
                                       it.getInt("user_id")
                            )
                    );
                }
            }
        } catch (SQLException e) {
            LOG.error("Ошибка: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
        LOG.info("Все проданные билеты по id киносеанса получены");
        return tickets;
    }
}
