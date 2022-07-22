package ru.job4j.cinema.services;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.job4j.cinema.Main;
import ru.job4j.cinema.models.Ticket;
import ru.job4j.cinema.models.User;
import ru.job4j.cinema.persistences.TicketsDBStore;
import ru.job4j.cinema.persistences.UsersDBStore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TicketServiceTest {

    private BasicDataSource pool;
    private TicketService ticketService;
    private UserService userService;

    @Before
    public void whenSetUp() {
        pool = new Main().loadPool();
        TicketsDBStore ticketsDBStore = new TicketsDBStore(pool);
        ticketService = new TicketService(ticketsDBStore);
        UsersDBStore usersDBStore = new UsersDBStore(pool);
        userService = new UserService(usersDBStore);
    }

    @After
    public void wipeTable() throws SQLException {
        try (Connection cn = pool.getConnection()) {
            PreparedStatement ps = cn.prepareStatement("DELETE FROM tickets");
            ps.execute();
        }
    }

    @Test
    public void whenCreateTicketThanSuccess() {
        Optional<User> firstUser = userService.addUser(
                new User(0, "name1", "email1", "phone1"));
        Optional<User> secondUser = userService.addUser(
                new User(0, "name2", "email2", "phone2"));
        Optional<User> thirdUser = userService.addUser(
                new User(0, "name3", "email3", "phone3"));
        Optional<Ticket> firstTicket =
                ticketService.createTicket(
                        new Ticket(0, 1, 1, 1, firstUser.get().getId()));
        Optional<Ticket> secondTicket =
                ticketService.createTicket(
                        new Ticket(0, 1, 2, 2, secondUser.get().getId()));
        Optional<Ticket> thirdTicket =
                ticketService.createTicket(
                        new Ticket(0, 2, 2, 4, thirdUser.get().getId()));
        List<Ticket> ticketsForSessionId1 = ticketService.findAllTickets(1);
        List<Ticket> ticketsForSessionId2 = ticketService.findAllTickets(2);
        assertThat(ticketsForSessionId1.size(), is(2));
        assertThat(ticketsForSessionId2.size(), is(1));
        assertThat(ticketsForSessionId1.get(0).getCell(), is(1));
        assertThat(ticketsForSessionId1.get(1).getRow(), is(2));
        assertThat(ticketsForSessionId2.get(0).getCell(), is(4));
    }

    @Test
    public void whenCreateTicketThanFail() {
        Optional<User> firstUser = userService.addUser(
                new User(0, "name1", "email1", "phone1"));
        Optional<User> secondUser = userService.addUser(
                new User(0, "name2", "email2", "phone2"));
        Optional<Ticket> firstTicket =
                ticketService.createTicket(
                        new Ticket(0, 1, 4, 4, firstUser.get().getId()));
        Optional<Ticket> secondTicket =
                ticketService.createTicket(
                        new Ticket(0, 1, 4, 4, secondUser.get().getId()));
        assertThat(ticketService.findAllTickets(1).size(), is(1));
        assertThat(ticketService.findAllTickets(1).get(0).getUserId(),
                    is(firstUser.get().getId()));
    }

    @Test
    public void whenGetMapOccupiedSeats() {
        Optional<User> firstUser = userService.addUser(
                new User(0, "name1", "email1", "phone1"));
        Optional<User> secondUser = userService.addUser(
                new User(0, "name2", "email2", "phone2"));
        Optional<User> thirdUser = userService.addUser(
                new User(0, "name3", "email3", "phone3"));
        Optional<Ticket> firstTicket =
                ticketService.createTicket(
                        new Ticket(0, 1, 1, 2, firstUser.get().getId()));
        Optional<Ticket> secondTicket =
                ticketService.createTicket(
                        new Ticket(0, 1, 1, 4, secondUser.get().getId()));
        Optional<Ticket> thirdTicket =
                ticketService.createTicket(
                        new Ticket(0, 2, 2, 1, thirdUser.get().getId()));
        Map<Integer, List<Integer>> mapOccupiedForSessionId1 =
                ticketService.getMapOccupiedSeats(1);
        Map<Integer, List<Integer>> mapOccupiedForSessionId2 =
                ticketService.getMapOccupiedSeats(2);
        assertThat(mapOccupiedForSessionId1, is(Map.of(1, List.of(2, 4))));
        assertThat(mapOccupiedForSessionId2, is(Map.of(2, List.of(1))));
    }
}