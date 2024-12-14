package com.example.advent;

import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.commons.io.IOUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.SneakyThrows;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
// @Component
public class Advent2024_13_2 { //implements CommandLineRunner {

    // @Override
    // public void run(String... args) throws Exception {
    //     this.execute();
    // }

    @SneakyThrows
    public List<Solution> execute() {
        log.info("2024 13 2");

        val is = new FileInputStream("/Users/gregorythompson/Projects/advent/2024_13_sample_1.txt");

        val lines = IOUtils.readLines(is, Charset.defaultCharset());
        val machines = IntStream.range(0, (lines.size() + 1) / 4)
            .mapToObj(i -> {
                val si = i * 4;
                return buildMachine(i, lines.get(si), lines.get(si + 1), lines.get(si + 2));
            })
            // .filter(m -> m.n == 201 || m.n == 237 || m.n == 251 || m.n == 311 || m.n == 312)
            .filter(m -> m.n == 0)
            .toList();

        // machines.stream().filter(m -> m.ax == m.ay || m.bx == m.by || m.y == m.x).forEach(m -> log.info("same m {}", m));

        // log.info("machines {}", machines);

        val solutions = machines.stream().map(m -> {
            val s = m.countTokens();
            // log.info("Machine {} tokens {}", m.n, s);            
            return s;
        }).toList();

        val tokens = solutions.stream().mapToLong(s -> s.t).sum();

        log.info("day 13 part 2 {}", tokens); 
        // log.info("day 12 part 2 {}", cost2); 
        return solutions;
    } 

    record Solution(int n, long t, long a, long b) {}

    record Machine(int n, int ax, int ay, int bx, int by, long x, long y) {
        Solution countTokens() {
            var minTokens = 0L;
            val alim = calcA(0, this);
            val blim = calcB(0, this);

            val bmax = Math.max(x / bx, y / by);
            val amax = Math.max(x / ax, y / ay);

            val abmax = calcA(bmax, this);
            val bamax = calcB(amax, this);

            val amin = calcA(0, this);
            val bmin = calcB(0, this);


            log.info("{} {} {} {} {} {}", bmax, amax, abmax, bamax, amin, bmin);

            // if (bmax > 0) {
            //     return new Solution(n, minTokens, 0, 0);
            // }

            // val bmin = calcB(0, this);
            // val bmax = calcB(0, this);
            // val blim = (long) Math.max(x / bx, y / by);
            var opta = 0L;
            var optb = 0L;

            // if (alim < blim) {
                long a = 0;

                for (long b = bmax; b >= bamax; b--) {
                // for (long b = 0; b < 100; b++) {
                    a = calcA(b, this);

                    // if (a % 10000 == 0) {
                        log.info("{} {} {} {} {}", alim, blim, a, b, (a * 3) + b );
                    // }

                    if ((a * ax) + (b * bx) == x && (a * ay) + (b * by) == y) {
                        long tokens = (a * 3) + b;
                        if (minTokens == 0 || minTokens > tokens) {
                            minTokens = tokens;
                            opta = a;
                            optb = b;
                        }
                    }
                }
            // } else if (alim > 0 && blim < 0) {
            //     long b = 0;

            //     for (long a = alim; a <= alim + 100; a++) {
            //         // for (long b = 0; b < 100; b++) {
            //         b = calcB(a, this);
    
            //         // if (a % 10000 == 0) {
            //             log.info("{} {} {} {} {}", alim, blim, a, b, (a * 3) + b );
            //         // }
    
            //         if ((a * ax) + (b * bx) == x && (a * ay) + (b * by) == y) {
            //             long tokens = (a * 3) + b;
            //             if (minTokens == 0 || minTokens > tokens) {
            //                 minTokens = tokens;
            //                 opta = a;
            //                 optb = b;
            //             }
            //         }
            //     }
            // } else {
            //     long b = 0;

            //     for (long a = alim; a >= 0; a--) {
            //         // for (long b = 0; b < 100; b++) {
            //         b = calcB(a, this);
    
            //         // if (a % 10000 == 0) {
            //             log.info("{} {} {} {} {}", alim, blim, a, b, (a * 3) + b );
            //         // }
    
            //         if ((a * ax) + (b * bx) == x && (a * ay) + (b * by) == y) {
            //             long tokens = (a * 3) + b;
            //             if (minTokens == 0 || minTokens > tokens) {
            //                 minTokens = tokens;
            //                 opta = a;
            //                 optb = b;
            //             }
            //         }
            //     }
            // }
    
            return new Solution(n, minTokens, opta, optb);
        }
    }

    private static boolean isInt(double y) {        
        return Math.abs(y - Math.round(y)) < 0.000000005;
    }

    private static long calcB(long a, Machine m) {
        if (m.ax - m.ay == 0) {
            return 0;
        }

        val d = (a + ((m.y - m.x) / (m.ax - m.ay))) * ((m.ax - m.ay) / (m.by - m.bx));
        return isInt(d) ? Math.round(d) : 0L;
    }

    private static long calcA(long b, Machine m) {
        if (m.ax - m.ay == 0) {
            return 0;
        }

        val d = ((b * (m.by - m.bx) / ((double) (m.ax - m.ay))) - ((m.y - m.x) / ((double) (m.ax - m.ay))));
        // log.info("{}", d);

        return isInt(d) ? Math.round(d) : 0L;
    }

    private Machine buildMachine(int n, String aLine, String bLine, String location) {
        val ax = Integer.parseInt(aLine.substring(aLine.indexOf("+"), aLine.indexOf(",")));
        val ay = Integer.parseInt(aLine.substring(aLine.indexOf("+", aLine.indexOf("+") + 1)));

        val bx = Integer.parseInt(bLine.substring(bLine.indexOf("+"), bLine.indexOf(",")));
        val by = Integer.parseInt(bLine.substring(bLine.indexOf("+", bLine.indexOf("+") + 1)));

        val x = Integer.parseInt(location.substring(location.indexOf("X=") + 2, location.indexOf(",")));
        val y = Integer.parseInt(location.substring(location.indexOf("Y=") + 2));

        // return new Machine(n, ax, ay, bx, by, x + 10000000000000L, y + 10000000000000L);
        return new Machine(n, ax, ay, bx, by, x, y);
    }
}
