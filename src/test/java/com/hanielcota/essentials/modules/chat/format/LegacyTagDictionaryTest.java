package com.hanielcota.essentials.modules.chat.format;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class LegacyTagDictionaryTest {

  @Test
  void convertLegacyColorCodes() {
    var result = LegacyTagDictionary.convertLegacy("&aGreen &cRed", true, false);

    assertEquals("<green>Green <red>Red", result);
  }

  @Test
  void convertLegacyDecorationCodes() {
    var result = LegacyTagDictionary.convertLegacy("&lBold &oItalic", false, true);

    assertEquals("<bold>Bold <italic>Italic", result);
  }

  @Test
  void convertLegacyBothColorsAndDecorations() {
    var result = LegacyTagDictionary.convertLegacy("&a&lBold Green", true, true);

    assertEquals("<green><bold>Bold Green", result);
  }

  @Test
  void convertLegacyShortCircuitsWhenNoAmpersand() {
    var result = LegacyTagDictionary.convertLegacy("plain text", true, true);

    assertEquals("plain text", result);
  }

  @Test
  void convertLegacyIgnoresAmpersandAtEnd() {
    var result = LegacyTagDictionary.convertLegacy("trailing&", true, true);

    assertEquals("trailing&", result);
  }

  @Test
  void gatedByColorPermission() {
    var result = LegacyTagDictionary.convertLegacy("&aGreen &lBold", true, false);

    assertEquals("<green>Green &lBold", result);
  }

  @Test
  void gatedByDecorationPermission() {
    var result = LegacyTagDictionary.convertLegacy("&aGreen &lBold", false, true);

    assertEquals("&aGreen <bold>Bold", result);
  }

  @Test
  void tagForReturnsNullForUnknownCode() {
    assertNull(LegacyTagDictionary.tagFor('z', true, true));
  }

  @Test
  void tagForReturnsNullForCodeAboveTableSize() {
    assertNull(LegacyTagDictionary.tagFor('\u00FF', true, true));
  }

  @Test
  void resetCodeIsInDecorationTable() {
    var tag = LegacyTagDictionary.tagFor('r', false, true);

    assertEquals("<reset>", tag);
  }

  @Test
  void allTagsTableContainsBothColorAndDecoration() {
    assertEquals("<green>", LegacyTagDictionary.ALL_TAGS['a']);
    assertEquals("<bold>", LegacyTagDictionary.ALL_TAGS['l']);
    assertNull(LegacyTagDictionary.tagFor('z', true, true));
  }
}
