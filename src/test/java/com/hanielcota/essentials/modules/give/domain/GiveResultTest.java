package com.hanielcota.essentials.modules.give.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class GiveResultTest {

  @Test
  void ofComputesGivenAsRequestedMinusLeftover() {
    var result = GiveResult.of(64, 10);

    assertEquals(64, result.requested());
    assertEquals(54, result.given());
    assertEquals(10, result.leftover());
  }

  @Test
  void ofWithNoLeftover() {
    var result = GiveResult.of(64, 0);

    assertEquals(64, result.requested());
    assertEquals(64, result.given());
    assertEquals(0, result.leftover());
  }

  @Test
  void ofWithAllLeftover() {
    var result = GiveResult.of(64, 64);

    assertEquals(64, result.requested());
    assertEquals(0, result.given());
    assertEquals(64, result.leftover());
  }

  @Test
  void noneGivenWhenGivenIsZero() {
    var result = GiveResult.of(10, 10);

    assertTrue(result.noneGiven());
  }

  @Test
  void noneGivenWhenGivenIsPositive() {
    var result = GiveResult.of(10, 5);

    assertFalse(result.noneGiven());
  }

  @Test
  void partialWhenLeftoverIsPositive() {
    var result = GiveResult.of(10, 3);

    assertTrue(result.partial());
  }

  @Test
  void partialWhenNoLeftover() {
    var result = GiveResult.of(10, 0);

    assertFalse(result.partial());
  }
}
