package ru.job4j.cinema.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class PlaceService {

    private static int rowsCount;
    private static int cellsCount;

    @Value("${rows.count}")
    public void setRowsCount(String rows) {
        PlaceService.rowsCount = Integer.parseInt(rows);
    }

    @Value("${cells.count}")
    public void setCellsCount(String cells) {
        PlaceService.cellsCount = Integer.parseInt(cells);
    }

    public static Map<Integer, List<Integer>> initMapFreeSeats() {
        Map<Integer, List<Integer>> rsl = new HashMap<>();
        for (Integer i
                : rows()) {
            rsl.put(i, cells());
        }
        return rsl;
    }

    public static List<Integer> rows() {
        return IntStream.range(1, rowsCount + 1)
                .boxed()
                .collect(Collectors.toList());
    }

    public static List<Integer> cells() {
        return IntStream.range(1, cellsCount + 1)
                .boxed()
                .collect(Collectors.toList());
    }
}
