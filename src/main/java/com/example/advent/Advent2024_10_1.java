package com.example.advent;

import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

import org.apache.commons.io.IOUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Advent2024_10_1 implements CommandLineRunner {

    private static List<Function<Point, Point>> MOVE_NEXT = List.of(
        p -> new Point('0', p.x, p.y - 1),
        p -> new Point('0', p.x + 1, p.y),
        p -> new Point('0', p.x, p.y + 1),
        p -> new Point('0', p.x - 1, p.y)
    );

    @Override
    public void run(String... args) throws Exception {
        log.info("2024 10 1");

        val is = new FileInputStream("/Users/gregorythompson/Projects/advent/2024_10_1.txt");

        val lines = IOUtils.readLines(is, Charset.defaultCharset());

        val map = create2dArray(lines);
        printArray(map);

        val trailheads = findStarts(map);
        log.info("trailheads count {}", trailheads.size());

        val scores = trailheads.stream().mapToInt(s -> {
            val newMap = copyMap(map);
            val result = findTrailheads(newMap, s, true);
            log.info("score is {} for trailhead {}", result, s);
            return result;
        }).sum();
        printArray(map);

        val newMap = copyMap(map);
        val ratings = trailheads.stream().mapToInt(s -> {
            val result = findTrailheads(newMap, s, false);
            log.info("score is {} for trailhead {}", result, s);
            return result;
        }).sum();
        printArray(map);

        log.info("day 10 part 1 {}", scores); 
        log.info("day 10 part 2 {}", ratings); 
    } 

    private record Point(char elevation, int x, int y) {
        boolean isOnMap(char[][] map) {
            return x >= 0 && x < map[0].length && y >= 0 && y < map.length;
        }
    }

    private int findTrailheads(char[][] map, Point current, boolean markEndings) {
        if (current.elevation == '9') {
            log.info("found trailhead at {}", current);
            if (markEndings) map[current.y][current.x] = 'T';
            return 1;
        }

        log.info("Looking for trailheads from {}", current);

        val nextSteps = findNextSteps(map, current);
        if (nextSteps == null || nextSteps.isEmpty()) {
            log.info("No next steps found for {}", current);
            return 0;
        }

        log.info("found {} next steps {}", nextSteps.size(), nextSteps);

        return nextSteps.stream().mapToInt(s -> findTrailheads(map, s, markEndings)).sum();
    }

    private List<Point> findNextSteps(char[][] map, Point s) {
        return MOVE_NEXT.stream()
            .filter(mn -> isNextStep(map, s.elevation, mn.apply(s)))
            .map(mn -> {
                val ns = mn.apply(s);
                val e = map[ns.y][ns.x];
                val np = new Point(e, ns.x, ns.y);
                return np;
            }).toList();
    }

    private boolean isNextStep(char[][] map, char elevation, Point s) {
        return s.isOnMap(map) && map[s.y][s.x] == elevation + 1;
    }

    private List<Point> findStarts(char[][] map) {
        val list = new ArrayList<Point>();
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                if (map[y][x] == '0') {
                    list.add(new Point('0', x, y));
                }
            }
        }

        return list;
    }

    private char[][] copyMap(char[][] map) {
        val newMap = new char[map.length][];
        IntStream.range(0, newMap.length).forEach(i -> newMap[i] = Arrays.copyOf(map[i], map[i].length));
        return newMap;
    }

    private static void printArray(char[][] array) {
        for (int y = 0; y < array.length; y++) {
            for (int x = 0; x < array[y].length; x++) {
                System.out.print(array[y][x]);
            }
            System.out.print("\n");
        }
    }

    private char[][] create2dArray(List<String> lines) {
        val rowCount = lines.size();
        int colCount = lines.stream().findFirst().orElseThrow(() -> new RuntimeException()).length();

        val result = new char[rowCount][colCount];
        IntStream.range(0, rowCount).forEach(i -> result[i] = lines.get(i).toCharArray());

        return result;
    }
}
