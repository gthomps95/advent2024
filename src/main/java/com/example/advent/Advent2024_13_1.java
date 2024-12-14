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
@Component
public class Advent2024_13_1 {//implements CommandLineRunner {

    // @Override
    // public void run(String... args) throws Exception {
    //     this.execute();
    // }

    @SneakyThrows
    public List<Solution> execute() {
        log.info("2024 13 1");

        val is = new FileInputStream("/Users/gregorythompson/Projects/advent/2024_13_1.txt");

        val lines = IOUtils.readLines(is, Charset.defaultCharset());
        val machines = IntStream.range(0, (lines.size() + 1) / 4).mapToObj(i -> {
            val si = i * 4;
            return buildMachine(i, lines.get(si), lines.get(si + 1), lines.get(si + 2));
        })
        // .filter(m -> m.n == 0)
        .toList();

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
            var opta = 0L;
            var optb = 0L;

            for (long a = 0; a < 100; a++) {
                for (long b = 0; b < 100; b++) {
                    // if (b % 10000 == 0) {
                    //     log.info("{} {} {} {}", alim, blim, a, b);
                    // }

                    if ((a * ax) + (b * bx) == x && (a * ay) + (b * by) == y) {
                        log.info("{} {} {}", n, a, b);
                        long tokens = (a * 3) + b;
                        if (minTokens == 0 || minTokens > tokens) {
                            minTokens = tokens;
                        }
                    }
                }
            }

            return new Solution(n, minTokens, opta, optb);
        }
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
