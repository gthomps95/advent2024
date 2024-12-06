package com.example.advent;

import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import org.apache.commons.io.IOUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Advent2024_6_1 implements CommandLineRunner{

    private char[][] map;

    @Override
    public void run(String... args) throws Exception {
        log.info("2024 6 1");

        val is = new FileInputStream("/Users/gregorythompson/Projects/advent/2024_6_1.txt");

        val lines = IOUtils.readLines(is, Charset.defaultCharset());

        map = create2dArray(lines);
        printMap();

        log.info("{}", map[6][4]);
        log.info("{}", map[4][6]);

        var guardLocation = findGuard();
        log.info("start guard location: {}", guardLocation);
        
        while (guardLocation.isOnMap(map)) {
            guardLocation = takeNextMove(guardLocation);
        }

        log.info("end guard location: {}", guardLocation);

        val total = findPathCount();
        printMap();

        log.info("day 6 part 1 {}", total); 
    } 

    private void printMap() {
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                System.out.print(map[y][x]);
            }
            System.out.print("\n");
        }
    }

    private int findPathCount() {
        int count = 0;
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                if (map[y][x] != '.' && map[y][x] != '#') {
                    count++;
                }
            }
        }

        return count;
    }

    private record GuardLocation(Point p, int d) {
        boolean isOnMap(char[][] map) {
            return p.isOnMap(map);
        }

        Point nextSpot() {
            return p.nextSpot(d);
        }
    }

    private GuardLocation findGuard() {
        int x = 0, y = 0;

        for (y = 0; y < map.length; y++) {
            for (x = 0; x < map[y].length; x++) {
                if (map[y][x] != '.' && map[y][x] != '#') {
                    break;
                }
            }

            if (x < map[y].length && map[y][x] != '.' && map[y][x] != '#') {
                break;
            }
        }

        int d = findDirection(map[y][x]);
        return new GuardLocation(new Point(x, y), d);
    }

    private int findDirection(char c) {
        return switch(c) {
            case '^' -> 0;
            case '>' -> 1;
            case 'v' -> 2;
            case '<' -> 3;
            default -> throw new RuntimeException("direction unknown");
        };
    }

    private record Point(int x, int y) {
        Point nextSpot(int d) {            
            return switch(d) {
                case 0 -> new Point(x, y - 1);
                case 1 -> new Point(x + 1, y);
                case 2 -> new Point(x, y + 1);
                case 3 -> new Point(x - 1, y);
                default -> throw new RuntimeException("direction unknown");
            };
        }

        boolean isOnMap(char[][] map) {
            return x >= 0 && x < map[0].length && y >= 0 && y < map.length;
        }

        char marker(char[][] map) {
            return map[y][x];
        }

        void markSpot(char[][] map) {
            map[y][x] = 'X';
        }
    };

    private GuardLocation takeNextMove(GuardLocation currentLocation) {
        val nextSpot = currentLocation.nextSpot();
        
        if (!nextSpot.isOnMap(map)) {
            log.info("guard is off map at {}, {}", nextSpot.x, nextSpot.y);
            return new GuardLocation(nextSpot, currentLocation.d);
        }

        else if (nextSpot.marker(map) != '#') {
            log.info("move next to {}, {}", nextSpot.x, nextSpot.y);
            nextSpot.markSpot(map);
            return new GuardLocation(nextSpot, currentLocation.d);
        }

        else {
            log.info("blocked at {}, {} turn right", nextSpot.x, nextSpot.y);
            return new GuardLocation(currentLocation.p, (currentLocation.d + 1) % 4);
        }
    }

    private char[][] create2dArray(List<String> lines) {
        val rowLength = lines.size();
        int colLength = lines.stream().findFirst().orElseThrow(() -> new RuntimeException()).length();

        val result = new char[colLength][rowLength];
        IntStream.range(0, rowLength).forEach(i -> result[i] = lines.get(i).toCharArray());

        return result;
    }
}
