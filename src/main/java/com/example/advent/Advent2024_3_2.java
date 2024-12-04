package com.example.advent;

import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
// @Component
public class Advent2024_3_2 implements CommandLineRunner{

    @Override
    public void run(String... args) throws Exception {
        log.info("2024 3 2");

        val is = new FileInputStream("/Users/gregorythompson/Projects/advent/2024_3_2.txt");

        val lines = IOUtils.readLines(is, Charset.defaultCharset());
        // log.info("lines {}", lines.size());

        val singleLine = String.join("", lines);

        val conditionalLines = mapToConditionalMuls(singleLine);

        // conditionalLines.stream().forEach(cl -> log.info("cl {}", cl));

        val starts = conditionalLines.stream().filter(cm -> cm.isStart).toList();
        
        // log.info("matches {}", starts); 

        val total = starts.stream().mapToLong(cm -> cm.muls.stream().mapToLong(m -> performMul(m)).sum()).sum();

        log.info("day 3 part 2 {}", total); 
    } 

    private List<ConditionalMul> mapToConditionalMuls(String s) {
        val ns = s.replace("do()", "do()start").replace("don't()", "don't()stop");
        // log.info("ns {}", ns);
        val split = ns.split("do\\(\\)|don't\\(\\)");

        // Arrays.stream(split).forEach(a -> log.info("a {}", a));
        // log.info("split {}", Arrays.asList(split));

        return Arrays.stream(split).map(s1 -> mapToConditionalMul(s1)).toList();
    }

    private ConditionalMul mapToConditionalMul(String s) {
        return new ConditionalMul(extractInstructions(s), !s.startsWith("stop"));
    }

    private record ConditionalMul(List<String> muls, boolean isStart) {}

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
