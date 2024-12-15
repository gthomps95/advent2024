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

import lombok.Builder;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Advent2024_15_1 implements CommandLineRunner {

    private static List<Function<Point, Point>> MOVE_NEXT = List.of(
        p -> new Point(p.x, p.y - 1),
        p -> new Point(p.x + 1, p.y),
        p -> new Point(p.x, p.y + 1),
        p -> new Point(p.x - 1, p.y)
    );

    @Override
    public void run(String... args) throws Exception {
        this.execute();
    }

    @SneakyThrows
    public void execute() {
        log.info("2024 15 1");

        val is = new FileInputStream("/Users/gregorythompson/Projects/advent/2024_15_1.txt");

        val lines = IOUtils.readLines(is, Charset.defaultCharset());

        val map = create2dArray(lines);
        val dirs = readDirs(lines);
        printArray(map);
        log.info("{}", dirs);

        val l = findCurrentLocation(map);
        log.info("current location {}", l);
        val robot = Robot.builder().currentLocation(l).build();

        for (int m = 0; m < dirs.length; m++) {
            robot.move(dirs[m], map);
            // printArray(map);
        }

        var sum = 0L;

        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                val p = new Point(x, y);
                if (p.isBox(map)) {
                    sum += p.gps();
                }
            }

        }

        printArray(map);

        log.info("day 15 part 1 {}", sum); 
        // log.info("day 12 part 2 {}", cost2); 
    } 

    @Data
    @Builder
    private static class Robot {
        private Point currentLocation;

        private void move(int dir, char[][] map) {
            val newPoint = MOVE_NEXT.get(dir).apply(this.currentLocation);

            if (newPoint.isWall(map)) {
                log.info("{} is wall {}", dir, newPoint);
                return;
            } else if (newPoint.isOpen(map)) {
                log.info("{} is open {}", dir, newPoint);
                currentLocation.makeOpen(map);
                currentLocation = newPoint;
                currentLocation.makeRobot(map);
            } else {
                log.info("{} is box {}", dir, newPoint);
                currentLocation = pushBoxes(currentLocation, dir, map);
            }
        }
    }

    private static Point pushBoxes(Point currentLocation, int dir, char[][] map) {
        val mn = MOVE_NEXT.get(dir);
        var newPoint = mn.apply(currentLocation);
        val boxes = new ArrayList<Point>();

        while (newPoint.isBox(map)) {
            boxes.add(newPoint);
            newPoint = mn.apply(newPoint);
        }

        if (newPoint.isWall(map)) {
            return currentLocation;
        } else {
            for (int b = boxes.size() - 1; b >= 0; b--) {
                val box = boxes.get(b);
                val open = mn.apply(box);
                open.makeBox(map);
                box.makeOpen(map);
            }

            val result = mn.apply(currentLocation);
            currentLocation.makeOpen(map);
            result.makeRobot(map);
            return result;
        }
    }

    private Point findCurrentLocation(char[][] map) {
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                if (map[y][x] == '@') {
                    return new Point(x, y);
                }
            }
        }

        return null;
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

        char getChar(char[][] map) {
            return map[y][x];
        }

        boolean matches(char[][] map, char plant) {
            return (isOnMap(map) && map[y][x] == plant);
        }

        boolean isBox(char[][] map) {
            return map[y][x] == 'O';
        }

        boolean isOpen(char[][] map) {
            return map[y][x] == '.';
        }

        boolean isWall(char[][] map) {
            return map[y][x] == '#';
        }

        void makeOpen(char[][] map) {
            map[y][x] = '.';
        }

        void makeBox(char[][] map) {
            map[y][x] = 'O';
        }

        void makeRobot(char[][] map) {
            map[y][x] = '@';
        }

        int gps() {
            return (y * 100) + x;
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

    private int[] readDirs(List<String> lines) {
        return lines.stream()
                    .filter(l -> !l.isEmpty() && !l.startsWith("#"))
                    .flatMap(l -> l.chars().mapToObj(c -> ((char) c) + ""))
                    .mapToInt(s -> convertDir(s))
                    .toArray();

    }

    private int convertDir(String s) {
        return switch(s) {
            case "^" -> 0;
            case ">" -> 1;
            case "v" -> 2;
            default -> 3;
        };
    }

    private char[][] create2dArray(List<String> lines) {
        val rowCount = lines.size();
        int colCount = lines.stream().findFirst().orElseThrow(() -> new RuntimeException()).length();

        val result = new char[rowCount][colCount];
        IntStream.range(0, rowCount)
            .filter(i -> lines.get(i).startsWith("#"))
            .forEach(i -> result[i] = lines.get(i).toCharArray());

        return result;
    }
}
