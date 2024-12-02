package com.example.advent;

import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.stream.IntStream;

import org.apache.commons.io.IOUtils;
import org.jline.utils.Log;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Advent2024_1_1 implements CommandLineRunner{

    @Override
    public void run(String... args) throws Exception {
        log.info("2024 1 1");

        val is = new FileInputStream("/Users/gregorythompson/Projects/advent/2024_1_1.txt");

		val lines = IOUtils.readLines(is, Charset.defaultCharset());

        val lineInts = lines.stream().map(l -> convertToInts(l)).toList();
        val list1 = lineInts.stream().mapToInt(li -> li.a).sorted().toArray();
        val list2 = lineInts.stream().mapToInt(li -> li.b).sorted().toArray();

        int size = list1.length;
        val sum = IntStream.range(0, size).map(i -> Math.abs(list2[i] - list1[i])).sum();


        log.info("part 1 {}", sum); 

        val counts = IntStream.range(0, size).mapToObj(i -> new Ints(list1[i], (int) findCount(list1[i], list2))).toList();

        val sum2 = counts.stream().mapToLong(i -> i.a * i.b).sum();

        counts.stream().forEach(i -> log.info("{} {}", i.a, i.b));
        
        log.info("part 2 {}", sum2);
        
        
    } 

    private Ints convertToInts(String l) {
        val split = l.split("   ");
        return new Ints(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
    }

    private long findCount(int s, int[] list){
        return IntStream.range(0, list.length).filter(i -> list[i] == s).count();
    }

    private record Ints(int a, int b) {};

}
