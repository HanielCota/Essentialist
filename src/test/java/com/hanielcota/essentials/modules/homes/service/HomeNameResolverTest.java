package com.hanielcota.essentials.modules.homes.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class HomeNameResolverTest {

  @Test
  void rejectsBlankInput() {
    var resolver = new HomeNameResolver(new HomeNameValidator());

    assertTrue(resolver.resolve("").isEmpty());
  }

  @Test
  void rejectsInvalidCharacters() {
    var resolver = new HomeNameResolver(new HomeNameValidator());

    assertTrue(resolver.resolve("<red>base").isEmpty());
  }

  @Test
  void passesThroughValidName() {
    var resolver = new HomeNameResolver(new HomeNameValidator());

    assertEquals("base", resolver.resolve("base").orElseThrow());
  }
}
