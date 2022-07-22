package ru.job4j.cinema.services;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.job4j.cinema.Main;
import ru.job4j.cinema.models.Session;
import ru.job4j.cinema.models.Ticket;
import ru.job4j.cinema.models.User;
import ru.job4j.cinema.persistences.SessionsDBStore;
import ru.job4j.cinema.persistences.TicketsDBStore;
import ru.job4j.cinema.persistences.UsersDBStore;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SessionServiceTest {

    private SessionService sessionService;
    private TicketService ticketService;
    private UserService userService;

    @Before
    public void whenSetUp() {
        BasicDataSource pool = new Main().loadPool();
        SessionsDBStore sessionsDBStore = new SessionsDBStore(pool);
        TicketsDBStore ticketsDBStore = new TicketsDBStore(pool);
        UsersDBStore usersDBStore = new UsersDBStore(pool);
        sessionService = new SessionService(sessionsDBStore);
        ticketService = new TicketService(ticketsDBStore);
        userService = new UserService(usersDBStore);
    }

    @Test
    public void findAllSessions() {
        List<Session> rsl = sessionService.findAllSessions();
        assertThat(rsl.size(), is(3));
        assertThat(rsl.get(0).getName(), is("INTERSTELLAR"));
        assertThat(rsl.get(2).getName(), is("Лев Яшин. Вратарь моей мечты"));
    }

    @Test
    public void findSessionById() {
        assertTrue(sessionService.findSessionById(2).isPresent());
        assertThat(sessionService.findSessionById(2).get().getName(), is("Три кота. Мультфильм"));
        assertTrue(sessionService.findSessionById(4).isEmpty());

    }

    @Test
    public void whenGetMapFreeSeats() {
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
        Map<Integer, List<Integer>> mapFreeSeatsForSessionId1 =
                sessionService.mapFreeSeats(mapOccupiedForSessionId1);
        Map<Integer, List<Integer>> mapFreeSeatsForSessionId2 =
                sessionService.mapFreeSeats(mapOccupiedForSessionId2);
        assertThat(
                mapFreeSeatsForSessionId1.get(1), is(List.of(1, 3))
        );
        assertThat(
                mapFreeSeatsForSessionId1.get(2), is(List.of(1, 2, 3, 4))
        );
        assertThat(
                mapFreeSeatsForSessionId2.get(2), is(List.of(2, 3, 4))
        );
    }
}