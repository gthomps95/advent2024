package com.example.advent;

import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Advent2024_6_1 implements CommandLineRunner{

    private static char[] VALID_SQUARES = new char[] {'.', 'W', 'X', 'Y', 'Z'};
    private static char[] BLOCKERS = new char[] {'#'};

    private static char[] DIR_MARKERS = new char[] {'X', 'X', 'X', 'X'};

    private static char[] INIT_MARKERS = new char[] {'^', '>', 'v', '<'};

    private static List<Function<Point, Point>> MOVE_NEXT = List.of(
        p -> new Point(p.x, p.y - 1),
        p -> new Point(p.x + 1, p.y),
        p -> new Point(p.x, p.y + 1),
        p -> new Point(p.x - 1, p.y)
    );

    private List<Point> options = new ArrayList<>();

    @Override
    public void run(String... args) throws Exception {
        log.info("2024 6 1");

        val is = new FileInputStream("/Users/gregorythompson/Projects/advent/2024_6_1.txt");

        val lines = IOUtils.readLines(is, Charset.defaultCharset());

        val map = create2dArray(lines);
        printMap(map);

        log.info("{}", map[6][4]);
        log.info("{}", map[4][6]);

        var guardLocation = findGuard(map);
        log.info("start guard location: {}", guardLocation);
        var found = false;
        
        val tempMap = copyMap(map);
        traverseMap(map, guardLocation, false);

        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                val t = copyMap(tempMap);
                t[y][x] = BLOCKERS[0];
                val escaped = traverseMap(t, guardLocation, false);
                if (!escaped) {
                    options.add(new Point(x, y));
                }
            }
        }

        // log.info("end guard location: {}", guardLocation);

        val total = findPathCount(map);

        // options.stream().forEach(p -> map[p.y][p.x] = 'O');
        printMap(map);

        log.info("day 6 part 1 {}", total); 
        log.info("day 6 part 2 {}", options.size()); 
        // log.info("{}", options);
    } 

    private boolean traverseMap(char[][] map, GuardLocation guardLocation, boolean tryBlockers) {
        int steps = 0;
        while (guardLocation.isOnMap(map) && steps <= map.length * map[0].length * 2) {
            // found = checkForLoops(guardLocation);
            guardLocation = takeNextMove(map, guardLocation);
            steps++;

            if (tryBlockers) {
                val blockerPoint = guardLocation.nextSpot();
                // log.info("trying blocker at point {}", blockerPoint);

                if (!blockerPoint.isOnMap(map) || ArrayUtils.contains(BLOCKERS, map[blockerPoint.y][blockerPoint.x])) {
                    log.info("blocker point is off map or already blocker {}", blockerPoint);                    
                } else {    
                    val tempMap = copyMap(map);
                    blockerPoint.markSpot(tempMap, BLOCKERS[0]);

                    val escaped = traverseMap(tempMap, guardLocation, false);
                    if (!escaped) {
                        blockerPoint.markSpot(tempMap, 'O');
                        options.add(blockerPoint);

                        if (options.size() == 1) {
                            printMap(tempMap);
                            log.info("blocker point {}", blockerPoint);
                        }
                    }
                }
            }
        }

        return !guardLocation.isOnMap(map);
    }
        
    // private boolean checkForLoops(GuardLocation guardLocation) {
    //     val nextSpot = guardLocation.nextSpot( map);
    //     if (!nextSpot.isOnMap(map)) {
    //         return false;
    //     }

    //     var rotated = (guardLocation.d + 1) % 4;
    //     val dirMarker = DIR_MARKERS[rotated];
    //     var point = guardLocation.p;
    //     var found = false;

    //     val tempMap = copyMap(map);

    //     while (!found && point.isOnMap(tempMap)) {
    //         if (point.isSameRoute(tempMap, dirMarker)) {
    //             options.add(nextSpot);
    //             found = true;
    //             break;
    //         }

    //         val nextPoint = point.nextSpot(rotated);

    //         if (nextPoint.isOnMap(tempMap) && nextPoint.isBlokcer(tempMap)) {
    //             // log.info("P {} N {}", point, nextPoint);
    //             rotated = (rotated + 1) % 4;
    //         } else {
    //             if (point.isOnMap(map)) {
    //                 tempMap[point.y][point.x] = DIR_MARKERS[rotated];
    //             }

    //             point = nextPoint;
    //         }
    //     }

    //     if (found) {
    //         tempMap[nextSpot.y][nextSpot.x] = 'O';
    //         printMap(tempMap);
    //     }

    //     return found;
    // }

    private char[][] copyMap(char[][] map) {
        val newMap = new char[map.length][];
        IntStream.range(0, newMap.length).forEach(i -> newMap[i] = Arrays.copyOf(map[i], map[i].length));
        return newMap;
    }

    private void printMap(char[][] map) {
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                System.out.print(map[y][x]);
            }
            System.out.print("\n");
        }
    }

    private int findPathCount(char[][] map) {
        int count = 0;
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                if (ArrayUtils.contains(DIR_MARKERS, map[y][x])) {
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

        boolean isOnSameRoute(char[][] map) {
            return p.isSameRoute(map, DIR_MARKERS[d]);
        }

        Point nextSpot() {
            return p.nextSpot(d);
        }
    }

    private GuardLocation findGuard(char[][] map) {
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
        val point = new Point(x, y);
        point.markSpot(map, d);
        return new GuardLocation(point, d);
    }

    private int findDirection(char c) {
        return ArrayUtils.indexOf(INIT_MARKERS, c);
    }

    private record Point(int x, int y) {
        Point nextSpot(int d) {            
            return MOVE_NEXT.get(d).apply(this);
        }

        boolean isOnMap(char[][] map) {
            return x >= 0 && x < map[0].length && y >= 0 && y < map.length;
        }

        boolean isBlokcer(char[][] map) {
            return ArrayUtils.contains(BLOCKERS, map[y][x]);
        }

        boolean isSameRoute(char[][] map, char dirMarker) {
            return map[y][x] == dirMarker;
        }

        char marker(char[][] map) {
            return map[y][x];
        }

        void markSpot(char[][] map, int d) {
            if (!ArrayUtils.contains(DIR_MARKERS, map[y][x])) {
                map[y][x] = DIR_MARKERS[d];
            }
        }

        void markSpot(char[][] map, char c) {
            map[y][x] = c;
        }
    };

    private GuardLocation takeNextMove(char[][] map, GuardLocation currentLocation) {
        val nextSpot = currentLocation.nextSpot();
        
        if (!nextSpot.isOnMap(map)) {
            // log.info("guard is off map at {}, {}", nextSpot.x, nextSpot.y);
            return new GuardLocation(nextSpot, currentLocation.d);
        }

        else if (nextSpot.marker(map) != '#') {
            // log.info("move next to {}, {}", nextSpot.x, nextSpot.y);
            nextSpot.markSpot(map, currentLocation.d);
            return new GuardLocation(nextSpot, currentLocation.d);
        }

        else {
            // log.info("blocked at {}, {} turn right", nextSpot.x, nextSpot.y);
            // currentLocation.p.markSpot(map, '+');
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
