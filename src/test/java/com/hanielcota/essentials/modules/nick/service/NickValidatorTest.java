package com.hanielcota.essentials.modules.nick.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class NickValidatorTest {

  @Test
  void rejectsEmptyName() {
    var result = NickValidator.check("", 3, 16);
    assertEquals(NickValidator.Result.TOO_SHORT, result);
  }

  @Test
  void rejectsBelowMinLength() {
    var result = NickValidator.check("ab", 3, 16);
    assertEquals(NickValidator.Result.TOO_SHORT, result);
  }

  @Test
  void rejectsAboveMaxLength() {
    var result = NickValidator.check("aVeryLongNicknameThatExceedsTheMax", 3, 16);
    assertEquals(NickValidator.Result.TOO_LONG, result);
  }

  @Test
  void rejectsInvalidCharacters() {
    var result = NickValidator.check("nick name", 3, 16);
    assertEquals(NickValidator.Result.INVALID_CHARS, result);
  }

  @Test
  void rejectsColorCodes() {
    var result = NickValidator.check("&6nick", 3, 16);
    assertEquals(NickValidator.Result.INVALID_CHARS, result);
  }

  @Test
  void acceptsValidNickname() {
    var result = NickValidator.check("Ace_123", 3, 16);
    assertEquals(NickValidator.Result.OK, result);
  }

  @Test
  void acceptsExactMinLength() {
    var result = NickValidator.check("abc", 3, 16);
    assertEquals(NickValidator.Result.OK, result);
  }

  @Test
  void acceptsExactMaxLength() {
    var result = NickValidator.check("abcdefghijklmnop", 3, 16);
    assertEquals(NickValidator.Result.OK, result);
  }

  @Test
  void rejectsSpecialCharacters() {
    var result = NickValidator.check("nick!", 3, 16);
    assertEquals(NickValidator.Result.INVALID_CHARS, result);
  }

  @Test
  void acceptsUnderscoreOnly() {
    var result = NickValidator.check("player_one", 3, 16);
    assertEquals(NickValidator.Result.OK, result);
  }
}
