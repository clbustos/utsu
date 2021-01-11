package com.utsusynth.utsu.common.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Tests for {@link RoundUtils}. */
public class RoundUtilsTest {
  @Test
  public void testRoundDecimal() {
    assertEquals(
            "1.0",
            RoundUtils.roundDecimal(0.99999, "#.#"),
            "Testing a decimal number"
    );
    assertEquals(
            "100.0",
            RoundUtils.roundDecimal(99.99999, "#.#"),
            "Testing a decimal number"
    );
    assertEquals(
            "101.4",
            RoundUtils.roundDecimal(101.4, "#.#"),
            "Testing a decimal number"
    );
  }
}
