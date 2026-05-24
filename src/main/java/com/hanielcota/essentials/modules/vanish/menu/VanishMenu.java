package com.hanielcota.essentials.modules.vanish.menu;

import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.api.MenuSession;
import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.github.hanielcota.menuframework.definition.SlotDefinition;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.EssentialsMenu;
import com.hanielcota.essentials.menu.PaginatedInfoMenus;
import com.hanielcota.essentials.modules.vanish.config.VanishConfig;
import com.hanielcota.essentials.modules.vanish.service.VanishService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class VanishMenu implements EssentialsMenu {

  // Matches the convention shared by every other menu (WhitelistMenu.ID, BackMenu.ID, etc.).
  // The id()/ID lookalike is intentional — keeping the constant uppercase and the accessor
  // lowercase preserves both the field-as-constant convention and the EssentialsMenu interface.
  @SuppressWarnings("java:S1845")
  public static final String ID = "essentials.vanish.list";

  private final ConfigHandle<VanishConfig> config;
  private final VanishService service;
  private final VanishEntryRenderer renderer;
  private final VanishClickHandler clickHandler;

  private static @NonNull ItemTemplate buildInfoTemplate(@NonNull VanishConfig snap) {
    return ItemTemplate.builder(snap.infoMaterial())
        .name(snap.infoName())
        .lore(snap.infoLore().toArray(String[]::new))
        .italic(false)
        .build();
  }

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void register(@NonNull MenuService menus) {
    var snap = this.config.value();
    PaginatedInfoMenus.register(
        menus,
        ID,
        snap.effectiveRows(),
        snap.menuTitle(),
        snap.effectiveContentSlots(),
        snap.navigation(),
        snap.effectiveInfoSlot(),
        buildInfoTemplate(snap),
        this::buildSlots);
  }

  private List<SlotDefinition> buildSlots(@NonNull Player viewer, @NonNull MenuSession session) {
    var vanishedIds = this.service.vanished();
    var players = new ArrayList<Player>(vanishedIds.size());
    for (var id : vanishedIds) {
      var player = Bukkit.getPlayer(id);
      if (player != null) {
        players.add(player);
      }
    }
    if (players.isEmpty()) {
      return emptyState();
    }
    players.sort(Comparator.comparing(Player::getName, String.CASE_INSENSITIVE_ORDER));

    var slots = new ArrayList<SlotDefinition>(players.size());
    for (var player : players) {
      var template = this.renderer.render(player);
      var targetId = player.getUniqueId();
      var targetName = player.getName();
      slots.add(
          SlotDefinition.of(
              -1, template, click -> this.clickHandler.handle(click, targetId, targetName)));
    }
    return slots;
  }

  private List<SlotDefinition> emptyState() {
    var slots = this.config.value().effectiveContentSlots();
    var centerSlot = slots.get(slots.size() / 2);

    return List.of(SlotDefinition.of(centerSlot, this.renderer.renderEmpty(), click -> {}));
  }
}
