package com.hanielcota.essentials.modules.tpa.menu;

import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.api.MenuSession;
import com.github.hanielcota.menuframework.definition.SlotDefinition;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.EssentialsMenu;
import com.hanielcota.essentials.menu.MenuLayouts;
import com.hanielcota.essentials.menu.PaginatedMenus;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.config.menu.TpaProfileMenuConfig;
import com.hanielcota.essentials.modules.tpa.menu.presentation.TpaProfileMenuRenderer;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class TpaProfileMenu implements EssentialsMenu {

  public static final String ID = "essentials.tpa.profile";

  private final ConfigHandle<TpaConfig> config;
  private final TpaProfileMenuRenderer renderer;

  static List<Integer> contentSlots(@NonNull TpaProfileMenuConfig settings, int rows) {
    return List.of(
        MenuLayouts.sanitizeSlot(settings.headSlot(), rows, 0),
        MenuLayouts.sanitizeSlot(settings.sentSlot(), rows, 0),
        MenuLayouts.sanitizeSlot(settings.receivedSlot(), rows, 0),
        MenuLayouts.sanitizeSlot(settings.acceptRateSlot(), rows, 0),
        MenuLayouts.sanitizeSlot(settings.avgResponseSlot(), rows, 0),
        MenuLayouts.sanitizeSlot(settings.mostContactedSlot(), rows, 0),
        MenuLayouts.sanitizeSlot(settings.backSlot(), rows, 0));
  }

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void register(@NonNull MenuService menus) {
    var settings = this.config.value().profileMenu();
    var rows = MenuLayouts.clampRows(settings.rows());
    var slots = contentSlots(settings, rows);

    PaginatedMenus.register(menus, ID, rows, settings.title(), slots, this::buildSlots);
  }

  private List<SlotDefinition> buildSlots(@NonNull Player player, @NonNull MenuSession session) {
    var settings = this.config.value().profileMenu();
    var rows = MenuLayouts.clampRows(settings.rows());

    return this.renderer.buildSlots(player, rows);
  }
}
