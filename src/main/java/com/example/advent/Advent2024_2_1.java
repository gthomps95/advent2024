package com.example.advent;

import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.stream.IntStream;

import org.apache.commons.io.IOUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Advent2024_2_1 implements CommandLineRunner{

    @Override
    public void run(String... args) throws Exception {
        log.info("2024 2 1");

        val is = new FileInputStream("/Users/gregorythompson/Projects/advent/2024_2.txt");

        val lines = IOUtils.readLines(is, Charset.defaultCharset());

        val lineResults = lines.stream().map(l -> mapLine(l)).toList();

        val count = lineResults.stream().filter(lr -> lr.safe).count();

        log.info("day 2 part 1 {}", count); 
    } 

    private LineResult mapLine(String l) {
        val ints = Arrays.stream(l.split(" ")).mapToInt(s -> Integer.parseInt(s)).toArray();
        val diffs = calcDiffs(ints);
        val isSafe = calcSafe(diffs);
        return new LineResult(ints, diffs, isSafe);
    }

    private boolean calcSafe(int[] diffs) {
        return Arrays.stream(diffs).allMatch(i -> i <= 3 && i >= 1) || Arrays.stream(diffs).allMatch(i -> i <= -1 && i >= -3);
    }

    private int[] calcDiffs(int[] ints) {
        return IntStream.range(0, ints.length - 1).map(i -> ints[i] - ints[i + 1]).toArray();
    }

    private record LineResult(int[] ints, int[] diffs, boolean safe) {};

}
