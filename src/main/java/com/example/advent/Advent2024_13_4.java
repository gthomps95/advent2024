package com.example.advent;

import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.io.IOUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.SneakyThrows;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Advent2024_13_4 { //implements CommandLineRunner {

    // @Override
    // public void run(String... args) throws Exception {
    //     this.execute();
    // }

    @SneakyThrows
    public List<Solution> execute() {
        log.info("2024 13 2");

        val is = new FileInputStream("/Users/gregorythompson/Projects/advent/2024_13_1.txt");

        val lines = IOUtils.readLines(is, Charset.defaultCharset());
        val machines = IntStream.range(0, (lines.size() + 1) / 4)
            .mapToObj(i -> {
                val si = i * 4;
                return buildMachine(i, lines.get(si), lines.get(si + 1), lines.get(si + 2));
            })
            // .filter(m -> m.n == 201 || m.n == 237 || m.n == 251 || m.n == 311 || m.n == 312)
            // .filter(m -> m.n == 3)
            .toList();

        // machines.stream().filter(m -> m.ax == m.ay || m.bx == m.by || m.y == m.x).forEach(m -> log.info("same m {}", m));

        // log.info("machines {}", machines);

        val solutions = machines.stream().map(m -> {
            val s = m.countTokens();
            log.info("Machine {} tokens {}", m.n, s);            
            return s;
        }).toList();

        val tokens = solutions.stream().mapToLong(s -> s.t).sum();

        val correct = solutions.stream().collect(Collectors.partitioningBy(s -> s.m.confirm(s)));
        log.info("incorrect {}", correct.get(Boolean.FALSE));

        log.info("day 13 part 2 {}", tokens); 
        // log.info("day 12 part 2 {}", cost2); 
        return solutions;
    } 

    record Point(double x, double y) {}
    record Line(double s, double i) {}

    record Solution(Machine m, int n, long t, long a, long b) {}

    record Machine(int n, int ax, int ay, int bx, int by, long x, long y) {
        Solution countTokens() {
            val aline = new Line(ay / (double) ax, 0);

            // find yint
            val yint = y - ((by / (double) bx) * x);
            val bline = new Line(by / (double) bx, yint);

            // log.info("aline {}", aline);
            // log.info("bline {}", bline);

            // find intersection
            val p = findIntersection(aline, bline);
            // log.info("intersection {}", p);

            // find distance
            val alength = findDistance(new Point(0, 0), p);
            val blength = findDistance(p, new Point(x, y));

            val ad = findDistance(new Point(0, 0), new Point(ax, ay));
            val bd = findDistance(new Point(0, 0), new Point(bx, by));

            val a = alength / ad;
            val b = blength / bd;

            // log.info("al {} ad {} bl {} bd {} a {} b {} isInt a {} isInt b {}", alength, ad, blength, bd, a, b, isInt(a), isInt(b));

            return (isInt(a) && isInt(b)) ? new Solution(this, n, Math.round((a * 3) + b), Math.round(a), Math.round(b)) : new Solution(this, n, 0, 0, 0);
        }

        boolean confirm(Solution s) {
            return s.t == 0 || (s.a * ax) + (s.b * bx) == x && (s.a * ay) + (s.b * by) == y;
        }
    }

    private static double findDistance(Point p1, Point p2) {
        return Math.pow((Math.pow(p2.x - p1.x, 2) + Math.pow(p2.y - p1.y, 2)), 0.5);
    }

    private static Point findIntersection(Line aline, Line bline) {
        double x = (bline.i - aline.i) / (aline.s - bline.s);
        double y = aline.s * x;
        return new Point(x, y);
    }


    private static boolean isInt(double y) {        
        return Math.abs(y - Math.round(y)) < 0.001;
    }

    private Machine buildMachine(int n, String aLine, String bLine, String location) {
        val ax = Integer.parseInt(aLine.substring(aLine.indexOf("+"), aLine.indexOf(",")));
        val ay = Integer.parseInt(aLine.substring(aLine.indexOf("+", aLine.indexOf("+") + 1)));

        val bx = Integer.parseInt(bLine.substring(bLine.indexOf("+"), bLine.indexOf(",")));
        val by = Integer.parseInt(bLine.substring(bLine.indexOf("+", bLine.indexOf("+") + 1)));

        val x = Integer.parseInt(location.substring(location.indexOf("X=") + 2, location.indexOf(",")));
        val y = Integer.parseInt(location.substring(location.indexOf("Y=") + 2));

        return new Machine(n, ax, ay, bx, by, x + 10000000000000L, y + 10000000000000L);
        // return new Machine(n, ax, ay, bx, by, x, y);
    }
}
