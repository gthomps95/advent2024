package com.example.advent;

import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
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
public class Advent2024_11_1 implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        log.info("2024 11 1");

        val is = new FileInputStream("/Users/gregorythompson/Projects/advent/2024_11_1.txt");

        val lines = IOUtils.readLines(is, Charset.defaultCharset());        

        var stones = addStones(lines.get(0));
        log.info("Initial stones {}", stones);

        val blinkCount = 40;
        for (int i = 0; i < blinkCount; i++) {
            log.info("split number {}", i);
            stones = splitStones(i, stones);
        };

        val blink2Count = 30;
        long count = 0;
        for (int i = 0; i < stones.size(); i++) {
            var sub = List.of(stones.get(i));
            for (int b = 0; b < blink2Count; b++) {
                log.info("total {} split number {} blink number {}", stones.size(), i, b);
                sub = splitStones(i, sub);
            }
            count += sub.size();
        }

        log.info("day 11 part 1 {}", count); 
        // log.info("day 11 part 2 {}", ratings); 
    } 

    private List<Stone> addStones(String line) {
        return Arrays.stream(line.split(" ")).map(s -> new Stone(Long.parseLong(s))).toList();
    }

    private List<Stone> splitStones(int i, List<Stone> stones) {
        val newList = new ArrayList<Stone>();
        stones.stream().forEach(s -> newList.addAll(s.split()));
        return newList;
    }

    private record Stone(long number) {
        private List<Stone> split() {
            if (number == 0) {
                return List.of(new Stone(1));
            }
            else if ((number + "").length() % 2 == 0) {
                val s = (number + "");
                val split = s.length() / 2;
                val l = s.substring(0, split);
                val r = s.substring(split);

                // log.info("{} {} {}", s, l, r);

                return List.of(new Stone(Long.parseLong(l)), new Stone(Long.parseLong(r)));
            }
            else {
                return List.of(new Stone(number * 2024));
            }
        }
    }
}
