package com.hanielcota.essentials.modules.rename.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class RenameServiceTest {

  @Test
  void resultHasAllValues() {
    assertEquals(3, RenameService.Result.values().length);
  }
}
