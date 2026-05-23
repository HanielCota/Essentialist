package com.hanielcota.essentials.modules.whitelist.menu;

import com.github.hanielcota.menuframework.MenuFramework;
import com.github.hanielcota.menuframework.api.Menu;
import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.api.MenuSession;
import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.github.hanielcota.menuframework.definition.PaginationConfig;
import com.github.hanielcota.menuframework.definition.SlotDefinition;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.whitelist.config.WhitelistConfig;
import com.hanielcota.essentials.modules.whitelist.service.WhitelistService;
import com.hanielcota.essentials.util.ComponentUtils;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class WhitelistMenu implements Menu {

  public static final String ID = "essentials.whitelist";

  private static final int MIN_ROWS = 1;
  private static final int SLOTS_PER_ROW = 9;

  /**
   * A handler-less dynamic slot. The framework skips it when rendering but still advances the slot
   * index, which lets {@link #emptyState()} push its single item into a centred slot.
   */
  private static final SlotDefinition SKIP = SlotDefinition.withHandler(-1, null);

  private final ConfigHandle<WhitelistConfig> config;
  private final WhitelistService service;
  private final WhitelistEntryRenderer renderer;
  private final WhitelistClickHandler clickHandler;

  // Content slots cover every row but the last, which is left for pagination controls.
  private static List<Integer> contentSlots(int rows) {
    var count = rows > MIN_ROWS ? (rows - 1) * SLOTS_PER_ROW : SLOTS_PER_ROW;
    var slots = new ArrayList<Integer>(count);

    for (var i = 0; i < count; i++) slots.add(i);
    return slots;
  }

  private static ItemTemplate pageButton(@NonNull String name) {
    return ItemTemplate.builder(Material.ARROW).name(name).italic(false).build();
  }

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void register(@NonNull MenuService menus) {
    var snap = this.config.value();
    var rows = snap.effectiveRows();
    var pagination = PaginationConfig.builder().contentSlots(contentSlots(rows)).build();

    var menuTitle = ComponentUtils.mini(snap.menuTitle());
    var builder =
        MenuFramework.builder(ID, menus)
            .rows(rows)
            .title(menuTitle)
            .pagination(pagination)
            .dynamicContent(this::buildSlots);

    // Without page buttons a whitelist larger than one page is unreachable. The last
    // row is free for them only when the menu has more than one row.
    if (rows > MIN_ROWS) {
      var lastRow = (rows - 1) * SLOTS_PER_ROW;
      builder
          .previousPageButton(lastRow + 3, pageButton("<yellow>« Página anterior"))
          .nextPageButton(lastRow + 5, pageButton("<yellow>Próxima página »"));
    }

    builder.build().register();
  }

  private List<SlotDefinition> buildSlots(@NonNull Player player, @NonNull MenuSession session) {
    var whitelisted = this.service.list();
    if (whitelisted.isEmpty()) {
      return emptyState();
    }
    var slots = new ArrayList<SlotDefinition>(whitelisted.size());
    for (var entry : whitelisted) {
      var template = this.renderer.render(entry);
      slots.add(SlotDefinition.of(-1, template, click -> this.clickHandler.handle(click, entry)));
    }
    return slots;
  }

  /**
   * A single placeholder item centred in the content area, shown when the whitelist is empty. The
   * framework maps the n-th dynamic item to content slot n, so the item is preceded by {@link
   * #SKIP} entries that consume the leading indices without rendering anything.
   */
  private List<SlotDefinition> emptyState() {
    var contentRows = contentSlots(this.config.value().effectiveRows()).size() / SLOTS_PER_ROW;
    var centerSlot = (contentRows / 2) * SLOTS_PER_ROW + SLOTS_PER_ROW / 2;

    var slots = new ArrayList<SlotDefinition>(centerSlot + 1);
    for (var i = 0; i < centerSlot; i++) {
      slots.add(SKIP);
    }
    slots.add(SlotDefinition.of(-1, this.renderer.renderEmpty(), click -> {}));
    return slots;
  }
}
