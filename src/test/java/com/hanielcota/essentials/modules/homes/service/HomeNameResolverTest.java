package com.hanielcota.essentials.modules.homes.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class HomeNameResolverTest {

  @Test
  void rejectsBlankInput() {
    var resolver = new HomeNameResolver(new HomeNameValidator());

    assertNull(resolver.resolve(""));
  }

  @Test
  void rejectsInvalidCharacters() {
    var resolver = new HomeNameResolver(new HomeNameValidator());

    assertNull(resolver.resolve("<red>base"));
  }

  @Test
  void passesThroughValidName() {
    var resolver = new HomeNameResolver(new HomeNameValidator());

    assertEquals("base", resolver.resolve("base"));
  }
}
