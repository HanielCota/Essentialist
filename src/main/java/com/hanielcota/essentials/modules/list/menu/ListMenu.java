package com.hanielcota.essentials.modules.list.menu;

import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.api.MenuSession;
import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.github.hanielcota.menuframework.definition.SlotDefinition;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.EssentialsMenu;
import com.hanielcota.essentials.menu.PaginatedInfoMenus;
import com.hanielcota.essentials.modules.list.config.ListConfig;
import com.hanielcota.essentials.modules.list.service.ListService;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

@RequiredArgsConstructor
public final class ListMenu implements EssentialsMenu {

  public static final String ID = "essentials.list";

  private final ConfigHandle<ListConfig> config;
  private final ListService service;
  private final ListEntryRenderer renderer;

  private static @NonNull ItemTemplate buildInfoTemplate(@NonNull ListConfig snap) {
    var material = snap.infoMaterial();
    var name = snap.infoName();
    var lore = snap.infoLore();
    var loreArray = lore.toArray(String[]::new);

    var builder = ItemTemplate.builder(material);
    builder = builder.name(name);
    builder = builder.lore(loreArray);
    builder = builder.flags(ItemFlag.HIDE_ATTRIBUTES);
    builder = builder.italic(false);

    return builder.build();
  }

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void register(@NonNull MenuService menus) {
    var snap = this.config.value();
    var rows = snap.effectiveRows();
    var title = snap.menuTitle();
    var contentSlots = snap.effectiveContentSlots();
    var navigation = snap.navigation();
    var infoSlot = snap.effectiveInfoSlot();
    var infoTemplate = buildInfoTemplate(snap);

    PaginatedInfoMenus.register(
        menus, ID, rows, title, contentSlots, navigation, infoSlot, infoTemplate, this::buildSlots);
  }

  private List<SlotDefinition> buildSlots(@NonNull Player viewer, @NonNull MenuSession session) {
    var roster = this.service.roster(viewer);
    if (roster.isEmpty()) {
      return emptyState();
    }

    var slots = new ArrayList<SlotDefinition>(roster.size());

    for (var entry : roster) {
      var template = this.renderer.render(entry);
      var slotDef = SlotDefinition.of(-1, template, click -> {});

      slots.add(slotDef);
    }

    return slots;
  }

  /** A single placeholder item centred in the content area, shown when no players are visible. */
  private List<SlotDefinition> emptyState() {
    var snap = this.config.value();
    var slots = snap.effectiveContentSlots();
    var centerSlot = slots.get(slots.size() / 2);
    var emptyTemplate = this.renderer.renderEmpty();

    var slotDef = SlotDefinition.of(centerSlot, emptyTemplate, click -> {});

    return List.of(slotDef);
  }
}
