package com.example.advent;

import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import org.apache.commons.io.IOUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
// @Component
public class Advent2024_9_1 implements CommandLineRunner{

    @Override
    public void run(String... args) throws Exception {
        log.info("2024 9 1");

        val is = new FileInputStream("/Users/gregorythompson/Projects/advent/2024_9_1.txt");

        val lines = IOUtils.readLines(is, Charset.defaultCharset());

        val map = create2dArray(lines);
        val diskMap = map[0].length % 2 == 0 ? map[0] : createDiskMap(map[0]);

        val diskLength = calcDiskLength(diskMap);
        val fileLength = calcFileLength(diskMap);
        log.info("disk length {} file length {}", diskLength, fileLength);

        val disk = new char[diskLength];
        populateDisk(diskMap, disk);
        // printArray(disk);

        compressDisk(disk, fileLength);
        // printArray(disk);

        long cs = calcCheckSum(fileLength, disk);

        log.info("day 9 part 1 {}", cs); 
        // log.info("day 7 part 2 {}", sum + sum2); 
    } 

    private char[] createDiskMap(char[] input) {
        val diskMap = new char[input.length + 1];
        for (int i = 0; i < input.length; i++) {
            diskMap[i] = input[i];
        }

        diskMap[input.length] = '0';
        return diskMap;
    }

    private int calcDiskLength(char[] diskMap) {
        return IntStream.range(0, diskMap.length).map(i -> Integer.parseInt(diskMap[i] + "")).sum();
    }

    private int calcFileLength(char[] diskMap) {
        return IntStream.range(0, diskMap.length).filter(i -> i % 2 == 0).map(i -> Integer.parseInt(diskMap[i] + "")).sum();
    }

    private void compressDisk(char[] disk, int fileLength) {
        IntStream.range(0, fileLength)
            .filter(i -> disk[i] == '.')
            .forEach(i -> {
                val last = findLastFile(disk);
                disk[i] = disk[last];
                disk[last] = '.';
            });
    }

    private int findLastFile(char[] disk) {
        for (int i = disk.length - 1; i >= 0; i--) {
            if (disk[i] != '.') return i;
        }

        return disk.length - 1;
    }

    private void populateDisk(char[] diskMap, char[] disk) {
        val lastIndex = new AtomicInteger(0);
        val fileIndex = new AtomicInteger('0');
        IntStream.range(0, diskMap.length / 2).forEach(i -> {
            int fileLengthIndex = i * 2;
            int freeLengthIndex = (i * 2) + 1;
            int fileLength = Integer.parseInt(diskMap[fileLengthIndex] + "");
            int freeLength = Integer.parseInt(diskMap[freeLengthIndex] + "");

            // log.info("{} {} {} {} {} {}", fileLengthIndex, freeLengthIndex, fileLength, freeLength, lastIndex.get(), fileIndex.get());

            IntStream.range(lastIndex.get(), lastIndex.get() + fileLength).forEach(j -> disk[j] = (char) fileIndex.get());
            lastIndex.set(lastIndex.get() + fileLength);

            IntStream.range(lastIndex.get(), lastIndex.get() + freeLength).forEach(j -> disk[j] = '.');
            lastIndex.set(lastIndex.get() + freeLength);

            fileIndex.incrementAndGet();
        });

        // log.info("{}", disk);
    }

    private long calcCheckSum(int fileLength, char[] disk) {
        return IntStream.range(0, fileLength).mapToLong(i -> (disk[i] - '0') * i).sum();
    };

    private static void printArray(char[] array) {
        for (int y = 0; y < array.length; y++) {
            System.out.print(array[y]);
        }
        System.out.print("\n");
    }

    private char[][] create2dArray(List<String> lines) {
        val rowCount = lines.size();
        int colCount = lines.stream().findFirst().orElseThrow(() -> new RuntimeException()).length();

        val result = new char[rowCount][colCount];
        IntStream.range(0, rowCount).forEach(i -> result[i] = lines.get(i).toCharArray());

        return result;
    }
}
