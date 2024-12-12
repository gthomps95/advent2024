package com.example.advent;

import java.io.FileInputStream;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.commons.io.IOUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
// @Component
public class Advent2024_11_2 implements CommandLineRunner {
    private BigInteger[][] precalc;

    @Override
    public void run(String... args) throws Exception {
        log.info("2024 11 2");

        val is = new FileInputStream("/Users/gregorythompson/Projects/advent/2024_11_1.txt");

        val lines = IOUtils.readLines(is, Charset.defaultCharset());        

        var stones = addStones(lines.get(0));
        // log.info("Initial stones {}", stones);

        val ilen = 1000;
        val blen = 30;

        val precalc = new BigInteger[ilen][blen];

        IntStream.range(0, ilen).parallel().forEach(i -> {
            if (i % 100 == 0) {
                log.info("int {}", i);
            }

            IntStream.range(0, blen).parallel().forEach(b -> {
                precalc[i][b] = countStones(b, 1, i);
            });
        });

        this.precalc = precalc;

        val list = stones.parallelStream().map( s -> {
            log.info("calculating stone {}", s);
            return countStones(75, 1, s);
        }).toList();

        val count = list.stream().reduce(BigInteger.ZERO, (t, bi) -> t.add(bi));

        log.info("day 11 part 1 {}", count); 
        // log.info("day 11 part 2 {}", ratings); 
    } 

    private BigInteger countStones(int limit, int blink, long stone) {
        val precalcIndex = limit - blink + 1;
        if (precalc != null && stone <= precalc.length && precalcIndex < precalc[0].length) {
            return precalc[(int) stone][precalcIndex];
        };

        if (blink > limit) {
            return BigInteger.ONE;
        }

        val stones = split(stone);
        var count = BigInteger.ZERO;

        for (int i = 0; i < stones.length; i++) {
            count = count.add(countStones(limit, blink + 1, stones[i]));
        }

        return count;
    }

    private List<Long> addStones(String line) {
        return Arrays.stream(line.split(" ")).map(s -> Long.parseLong(s)).toList();
    }

    private long[] split(long stone) {
        if (stone == 0) {
            return new long[] {1};
        }
        else if ((stone + "").length() % 2 == 0) {
            val s = (stone + "");
            val split = s.length() / 2;
            val l = s.substring(0, split);
            val r = s.substring(split);

            // log.info("{} {} {}", s, l, r);

            return new long[] {Long.parseLong(l), Long.parseLong(r)};
        }
        else {
            return new long[] {stone * 2024};
        }
    }
}
