// package com.example.advent;

// import java.io.FileInputStream;
// import java.nio.charset.Charset;

// import org.apache.commons.io.IOUtils;
// import org.springframework.boot.CommandLineRunner;
// import org.springframework.stereotype.Component;

// import lombok.extern.slf4j.Slf4j;
// import lombok.val;

// @Slf4j
// @Component
// public class Advent2023_1_1 implements CommandLineRunner {

// 	@Override
// 	public void run(String... args) throws Exception {
// 		val is = new FileInputStream("/Users/gregorythompson/Projects/advent/2023_1.txt");

// 		val lines = IOUtils.readLines(is, Charset.defaultCharset());

// 		val sum = lines.stream().mapToLong(l -> {
// 			val rf = replaceWordsForward(l);
// 			val rb = replaceWordsBackword(l);
// 			val first = getFirst(rf);
// 			val last = getLast(rb);

// 			// log.info("f {}", first);
// 			// log.info("l {}", last);

// 			int value = (10 * first) + last;

// 			log.info("v {} {}", value, l);

// 			return value;
// 		}).sum();
// 		log.info("Answer is {}", sum);
// 	}
		
// 	private int getFirst(String l) {
// 		return l.chars().filter(c -> isNumber(c)).findFirst().orElse(0) - '0';
// 	}

// 	private int getLast(String l) {
// 		return new StringBuilder(l).reverse().toString().chars().filter(c -> isNumber(c)).findFirst().orElse(0) - '0';
// 	}

// 	boolean isNumber(int c) {
// 		return c <= '9' && c >= '0';
// 	}

// 	private String replaceWordsBackword(String l) {
// 		return l
// 		.replace("zerone", "1")
// 		.replace("zero", "0")
// 		.replace("oneight", "8")
// 		.replace("one", "1")
// 		.replace("two", "2")
// 		.replace("threight", "8")
// 		.replace("three", "3")
// 		.replace("four", "4")
// 		.replace("fiveight", "8")
// 		.replace("five", "5")
// 		.replace("six", "6")
// 		.replace("seven", "7")
// 		.replace("eight", "8")
// 		.replace("nine", "9")
// 		;
// 	}

// 	private String replaceWordsForward(String l) {
// 		return l
// 		.replace("zero", "0")
// 		.replace("twone", "2")
// 		.replace("one", "1")
// 		.replace("eightwo", "8")
// 		.replace("eighthree", "8")
// 		.replace("two", "2")
// 		.replace("three", "3")
// 		.replace("four", "4")
// 		.replace("five", "5")
// 		.replace("six", "6")
// 		.replace("seven", "7")
// 		.replace("eight", "8")
// 		.replace("nine", "9")
// 		;
// 	}
// }
