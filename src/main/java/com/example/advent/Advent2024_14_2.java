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

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Advent2024_14_2 implements CommandLineRunner {

    private static int xlen = 101;
    private static int ylen = 103;    

    private static List<Quadrant> QUADRANTS = List.of(
        new Quadrant(1, new Point(0,0), new Point(((xlen - 1) / 2) - 1 , ((ylen - 1) / 2) - 1)),
        new Quadrant(2, new Point(((xlen - 1) / 2) + 1, 0), new Point(xlen - 1, ((ylen - 1) / 2) - 1)),
        new Quadrant(3, new Point(0,((ylen - 1) / 2) + 1), new Point(((xlen - 1) / 2) - 1, ylen - 1)),
        new Quadrant(4, new Point(((xlen - 1) / 2) + 1, ((ylen - 1) / 2) + 1), new Point(xlen - 1, ylen - 1))
    );

    @Override
    public void run(String... args) throws Exception {
        this.execute();
    }

    @SneakyThrows
    public void execute() {
        log.info("2024 14 2");

        val is = new FileInputStream("/Users/gregorythompson/Projects/advent/2024_14_1.txt");

        val lines = IOUtils.readLines(is, Charset.defaultCharset());
        val robots = createRobots(lines);
        // log.info("robots {}", robots);
        // log.info("quadrants {}", QUADRANTS);

        // robots.forEach(r -> log.info("robot {} {}", r, QUADRANTS.stream().filter(q -> r.isInQuadrant(q)).findFirst().map(q -> q.n).orElse(null)));

        // val map = new int[103][101];
        // printArray(map);

        int m = 0;
        boolean found = false;
        while (!found) {
            robots.stream().forEach(r -> r.move());

            val q1 = robots.stream().filter(r -> r.isInQuadrant(QUADRANTS.get(0))).count();
            val q2 = robots.stream().filter(r -> r.isInQuadrant(QUADRANTS.get(1))).count();
            val q3 = robots.stream().filter(r -> r.isInQuadrant(QUADRANTS.get(2))).count();
            val q4 = robots.stream().filter(r -> r.isInQuadrant(QUADRANTS.get(3))).count();

            found = Math.abs(q1 - q2) < 10 && Math.abs(q3 - q4) < 10 && q3 / (double) q1 > 2.3;
            // found = q1 == q2 && q3 == q4 && q3 / (double) q1 > 2;
            log.info("m {}", m++);

            if (found) {
                val map = new int[103][101];
                robots.stream().forEach(r -> map[r.p.y][r.p.x]++);
                printArray(map);
                System.out.println(m);    
            }
        }
        
        // robots.forEach(r -> log.info("robot {} {}", r, QUADRANTS.stream().filter(q -> r.isInQuadrant(q)).findFirst().map(q -> q.n).orElse(null)));

        val safety = QUADRANTS.stream().mapToLong(q -> robots.stream().filter(r -> r.isInQuadrant(q)).count()).reduce(1, (i, c) -> i * c);

        log.info("day 14 part 1 {}", safety); 
        // log.info("day 12 part 2 {}", cost2); 
    } 

    private record Quadrant(int n, Point a, Point b) {}

    @Data
    @SuperBuilder
    private static class Robot {
        private int n;
        private Point p;
        private Point v;

        void move() {
            int x = move1(p.x, v.x, xlen);
            int y = move1(p.y, v.y, ylen);

            p = new Point(x, y);
        }

        boolean isInQuadrant(Quadrant q) {
            return p.x >= q.a.x && p.x <= q.b.x && p.y >= q.a.y && p.y <= q.b.y;
        }
    }

    private static int move1(int p, int v, int len) {
        val np = p + v;
        if (np < 0) return len + np;
        if (np >= len) return np - len;
        return np;
    }

    private List<Robot> createRobots(List<String> lines) {
        return IntStream.range(0, lines.size()).mapToObj(i -> createRobot(i, lines.get(i))).toList();
    }

    private Robot createRobot(int n, String line) {
        val s = line.replace("p=", "").replace("v=", "").replace(" ", ",").split(",");

        val px = Integer.parseInt(s[0]);
        val py = Integer.parseInt(s[1]);
        val vx = Integer.parseInt(s[2]);
        val vy = Integer.parseInt(s[3]);
        return Robot.builder().n(n).p(new Point(px, py)).v(new Point(vx, vy)).build();
    }

    private record Point(int x, int y) {
        boolean isOnMap(int[][] map) {
            return x >= 0 && x < map[0].length && y >= 0 && y < map.length;
        }
    }

    private int[][] copyMap(int[][] map) {
        val newMap = new int[map.length][];
        IntStream.range(0, newMap.length).forEach(i -> newMap[i] = Arrays.copyOf(map[i], map[i].length));
        return newMap;
    }

    private static void printArray(int[][] array) {
        for (int y = 0; y < array.length; y++) {
            for (int x = 0; x < array[y].length; x++) {
                if (array[y][x] == 0) {
                    System.out.print(".");
                } else {
                    System.out.print(array[y][x]);
                }
            }
            System.out.print("\n");
        }
    }
}
