package com.example.advent;

import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
public class Advent2024_12_1 implements CommandLineRunner {

    private static List<Function<Point, Point>> MOVE_NEXT = List.of(
        p -> new Point(p.x, p.y - 1),
        p -> new Point(p.x + 1, p.y),
        p -> new Point(p.x, p.y + 1),
        p -> new Point(p.x - 1, p.y)
    );

    @Override
    public void run(String... args) throws Exception {
        log.info("2024 12 1");

        val is = new FileInputStream("/Users/gregorythompson/Projects/advent/2024_12_1.txt");

        val lines = IOUtils.readLines(is, Charset.defaultCharset());

        val map = create2dArray(lines);
        printArray(map);

        val regions = findRegions(map);
        log.info("regions {}", regions);

        val perims = findPerimeters(map, regions);

        val cost = perims.stream().mapToInt(p -> p.length * p.area.points.size()).sum();


        log.info("day 12 part 1 {}", cost); 
        // log.info("day 10 part 2 {}", ratings); 
    } 

    private record Perimeter(int length, Region area) {

    }

    private record Region(char plant, List<Point> points) {

    }

    private List<Region> findRegions(char[][] map) {
        val visited = new ArrayList<Point>();
        val regions = new ArrayList<Region>();

        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                val c = map[y][x];
                val p = new Point(x, y);
                if (!visited.contains(p)) {
                    val list = new ArrayList<Point>();
                    list.addAll(findNeighbors(c, map, p, visited));
                    regions.add(new Region(c, list));
                }

            }
        }

        return regions;
    }

    private List<Point> findNeighbors(char c, char[][] map, Point p, List<Point> visited) {
        if (!p.isOnMap(map) || visited.contains(p) || map[p.y][p.x] != c) {
            return Collections.emptyList();
        } else {
            val list = new ArrayList<Point>();
            list.add(p);
            visited.add(p);
            list.addAll(MOVE_NEXT.stream().flatMap(mn -> findNeighbors(c, map, mn.apply(p), visited).stream()).toList());
            return list;
        }
    }

    private List<Perimeter> findPerimeters(char[][] map, List<Region> regions) {
        return regions.stream().map(r -> findPerimeter(map, r)).toList();
    }

    private Perimeter findPerimeter(char[][] map, Region region) {
        val c = region.points.stream().mapToInt(p -> p.perimeterCount(map)).sum();
        return new Perimeter(c, region);
    }
    

    private record Point(int x, int y) {
        boolean isOnMap(char[][] map) {
            return x >= 0 && x < map[0].length && y >= 0 && y < map.length;
        }

        int perimeterCount(char[][] map) {
            return (int) MOVE_NEXT.stream().filter(mn -> {
                val p = mn.apply(this);
                return (!p.isOnMap(map) || map[p.y][p.x] != map[this.y][this.x]);
            }).count();
        }
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
