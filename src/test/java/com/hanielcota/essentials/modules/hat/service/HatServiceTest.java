package com.hanielcota.essentials.modules.hat.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class HatServiceTest {

  @Test
  void resultHasAllValues() {
    assertEquals(4, HatService.Result.values().length);
  }
}
