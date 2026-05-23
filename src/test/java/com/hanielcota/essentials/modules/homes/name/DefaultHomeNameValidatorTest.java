package com.hanielcota.essentials.modules.homes.name;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DefaultHomeNameValidatorTest {

  private final DefaultHomeNameValidator validator = new DefaultHomeNameValidator();

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
    assertFalse(validator.isValid("a".repeat(33)));
  }
}
