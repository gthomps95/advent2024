package com.example.advent;

import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

import org.apache.commons.io.IOUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Advent2024_2_2 implements CommandLineRunner{

    @Override
    public void run(String... args) throws Exception {
        log.info("2024 2 2");

        val is = new FileInputStream("/Users/gregorythompson/Projects/advent/2024_2.txt");

        val lines = IOUtils.readLines(is, Charset.defaultCharset());

        val lineResults = lines.stream().map(l -> mapLine(l)).toList();

        val count = lineResults.stream().filter(lr -> lr.safe).count();

        log.info("day 2 part 2 {}", count); 
    } 

    private LineResult mapLine(String l) {
        int[] ints = Arrays.stream(l.split(" ")).mapToInt(s -> Integer.parseInt(s)).toArray();

        val isSafe = new AtomicBoolean(false);

        IntStream.range(0, ints.length).forEach(i -> {
            val newInts = IntStream.range(0, ints.length).filter(i2 -> i2 != i).map(i2 -> ints[i2]).toArray();
            val diffs = calcDiffs(newInts);
            isSafe.set(isSafe.get() || calcSafe(diffs));
        });

        return new LineResult(ints, new int[0], isSafe.get());
    }

    private boolean calcSafe(int[] diffs) {
        return Arrays.stream(diffs).allMatch(i -> i <= 3 && i >= 1) || Arrays.stream(diffs).allMatch(i -> i <= -1 && i >= -3);
    }

    private int[] calcDiffs(int[] ints) {
        return IntStream.range(0, ints.length - 1).map(i -> ints[i] - ints[i + 1]).toArray();
    }

    private record LineResult(int[] ints, int[] diffs, boolean safe) {};

}
