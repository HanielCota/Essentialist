package com.hanielcota.essentials.modules.essentials.menu;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ModuleCategoryCatalogTest {

  @Test
  void mapsKnownModulesToTheirCategory() {
    assertEquals(ModuleCategory.PROTECTION, ModuleCategoryCatalog.categoryOf("leaves"));
    assertEquals(ModuleCategory.TELEPORT, ModuleCategoryCatalog.categoryOf("homes"));
    assertEquals(ModuleCategory.CHAT, ModuleCategoryCatalog.categoryOf("mute"));
    assertEquals(ModuleCategory.ITEMS, ModuleCategoryCatalog.categoryOf("enchant"));
    assertEquals(ModuleCategory.PLAYER, ModuleCategoryCatalog.categoryOf("fly"));
    assertEquals(ModuleCategory.ADMIN, ModuleCategoryCatalog.categoryOf("whitelist"));
  }

  @Test
  void unknownModuleFallsBackToOther() {
    assertEquals(ModuleCategory.OTHER, ModuleCategoryCatalog.categoryOf("does-not-exist"));
  }
}
