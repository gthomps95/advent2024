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
// @Component
public class Advent2024_7_1 implements CommandLineRunner{

    @Override
    public void run(String... args) throws Exception {
        log.info("2024 7 1");

        val is = new FileInputStream("/Users/gregorythompson/Projects/advent/2024_7_1.txt");

        val lines = IOUtils.readLines(is, Charset.defaultCharset());

        val equations = createEquations(lines);
        // log.info("Equations: {}", equations);

        Map<Boolean, List<Equation>> p = equations.stream().collect(Collectors.partitioningBy(e -> e.canBeCalculated(operators_1).pass));

        val sum = p.get(Boolean.TRUE).stream().mapToLong(Equation::testValue).sum();
        val sum2 = p.get(Boolean.FALSE).stream().filter(e -> e.canBeCalculated(operators_2).pass).mapToLong(e -> e.testValue).sum();

        // val p2 = p.get(Boolean.FALSE).stream().map(e -> e.canBeCalculated(operators_2)).collect(Collectors.partitioningBy(r -> r.pass));

        // log.info("{} p2 size", p2.get(Boolean.FALSE).size());

        // equations.stream().map(e -> e.canBeCalculated(operators_2)).forEach(r -> log.info("ln {} pass {} trys {}", r.equation.lineNumber, r.pass, r.ops));

        // val r = equations.get(0).canBeCalculated(operators_2);
        // log.info("{} r", r);

        log.info("day 7 part 1 {}", sum); 
        log.info("day 7 part 2 {}", sum + sum2); 
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
    private static final List<BiFunction<Long, Long, Long>> operators_2 = List.of((a, b) -> a + b, (a, b) -> a * b, (a, b) -> Long.parseLong(a + "" + b));

    private record Result(Equation equation, boolean pass, int[] ops) {}

    private record Equation(int lineNumber, List<Long> operands, long testValue) {
        private Result canBeCalculated(List<BiFunction<Long, Long, Long>> operators) {
            val trys = buildTable(operands.size(), operators.size());            
            // log.info("{} try count", trys.length);

            for (int t = 0; t < trys.length; t++) {
                long value = operands.get(0);

                for (int i = 0; i < trys[t].length; i++) {
                    val op = trys[t][i];
                    value = operators.get(op).apply(value, operands.get(i + 1));
                }

                // log.info("{}", operands);
                // log.info("t {} tv {}", t, value);

                if (value == testValue) {
                    return new Result(this, true, trys[t]);
                }
            }

            return new Result(this, false, null);
        }
    }

    private List<Equation> createEquations(List<String> lines) {
        return IntStream.range(0, lines.size()).mapToObj(i -> createEquation(i + 1, lines.get(i))).toList();
    }

    private Equation createEquation(int lineNumber, String line) {
        val testValue = Long.parseLong(line.split(":")[0]);
        val operands = Arrays.stream(line.split(":")[1].split(" ")).filter(s -> !s.isEmpty()).map(s -> Long.parseLong(s)).toList();
        return new Equation(lineNumber, operands, testValue);
    }
}
