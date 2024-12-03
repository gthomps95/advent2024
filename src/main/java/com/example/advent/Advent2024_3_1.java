package com.example.advent;

import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.io.IOUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Advent2024_3_1 implements CommandLineRunner{

    @Override
    public void run(String... args) throws Exception {
        log.info("2024 3 1");

        val is = new FileInputStream("/Users/gregorythompson/Projects/advent/2024_3.txt");

        val lines = IOUtils.readLines(is, Charset.defaultCharset());

        val matches = lines.stream().flatMap(l -> extractInstructions(l).stream()).toList();

        log.info("matches {}", matches); 

        val total = matches.stream().mapToLong(m -> performMul(m)).sum();

        log.info("day 3 part 1 {}", total); 
    } 

    private List<String> extractInstructions(String l) {
        val p = Pattern.compile("mul\\(\\d{1,3},\\d{1,3}\\)");
        val m = p.matcher(l);
        val matches = new ArrayList<String>();

        while (m.find()) {
            matches.add(m.group());
        }
        
        return matches;
    }

    private long performMul(String m) {
        val ints = Arrays.stream(m.replace("mul(", "").replace(")", "").split(","))
        .mapToInt(s -> Integer.parseInt(s)).toArray();

        return ints[0] * ints[1];
    }


}
