package com.hanielcota.essentials.modules.more.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MoreServiceTest {

  @Test
  void resultHasAllValues() {
    assertEquals(3, MoreService.Result.values().length);
  }
}
