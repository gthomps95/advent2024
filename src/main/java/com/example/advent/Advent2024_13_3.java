package com.example.advent;
// 
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Advent2024_13_3 implements CommandLineRunner {

    private final Advent2024_13_1 p1;
    private final Advent2024_13_4 p2;

    public Advent2024_13_3(Advent2024_13_1 p1, Advent2024_13_4 p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    @Override
    public void run(String... args) throws Exception {
        // val m1 = p1.execute();
        val m2 = p2.execute();

        // val missing = m1.stream().filter(m -> m2.stream().filter(n -> m.n() == n.n()).findFirst().get().t() != m.t()).toList();

        // missing.forEach(ma -> {
        //     val mb = m2.stream().filter(n -> ma.n() == n.n()).findFirst();
        //     log.info("m {} n {}", ma, mb);
        // });

        // log.info("{} missing", missing);
    }
}
