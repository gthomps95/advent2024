package com.example.advent;

import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.io.IOUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Advent2024_7_1 implements CommandLineRunner{

    @Override
    public void run(String... args) throws Exception {
        log.info("2024 7 1");

        val is = new FileInputStream("/Users/gregorythompson/Projects/advent/2024_7_1.txt");

        val lines = IOUtils.readLines(is, Charset.defaultCharset());

        val equations = createEquations(lines);
        // log.info("Equations: {}", equations);

        Map<Boolean, List<Equation>> p = equations.stream().collect(Collectors.partitioningBy(e -> e.canBeCalculated(operators_1)));

        val sum = p.get(Boolean.TRUE).stream().mapToLong(Equation::testValue).sum();
        val sum2 = p.get(Boolean.FALSE).stream().filter(e -> e.canBeCalculated(operators_2)).mapToLong(e -> e.testValue).sum();

        log.info("day 7 part 1 {}", sum); 
        log.info("day 7 part 2 {}", sum + sum2); 
        // log.info("day 6 part 2 {}", options.size()); 
    } 

    private static void printArray(int[][] array) {
        for (int y = 0; y < array.length; y++) {
            for (int x = 0; x < array[y].length; x++) {
                System.out.print(array[y][x]);
            }
            System.out.print("\n");
        }
    }

    private static int[][] buildTable(int operandCount, int operatorCount)  {
        val trys = new int[(int) Math.pow(operatorCount, operandCount - 1)][operandCount - 1];
        // log.info("{} tc", trys.length);

        for (int t = 0; t < trys.length; t++) {
            for (int i = operandCount-2; i >= 0; i--) {                    
                trys[t][i] = (t /(int) Math.pow(operatorCount, i)) % operatorCount;
            }
        }

        // printArray(trys);
        return trys;
    }

    private static final List<BiFunction<Long, Long, Long>> operators_1 = List.of((a, b) -> a + b, (a, b) -> a * b);
    private static final List<BiFunction<Long, Long, Long>> operators_2 = List.of((a, b) -> a + b, (a, b) -> a * b, (a, b) -> Long.parseLong((a + "") + (b + "")));

    private record Equation(List<Long> operands, long testValue) {
        private boolean canBeCalculated(List<BiFunction<Long, Long, Long>> operators) {
            val trys = buildTable(operands.size(), operators.size());            

            for (int t = 0; t < trys.length; t++) {
                long value = operands.get(0);

                for (int i = 0; i < trys[t].length; i++) {
                    val op = trys[t][i];
                    value = operators.get(op).apply(value, operands.get(i + 1));
                }

                // log.info("{}", operands);
                // log.info("t {} tv {}", t, value);

                if (value == testValue) {
                    return true;
                }
            }

            return false;
        }
    }

    private List<Equation> createEquations(List<String> lines) {
        return lines.stream().map(this::createEquation).toList();
    }

    private Equation createEquation(String line) {
        val testValue = Long.parseLong(line.split(":")[0]);
        val operands = Arrays.stream(line.split(":")[1].split(" ")).filter(s -> !s.isEmpty()).map(s -> Long.parseLong(s)).toList();
        return new Equation(operands, testValue);
    }
}
