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
public class Advent2024_9_2 implements CommandLineRunner{

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

        compactDisk(disk);
        // printArray(disk);

        long cs = calcCheckSum(disk);

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

    private record Range(char c, int start, int length) {}

    private void compactDisk(char[] disk) {
        int starting = disk.length - 1;
        var file = findFirstFile(disk, starting);
        // log.info("file {}", file);

        while (file != null) {
            var freeRange = findFree(disk, 0, file);
            // log.info("free {}", freeRange);

            if (freeRange != null) {
                moveFile(disk, freeRange, file);
            }    

            starting = file.start - 1;
            file = findFirstFile(disk, starting);
            // log.info("file {}", file);
        }
    }

    private void moveFile(char[] disk, Range freeRange, Range fit) {
        // log.info("Moving {} to {}", fit, freeRange);
        for (int i = 0; i < fit.length; i++) {
            if (disk[freeRange.start + i] != '.') throw new RuntimeException("Writing into non free space.");
            disk[freeRange.start + i] = disk[fit.start + i];
            disk[fit.start + i] = '.';
        }
        // printArray(disk);
    }

    private Range findFirstFile(char[] disk, int start) {
        // log.info("start {}   ", start);
        int index = start;

        if (index < 0) {
            return null;
        }

        Range result = null;

        var c = disk[index];

        while (c == '.' && index >= 0) {
            index--;
            c = disk[index];

            if (index % 1000 == 0) {
                log.info("index {}", index);
            }
        }

        int length = 0;
        while (index >= 0 && disk[index] == c) {
            index--;
            length++;

            if (index % 1000 == 0) {
                log.info("index {}", index);
            }
        }

        if (length > 0) {
            result = new Range(c, index + 1, length);
        }

        return result;
    }

    private Range findFree(char[] disk, int starting, Range file) {
        Range free = null;
        int next = starting;
        int freeLength = 1;

        while (free == null && freeLength > 0) {
            freeLength = 0;
            int start = starting + next;            
            for (int i = start; i < file.start; i++) {
                if (disk[i] == '.') {
                    start = i;
                    break;
                }
            }

            for (int i = start; i < file.start; i++) {
                if (disk[i] == '.') {
                    freeLength++;
                } else {
                    break;
                }
            }

            if (freeLength >= file.length) {
                free = new Range('.', start, freeLength);
            }

            next += freeLength;
        }

        return free;
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

    private long calcCheckSum(char[] disk) {
        return IntStream.range(0, disk.length).filter(i -> disk[i] != '.').mapToLong(i -> (disk[i] - '0') * i).sum();
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
