package ru.job4j.cinema.services;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class PlaceService {

    private static final int ROWS_COUNT = 3;
    private static final int CELLS_COUNT = 3;

    public static Map<Integer, List<Integer>> initMapFreeSeats() {
        Map<Integer, List<Integer>> rsl = new HashMap<>();
        for (Integer i
                : rows()) {
            rsl.put(i, cells());
        }
        return rsl;
    }

    public static List<Integer> rows() {
        return IntStream.range(1, ROWS_COUNT + 1)
                .boxed()
                .collect(Collectors.toList());
    }

    public static List<Integer> cells() {
        return IntStream.range(1, CELLS_COUNT + 1)
                .boxed()
                .collect(Collectors.toList());
    }
}
