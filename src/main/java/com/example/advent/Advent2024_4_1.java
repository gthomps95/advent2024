package com.example.advent;

import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.io.IOUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
// @Component
public class Advent2024_4_1 implements CommandLineRunner{

    @Override
    public void run(String... args) throws Exception {
        log.info("2024 4 1");

        val is = new FileInputStream("/Users/gregorythompson/Projects/advent/2024_4_1.txt");

        val lines1 = IOUtils.readLines(is, Charset.defaultCharset());
        val lines2 = rowsToColumns(lines1);
        val lines3 = rowsToColumns(lines2);
        val lines4 = rowsToColumns(lines3);
        // val lines5 = rowsToColumns(lines4);

        // log.info(" lines 1 {}", lines1);
        // log.info(" lines 2 {}", lines2);
        // log.info(" lines 3 {}", lines3);
        // log.info(" lines 4 {}", lines4);
        // log.info(" lines 5 {}", lines5);

        val diag1 = createDiagonal(lines1);
        val diag2 = createDiagonal(lines2);
        val diag3 = createDiagonal(lines3);
        val diag4 = createDiagonal(lines4);

        // log.info("diag1 {}", diag1);
        // log.info("diag2 {}", diag2);
        // log.info("diag3 {}", diag3);
        // log.info("diag4 {}", diag4);

        val lines = List.of(lines1, lines2, lines3, lines4, diag1, diag2, diag3, diag4);
        val total = lines.stream().mapToInt(ls -> ls.stream().mapToInt(l -> countXmas(l)).sum()).sum();

        // val total = matches.stream().mapToLong(m -> performMul(m)).sum();

        log.info("day 4 part 1 {}", total); 
    } 

    private int countXmas(String line) {
        return StringUtils.countOccurrencesOf(line, "XMAS");
    }

    private List<String> rowsToColumns(List<String> lines) {
        val rowLength = lines.size();
        int colLength = lines.stream().findFirst().orElseThrow(() -> new RuntimeException()).length();

        return IntStream.range(0, colLength)
                .mapToObj(c -> IntStream.range(0, rowLength)
                .mapToObj(r -> lines.get(rowLength - r - 1).charAt(c) + "")
                .collect(Collectors.joining()))
                .toList();
    }

    private List<String> createDiagonal(List<String> lines) {
        val rowCount = lines.size();
        int colCount = lines.stream().findFirst().orElseThrow(() -> new RuntimeException()).length();
        int diagLength = calcDiagonalLength(rowCount, colCount);

        // log.info("rc {} cc {} dl {}", rowCount, colCount, diagLength);

        return IntStream.range(0, diagLength)
                .mapToObj(d -> {
                    val dcs = calcColStart(d, rowCount, colCount);
                    val drs = calcRowStart(d, rowCount, colCount);
                    val drl = calcDiagRowLength(d, rowCount, colCount);

                    // log.info("d {} dcs {} drs {} drl {}", d, dcs, drs, drl);

                    val diag = IntStream.range(0, drl)
                        .mapToObj(i -> {
                            // log.info("char d {} dcs {} drs {} drl {} i {} drs + i {} dcs - i {}", d, dcs, drs, drl, i, drs + i, dcs - i);
                            val s = lines.get(drs + i).charAt(dcs - i) + "";
                            return s;
                        })
                        .collect(Collectors.joining());
                    
                    // log.info("{}", diag);
                    return diag;
                })
                .toList();
    }

    int calcDiagonalLength(int rowLength, int colLength) {
        return rowLength + colLength - 1;
    }

    int calcDiagRowLength(int d, int rowCount, int colCount) {
        val max = Math.min(rowCount, colCount);

        if (d < max) return d + 1;

        return Math.min(calcDiagonalLength(rowCount, colCount) - d, max);
    }

    int calcColStart(int d, int rowCount, int colCount) {
        return Math.min(d, colCount - 1);
    }

    int calcRowStart(int d, int rowCount, int colCount) {
        if (d < colCount) {
            return 0;
        } else {
            return d - colCount + 1;
        }


        // return Math.max(d - colCount, 0);
    }
}
