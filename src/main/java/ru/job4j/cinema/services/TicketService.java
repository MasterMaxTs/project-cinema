package ru.job4j.cinema.services;

import org.springframework.stereotype.Service;
import ru.job4j.cinema.models.Ticket;
import ru.job4j.cinema.persistences.TicketsDBStore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TicketService {

    private final TicketsDBStore store;

    public TicketService(TicketsDBStore store) {
        this.store = store;
    }

    public Optional<Ticket> createTicket(Ticket ticket) {
        return store.createTicket(ticket);
    }

    public List<Ticket> findAllTickets(int sessionId) {
        return store.findAllTickets(sessionId);
    }

    private Map<Integer, List<Ticket>> seats(int sessionId) {
        return findAllTickets(sessionId)
                .stream()
                .collect(
                        Collectors.groupingBy(Ticket::getRow)
                );
    }

    public Map<Integer, List<Integer>> getMapOccupiedSeats(int sessionId) {
        Map<Integer, List<Integer>> rsl = new HashMap<>();
        for (Map.Entry<Integer, List<Ticket>> entry
                : seats(sessionId).entrySet()) {
            rsl.put(entry.getKey(),
                    entry.getValue()
                            .stream()
                            .map(Ticket::getCell).
                            collect(Collectors.toList())
            );
        }
        return rsl;
    }
}
