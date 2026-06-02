package com.hanielcota.essentials.modules.enchant.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class EnchantServiceTest {

  @Test
  void applyResultHasAllValues() {
    assertEquals(5, EnchantService.ApplyResult.values().length);
  }

  @Test
  void removeResultHasAllValues() {
    assertEquals(3, EnchantService.RemoveResult.values().length);
  }
}
