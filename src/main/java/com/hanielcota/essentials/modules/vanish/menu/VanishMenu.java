package com.hanielcota.essentials.modules.vanish.menu;

import com.github.hanielcota.menuframework.api.ClickHandler;
import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.api.MenuSession;
import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.github.hanielcota.menuframework.definition.SlotDefinition;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.EssentialsMenu;
import com.hanielcota.essentials.menu.PaginatedInfoMenus;
import com.hanielcota.essentials.modules.vanish.config.VanishConfig;
import com.hanielcota.essentials.modules.vanish.config.VanishMenuLayout;
import com.hanielcota.essentials.modules.vanish.service.VanishService;
import com.hanielcota.essentials.paper.PlayerProvider;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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
  private final PlayerProvider players;

  private static @NonNull ItemTemplate buildInfoTemplate(@NonNull VanishConfig snap) {
    var material = snap.infoMaterial();
    var name = snap.infoName();
    var loreList = snap.infoLore();
    var loreArray = loreList.toArray(String[]::new);

    var builder = ItemTemplate.builder(material);
    builder.name(name);
    builder.lore(loreArray);
    builder.italic(false);

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
    var contentSlots = VanishMenuLayout.contentSlots(snap);
    var navigation = snap.navigation();
    var infoSlot = snap.effectiveInfoSlot();
    var infoTemplate = buildInfoTemplate(snap);

    PaginatedInfoMenus.register(
        menus, ID, rows, title, contentSlots, navigation, infoSlot, infoTemplate, this::buildSlots);
  }

  private List<SlotDefinition> buildSlots(@NonNull Player viewer, @NonNull MenuSession session) {
    var vanishedIds = this.service.vanished();
    var roster = collectOnline(vanishedIds, vanishedIds.size());

    if (roster.isEmpty()) {
      return emptyState();
    }

    var nameOrder = Comparator.comparing(Player::getName, String.CASE_INSENSITIVE_ORDER);
    roster.sort(nameOrder);

    var slots = new ArrayList<SlotDefinition>(roster.size());
    for (var player : roster) {
      var slot = buildEntrySlot(player);
      slots.add(slot);
    }
    return slots;
  }

  private List<Player> collectOnline(@NonNull Iterable<UUID> ids, int expectedSize) {
    var roster = new ArrayList<Player>(expectedSize);
    for (var id : ids) {
      var player = this.players.online(id).orElse(null);
      if (player == null) {
        continue;
      }
      roster.add(player);
    }

    return roster;
  }

  private SlotDefinition buildEntrySlot(@NonNull Player player) {
    var template = this.renderer.render(player);
    var targetId = player.getUniqueId();
    var targetName = player.getName();
    var onClick = entryClick(targetId, targetName);

    return SlotDefinition.of(-1, template, onClick);
  }

  private ClickHandler entryClick(@NonNull UUID targetId, @NonNull String targetName) {
    return click -> this.clickHandler.handle(click, targetId, targetName);
  }

  private List<SlotDefinition> emptyState() {
    var snap = this.config.value();
    var slots = VanishMenuLayout.contentSlots(snap);
    var midIndex = slots.size() / 2;
    var centerSlot = slots.get(midIndex);

    var emptyTemplate = this.renderer.renderEmpty();
    ClickHandler noop = click -> {};
    var slot = SlotDefinition.of(centerSlot, emptyTemplate, noop);

    return List.of(slot);
  }
}
