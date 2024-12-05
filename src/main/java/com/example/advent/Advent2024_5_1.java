package com.example.advent;

import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.io.IOUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Advent2024_5_1 implements CommandLineRunner{

    @Override
    public void run(String... args) throws Exception {
        log.info("2024 5 1");

        val is = new FileInputStream("/Users/gregorythompson/Projects/advent/2024_5_1.txt");

        val lines = IOUtils.readLines(is, Charset.defaultCharset());

        val ruleLines = lines.stream().filter(l -> l.contains("|")).toList();
        val updLines = lines.stream().filter(l -> !l.contains("|")).filter(l -> !l.isEmpty()).toList();

        val rules = buildRules(ruleLines);
        val updates = buildUpdates(updLines);

        // log.info("rules {}", rules);
        // log.info("updates {}", updLines);

        val rulesMap = rules.stream().collect(Collectors.groupingBy(r -> r.page));
        val partition = updates.stream().collect(Collectors.partitioningBy(u -> u.isGoodUpdate(rulesMap)));
        val correct = partition.get(true);
        // log.info("correct: {}", correct);
        val total = correct.stream().mapToInt(c -> c.middlePage).sum();
        log.info("day 5 part 1 {}", total); 

        val incorrect = partition.get(false);
        log.info("is {}", incorrect.size());

        val madeCorrect = makeCorrect(rulesMap, incorrect);
        val madeTotal = madeCorrect.stream().mapToInt(c -> c.middlePage).sum();
        log.info("day 5 part 2 {}", madeTotal); 
    } 

    private List<Update> makeCorrect(Map<Integer, List<Rule>> rulesMap, List<Update> notCorrect) {
        return notCorrect.stream().map(nc -> makeCorrect(rulesMap, nc)).toList();
    }

    private Update makeCorrect(Map<Integer, List<Rule>> rulesMap, Update notCorrect) {
        val pages = notCorrect.pages;
        val newList = new ArrayList<Integer>();

        Arrays.stream(pages).forEach(p -> insertPage(notCorrect, p, newList, rulesMap));

        val newPages = newList.stream().mapToInt(i -> i).toArray();
        return new Update(notCorrect.lineNumber, newPages, findMiddlePage(newPages));
    }

    private void insertPage(Update notcorrect, int p, List<Integer> newList, Map<Integer, List<Rule>> rulesMap) {
        if (newList.isEmpty()) {
            newList.add(p);
            return;
        }

        int i = newList.size();
        while (i >= 0) {
            val tempList = new ArrayList<>(newList);

            tempList.add(i, p);

            val tempPages = tempList.stream().mapToInt(x -> x).toArray();
            val tempUpdate = new Update(0, tempPages, 0);

            if (tempUpdate.isGoodUpdate(rulesMap)) {
                newList.clear();
                newList.addAll(tempList);
                break;
            }

            i--;
        }

        if (i < 0) {
            log.error("good update not found on line {}", notcorrect.lineNumber);
        }
    }

    private record Update(int lineNumber, int[] pages, int middlePage) {
        private boolean isGoodUpdate(Map<Integer, List<Rule>> rulesMap) {
            return IntStream.range(0, pages.length).allMatch(i -> pageMatchesRules(rulesMap, i));
        }
    
        private boolean pageMatchesRules(Map<Integer, List<Rule>> rulesMap, int pageIndex) {
            val page = pages[pageIndex];
            val rules = rulesMap.get(page);
    
            if (rules == null) return true;
    
            return IntStream.range(0, pageIndex).allMatch(i -> pageIsNotInAfters(pages[i], rules));
        }
    
        private boolean pageIsNotInAfters(int priorPage, List<Rule> rules) {
            return !rules.stream().anyMatch(r -> r.afters.contains(priorPage));
        }
    }

    private record Rule(int page, List<Integer> afters) {}
    private record RuleLine(int page, int a) {}

    private List<Rule> buildRules(List<String> lines) {
        val pairs = lines.stream().map(l -> l.split("\\|")).map(i -> new RuleLine(Integer.parseInt(i[0]), Integer.parseInt(i[1]))).toList();
        val rawRules = pairs.stream().collect(Collectors.groupingBy(RuleLine::page, HashMap::new, Collectors.mapping(RuleLine::a, Collectors.toList())));
        return rawRules.entrySet().stream().map(es -> new Rule(es.getKey(), es.getValue())).toList();
    }

    private List<Update> buildUpdates(List<String> updLines) {
        val rawUpdates =  updLines.stream().map(this::mapToIntArray).toList();
        return IntStream.range(0, rawUpdates.size()).mapToObj(i -> new Update(i, rawUpdates.get(i), findMiddlePage(rawUpdates.get(i)))).toList();
    }

    private int findMiddlePage(int[] pages) {
        val i = (pages.length - 1) / 2;
        return pages[i];
    }

    private int[] mapToIntArray(String str) {
        return Arrays.stream(str.split(",")).mapToInt(s -> Integer.parseInt(s)).toArray();
    }
}
