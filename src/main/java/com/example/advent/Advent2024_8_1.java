package com.example.advent;

import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.io.IOUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
// @Component
public class Advent2024_8_1 implements CommandLineRunner{

    @Override
    public void run(String... args) throws Exception {
        log.info("2024 8 1");

        val is = new FileInputStream("/Users/gregorythompson/Projects/advent/2024_8_1.txt");

        val lines = IOUtils.readLines(is, Charset.defaultCharset());

        val map = create2dArray(lines);

        val antennas = findFrequencies(map);
        log.info("{}", antennas);

        placeAntiNodes(map, antennas);
        printArray(map);

        val count = countAntiNodes(map);

        log.info("day 8 part 1 {}", count); 
        // log.info("day 7 part 2 {}", sum + sum2); 
    } 

    private record Point(char frequency, int x, int y) {
        boolean isOnMap(char[][] map) {
            return x >= 0 && x < map[0].length && y >= 0 && y < map.length;
        }

        void placeAntiNode(char[][] map) {
            map[y][x] = '#';
        }

    }

    private int countAntiNodes(char[][] map) {
        int count = 0;

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j] == '#') count++;
            }
        }

        return count;
    }

    private void placeAntiNodes(char[][] map, Map<Character, List<Point>> antennas) {
        val frequencies = antennas.keySet();
        frequencies.stream().forEach(f -> placeAntiNodes(map, antennas.get(f)));
    }

    private void placeAntiNodes(char[][] map, List<Point> antennas) {
        IntStream.range(0, antennas.size() - 1).forEach(i -> IntStream.range(i + 1, antennas.size()).forEach(j -> {
            placeAntiNode(map, antennas.get(i), antennas.get(j));
            placeAntiNode(map, antennas.get(j), antennas.get(i));
        }));
    }

    private void placeAntiNode(char[][] map, Point antenna1, Point antenna2) {
        val xdiff = antenna2.x - antenna1.x;
        val ydiff = antenna2.y - antenna1.y;

        // log.info("Placing anti node for frequency {} for antenna 1 {} and antenna 2 {} xdiff {} ydiff {}", antenna1.frequency, antenna1, antenna2, xdiff, ydiff);        

        val p = new Point(antenna1.frequency, antenna2.x + xdiff, antenna2.y + ydiff);
        // log.info("Placing anti node at {}", p);
        if (p.isOnMap(map)) {
            p.placeAntiNode(map);
        }
    }

    private static void printArray(char[][] array) {
        for (int y = 0; y < array.length; y++) {
            for (int x = 0; x < array[y].length; x++) {
                System.out.print(array[y][x]);
            }
            System.out.print("\n");
        }
    }

    private Map<Character, List<Point>> findFrequencies(char[][] map) {
        val freqs = new ArrayList<Point>();

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j] != '.') {
                    val freq = map[i][j];
                    freqs.add(new Point(freq, j, i));
                }
            }
        }

        return freqs.stream().collect(Collectors.groupingBy(p -> p.frequency));
    }

    private char[][] create2dArray(List<String> lines) {
        val rowCount = lines.size();
        int colCount = lines.stream().findFirst().orElseThrow(() -> new RuntimeException()).length();

        val result = new char[rowCount][colCount];
        IntStream.range(0, rowCount).forEach(i -> result[i] = lines.get(i).toCharArray());

        return result;
    }
}
