package com.example.advent;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import lombok.val;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

public class Advent2024_4_1Tests {

    private record TestInputDRL(int d, int rc, int cc, int drl) {}

    private static List<TestInputDRL> testInputs() {
        return List.of(
            new TestInputDRL(0, 1, 1, 1),
            new TestInputDRL(0, 10, 9, 1),
            new TestInputDRL(1, 2, 2, 2),
            new TestInputDRL(5, 10, 2, 2),
            new TestInputDRL(5, 10, 20, 6),
            new TestInputDRL(6, 3, 6, 2),
            new TestInputDRL(9, 10, 20, 10),
            new TestInputDRL(12, 10, 20, 10),
            new TestInputDRL(25, 10, 20, 4),
            new TestInputDRL(28, 10, 20, 1),
            new TestInputDRL(25, 20, 10, 4),
            new TestInputDRL(11, 10, 12, 10),
            new TestInputDRL(0, 1, 1, 1)
        );
    }

    @MethodSource("testInputs")
    @ParameterizedTest
    void testCalcDiagonalRowLength(TestInputDRL input) {
        // arrange
        val sut = new Advent2024_4_1();

        // act
        val length = sut.calcDiagRowLength(input.d, input.rc, input.cc);

        // assert
        assertThat(length).isEqualTo(input.drl);
    }
}
