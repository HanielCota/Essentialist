package com.hanielcota.essentials.modules.itemlore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ItemLoreServiceTest {

  @Test
  void allResultsAreDefined() {
    assertEquals(7, ItemLoreService.Result.values().length);
  }
}
