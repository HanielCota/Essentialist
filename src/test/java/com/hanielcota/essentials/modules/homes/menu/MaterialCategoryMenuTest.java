package com.hanielcota.essentials.modules.homes.menu;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.github.hanielcota.menuframework.api.MenuSession;
import com.github.hanielcota.menuframework.definition.SlotDefinition;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import com.hanielcota.essentials.modules.homes.config.menu.HomesCategoryMenuConfig;
import com.hanielcota.essentials.modules.homes.config.menu.HomesMenuConfig;
import com.hanielcota.essentials.modules.homes.menu.material.MaterialCategoryClickHandler;
import com.hanielcota.essentials.modules.homes.menu.material.MaterialCategoryMenu;
import com.hanielcota.essentials.modules.homes.menu.presentation.MaterialCategorySection;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.List;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

class MaterialCategoryMenuTest {

  @Test
  void categoryContentDoesNotPageTheBackButton() throws Exception {
    var config = HomesConfig.defaults();
    var menu =
        new MaterialCategoryMenu(
            config(config), new MaterialCategoryClickHandler(new HomesActionTarget()));
    var slots = dynamicSlotsOf(menu);
    var backSlot = MaterialCategorySection.backSlot(config.menu().category());

    var hasBackButton = slots.stream().anyMatch(slot -> slot.slot() == backSlot);

    assertFalse(hasBackButton);
  }

  @Test
  void disabledCategoryBackButtonDoesNotCreateBackSlot() throws Exception {
    var config = homesWithCategoryBack(false);
    var menu =
        new MaterialCategoryMenu(
            config(config), new MaterialCategoryClickHandler(new HomesActionTarget()));

    var backSlot = backSlotOf(menu);

    assertNull(backSlot);
  }

  @SuppressWarnings("unchecked")
  private static List<SlotDefinition> dynamicSlotsOf(MaterialCategoryMenu menu) throws Exception {
    var method =
        MaterialCategoryMenu.class.getDeclaredMethod("buildSlots", Player.class, MenuSession.class);
    method.setAccessible(true);

    var player = proxy(Player.class);
    var session = proxy(MenuSession.class);

    return (List<SlotDefinition>) method.invoke(menu, player, session);
  }

  private static <T> T proxy(Class<T> type) {
    var loader = type.getClassLoader();
    var interfaces = new Class<?>[] {type};
    InvocationHandler handler = (proxy, method, args) -> null;

    return type.cast(Proxy.newProxyInstance(loader, interfaces, handler));
  }

  private static Object backSlotOf(MaterialCategoryMenu menu) throws Exception {
    var method = MaterialCategoryMenu.class.getDeclaredMethod("backButtonSlot");
    method.setAccessible(true);

    return method.invoke(menu);
  }

  private static HomesConfig homesWithCategoryBack(boolean enabled) throws Exception {
    var original = HomesConfig.defaults();
    var menu = menuWithCategoryBack(original.menu(), enabled);

    return new HomesConfig(
        original.teleportDelaySeconds(),
        original.defaultLimit(),
        original.defaultMaterial(),
        original.renameTimeoutSeconds(),
        menu,
        original.messages());
  }

  private static HomesMenuConfig menuWithCategoryBack(HomesMenuConfig original, boolean enabled)
      throws Exception {
    var originalCategory = original.category();
    var newCategory =
        new HomesCategoryMenuConfig(
            originalCategory.title(),
            originalCategory.rows(),
            originalCategory.contentSlots(),
            originalCategory.itemName(),
            originalCategory.itemLore(),
            originalCategory.names(),
            enabled,
            originalCategory.backSlot(),
            originalCategory.backMaterial(),
            originalCategory.backName(),
            originalCategory.backLore());

    return new HomesMenuConfig(
        original.main(),
        newCategory,
        original.picker(),
        original.deleteDialog(),
        original.options());
  }

  private static ConfigHandle<HomesConfig> config(HomesConfig value) {
    return new ConfigHandle<>() {
      @Override
      public String name() {
        return "homes";
      }

      @Override
      public HomesConfig value() {
        return value;
      }
    };
  }
}
