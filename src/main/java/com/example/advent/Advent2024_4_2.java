package com.example.advent;

import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.io.IOUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Advent2024_4_2 implements CommandLineRunner{

    @Override
    public void run(String... args) throws Exception {
        log.info("2024 4 2");

        val is = new FileInputStream("/Users/gregorythompson/Projects/advent/2024_4_1.txt");

        val lines = IOUtils.readLines(is, Charset.defaultCharset());

        val chars = create2dArray(lines);

        val total = findXmas(chars);
        log.info("day 4 part 2 {}", total); 
    } 

    private int findXmas(char[][] chars) {
        val total = new AtomicInteger();
        IntStream.range(1, chars.length - 1)
            .forEach(i -> IntStream.range(1, chars[0].length - 1)
            .forEach(j -> {
                if (checkXmas(chars, i, j)) total.incrementAndGet();
            }));

        return total.get();
    }

    private boolean checkXmas(char[][] chars, int i, int j) {
        return chars[i][j] == 'A' && checkMas1(chars, i, j) && checkMas2(chars, i, j);
    }

    private boolean checkMas1(char[][] chars, int i, int j) {
        return (chars[i-1][j-1] == 'M' && chars[i+1][j+1] == 'S') ||
            (chars[i-1][j-1] == 'S' && chars[i+1][j+1] == 'M');
    }

    private boolean checkMas2(char[][] chars, int i, int j) {
        return (chars[i-1][j+1] == 'M' && chars[i+1][j-1] == 'S') ||
            (chars[i-1][j+1] == 'S' && chars[i+1][j-1] == 'M');
       
    }

    private char[][] create2dArray(List<String> lines) {
        val rowLength = lines.size();
        int colLength = lines.stream().findFirst().orElseThrow(() -> new RuntimeException()).length();

        val result = new char[rowLength][colLength];
        IntStream.range(0, rowLength).forEach(i -> result[i] = lines.get(i).toCharArray());

        return result;
    }
}
