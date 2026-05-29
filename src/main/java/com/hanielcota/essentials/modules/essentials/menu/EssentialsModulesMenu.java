package com.hanielcota.essentials.modules.essentials.menu;

import com.github.hanielcota.menuframework.MenuFramework;
import com.github.hanielcota.menuframework.api.ClickContext;
import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.api.MenuSession;
import com.github.hanielcota.menuframework.definition.PaginationConfig;
import com.github.hanielcota.menuframework.definition.SlotDefinition;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.EssentialsMenu;
import com.hanielcota.essentials.module.control.ModuleControl;
import com.hanielcota.essentials.modules.essentials.config.EssentialsConfig;
import com.hanielcota.essentials.shared.ComponentUtils;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/**
 * Single admin menu listing the modules of the selected category. Clicking a module flips its
 * persisted switch in {@code modules.yml} (applied on the next restart); clicking the filter button
 * cycles the category. Both re-render this same inventory via {@link ClickContext#refresh()}.
 */
@RequiredArgsConstructor
public final class EssentialsModulesMenu implements EssentialsMenu {

  public static final String ID = "essentials.modules";

  private final @NonNull ConfigHandle<EssentialsConfig> config;
  private final @NonNull EssentialsModulesMenuState state;
  private final @NonNull ModulesMenuRenderer renderer;
  private final @NonNull ModuleControl control;

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void register(@NonNull MenuService menus) {
    var snap = this.config.value();
    var menu = snap.menu();
    var rows = menu.effectiveRows();

    var rawTitle = menu.title();
    var menuTitle = ComponentUtils.mini(rawTitle);

    // The framework requires content slots whenever dynamicContent is used. Every item is placed at
    // an explicit slot (modules in the content slots, guide + filter at their own slots), so
    // pagination never has to project a -1 item.
    var contentSlots = menu.effectiveContentSlots();
    var pagination = PaginationConfig.builder().contentSlots(contentSlots).build();

    var builder = MenuFramework.builder(ID, menus);
    builder.rows(rows);
    builder.title(menuTitle);
    builder.pagination(pagination);
    builder.dynamicContent(this::buildSlots);

    builder.buildAndRegister();
  }

  private List<SlotDefinition> buildSlots(@NonNull Player player, @NonNull MenuSession session) {
    var viewerId = player.getUniqueId();
    var category = this.state.category(viewerId);

    return this.renderer.slots(category, this::cycleCategory, this::toggleModule);
  }

  private void cycleCategory(@NonNull ClickContext click) {
    var viewer = click.player();
    var viewerId = viewer.getUniqueId();

    var current = this.state.category(viewerId);
    var next = current.next();

    this.state.switchCategory(viewerId, next);
    click.refresh();
  }

  private void toggleModule(@NonNull ClickContext click, @NonNull String moduleId) {
    var viewer = click.player();
    var nowEnabled = this.control.toggle(moduleId);

    var snap = this.config.value();
    var menu = snap.menu();
    var feedback = menu.toggleFeedback(nowEnabled, moduleId);
    var component = ComponentUtils.mini(feedback);

    viewer.sendMessage(component);
    click.refresh();
  }
}
