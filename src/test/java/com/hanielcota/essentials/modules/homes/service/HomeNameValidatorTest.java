package com.hanielcota.essentials.modules.homes.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class HomeNameValidatorTest {

  private final HomeNameValidator validator = new HomeNameValidator(1, 16, "[A-Za-z0-9_-]+");

  @Test
  void acceptsSimpleNamesWithLettersNumbersUnderscoreAndHyphen() {
    assertTrue(validator.isValid("home"));
    assertTrue(validator.isValid("base_1"));
    assertTrue(validator.isValid("nether-base"));
  }

  @Test
  void rejectsNamesThatWouldBreakCommandsOrMiniMessageRendering() {
    assertFalse(validator.isValid(""));
    assertFalse(validator.isValid("two words"));
    assertFalse(validator.isValid("<red>home"));
    assertFalse(validator.isValid("home/name"));
  }

  @Test
  void enforcesTheConfiguredMaxLength() {
    assertTrue(validator.isValid("a".repeat(16)));
    assertFalse(validator.isValid("a".repeat(17)));
  }
}
