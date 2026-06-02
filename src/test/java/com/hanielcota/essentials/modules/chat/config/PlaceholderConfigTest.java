package com.hanielcota.essentials.modules.chat.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PlaceholderConfigTest {

  @Test
  void defaultsProvideVaultPrefixAndSuffix() {
    var snap = PlaceholderConfig.defaults();

    assertEquals("%vault_prefix%", snap.prefixPlaceholder());
    assertEquals("%vault_suffix%", snap.suffixPlaceholder());
  }

  @Test
  void emptyPlaceholdersAreAllowed() {
    var snap = new PlaceholderConfig("", "");

    assertEquals("", snap.prefixPlaceholder());
    assertEquals("", snap.suffixPlaceholder());
  }
}
