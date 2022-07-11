package ru.job4j.cinema.services;

import org.springframework.stereotype.Service;
import ru.job4j.cinema.models.Session;
import ru.job4j.cinema.persistences.SessionsDBStore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SessionService {

    private final SessionsDBStore store;

    public SessionService(SessionsDBStore store) {
        this.store = store;
    }

    public List<Session> findAllSessions() {
        return store.findAllSessions();
    }

    public Optional<Session> findSessionById(int id) {
        return store.findSessionById(id);
    }

    private List<Integer> findFreeCellsByRowId(
                    Map<Integer, List<Integer>> mapOccupiedSeats, int rowId) {
        List<Integer> freeCells = PlaceService.initMapFreeSeats().get(rowId);
        List<Integer> occupiedCells = mapOccupiedSeats.get(rowId);
        if (occupiedCells != null) {
            freeCells.removeAll(mapOccupiedSeats.get(rowId));
        }
        return freeCells;
    }

    public Map<Integer, List<Integer>> mapFreeSeats(
                             Map<Integer, List<Integer>> mapOccupiedSeats) {
        Map<Integer, List<Integer>> rsl = new HashMap<>();
        for (Integer rowId
                : PlaceService.rows()) {
            rsl.put(rowId, findFreeCellsByRowId(mapOccupiedSeats, rowId));
        }
        return rsl;
    }
}
