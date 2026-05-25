package com.hanielcota.essentials.modules.whitelist.menu;

import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.api.MenuSession;
import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.github.hanielcota.menuframework.definition.SlotDefinition;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.EssentialsMenu;
import com.hanielcota.essentials.menu.PaginatedInfoMenus;
import com.hanielcota.essentials.modules.whitelist.config.WhitelistConfig;
import com.hanielcota.essentials.modules.whitelist.service.WhitelistService;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class WhitelistMenu implements EssentialsMenu {

  public static final String ID = "essentials.whitelist";

  private final ConfigHandle<WhitelistConfig> config;
  private final WhitelistService service;
  private final WhitelistEntryRenderer renderer;
  private final WhitelistClickHandler clickHandler;

  private static @NonNull ItemTemplate buildInfoTemplate(@NonNull WhitelistConfig snap) {
    var material = snap.infoMaterial();
    var name = snap.infoName();
    var lore = snap.infoLore();
    var loreArray = lore.toArray(String[]::new);

    var builder = ItemTemplate.builder(material);
    builder = builder.name(name);
    builder = builder.lore(loreArray);
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

  private List<SlotDefinition> buildSlots(@NonNull Player player, @NonNull MenuSession session) {
    var whitelisted = this.service.list();
    if (whitelisted.isEmpty()) {
      return emptyState();
    }

    var slots = new ArrayList<SlotDefinition>(whitelisted.size());

    for (var entry : whitelisted) {
      var template = this.renderer.render(entry);
      var slotDef =
          SlotDefinition.of(-1, template, click -> this.clickHandler.handle(click, entry));

      slots.add(slotDef);
    }

    return slots;
  }

  /** A single placeholder item centred in the content area, shown when the whitelist is empty. */
  private List<SlotDefinition> emptyState() {
    var snap = this.config.value();
    var slots = snap.effectiveContentSlots();
    var centerSlot = slots.get(slots.size() / 2);
    var emptyTemplate = this.renderer.renderEmpty();

    var slotDef = SlotDefinition.of(centerSlot, emptyTemplate, click -> {});

    return List.of(slotDef);
  }
}
