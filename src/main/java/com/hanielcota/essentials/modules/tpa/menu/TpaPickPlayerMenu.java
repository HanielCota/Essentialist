package com.hanielcota.essentials.modules.tpa.menu;

import com.github.hanielcota.menuframework.MenuFramework;
import com.github.hanielcota.menuframework.api.ClickContext;
import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.api.MenuSession;
import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.github.hanielcota.menuframework.definition.PaginationConfig;
import com.github.hanielcota.menuframework.definition.SlotDefinition;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.EssentialsMenu;
import com.hanielcota.essentials.menu.MenuLayouts;
import com.hanielcota.essentials.menu.MenuTemplates;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.config.menu.TpaPickPlayerMenuConfig;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequestType;
import com.hanielcota.essentials.modules.tpa.domain.TpaTargetSelection;
import com.hanielcota.essentials.modules.tpa.service.TpaTargetSelections;
import com.hanielcota.essentials.paper.PlayerProvider;
import com.hanielcota.essentials.shared.ComponentUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Paginated picker of every online player (except the viewer) opened from the hub's TPA slot.
 * Clicking a head stashes a {@link TpaTargetSelection} with {@code preferredType=TPA} and switches
 * to {@link TpaTargetActionMenu}.
 */
@RequiredArgsConstructor
public final class TpaPickPlayerMenu implements EssentialsMenu {

  public static final String ID = "essentials.tpa.pick.player";

  private final ConfigHandle<TpaConfig> config;
  private final PlayerProvider players;
  private final TpaTargetSelections selections;

  static List<Integer> contentSlots(@NonNull TpaPickPlayerMenuConfig settings, int rows) {
    var slotCount = MenuLayouts.slotCount(rows);
    var fallbackWidth = Math.min(7, slotCount);
    var fallback = MenuLayouts.fallbackContentSlots(rows, fallbackWidth);

    return MenuLayouts.sanitizeSlots(settings.contentSlots(), rows, fallback);
  }

  private static List<String> replacePlayer(@NonNull List<String> lines, @NonNull String player) {
    var replaced = new ArrayList<String>(lines.size());
    for (var line : lines) {
      replaced.add(line.replace("{player}", player));
    }
    return replaced;
  }

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void register(@NonNull MenuService menus) {
    var settings = this.config.value().pickPlayerMenu();
    var rows = MenuLayouts.clampRows(settings.rows());
    var slots = contentSlots(settings, rows);
    var pagination = PaginationConfig.builder().contentSlots(slots).build();

    var builder = MenuFramework.builder(ID, menus);
    builder.rows(rows);
    builder.title(ComponentUtils.mini(settings.title()));
    builder.pagination(pagination);
    builder.dynamicContent(this::buildSlots);

    var menu = builder.build();
    menu.register();
  }

  private List<SlotDefinition> buildSlots(@NonNull Player player, @NonNull MenuSession session) {
    var settings = this.config.value().pickPlayerMenu();
    var rows = MenuLayouts.clampRows(settings.rows());
    var viewerId = player.getUniqueId();

    var slots = new ArrayList<SlotDefinition>();
    slots.add(backSlot(settings, rows));

    var candidates = onlineOthers(viewerId);
    if (candidates.isEmpty()) {
      slots.add(emptySlot(settings, rows));
      return slots;
    }

    for (var candidate : candidates) {
      slots.add(playerSlot(settings, candidate));
    }
    return slots;
  }

  private List<Player> onlineOthers(@NonNull UUID viewerId) {
    var all = this.players.all();
    var result = new ArrayList<Player>(all.size());
    for (var candidate : all) {
      if (candidate.getUniqueId().equals(viewerId)) {
        continue;
      }
      result.add(candidate);
    }
    return result;
  }

  private SlotDefinition playerSlot(
      @NonNull TpaPickPlayerMenuConfig settings, @NonNull Player candidate) {
    var template = playerTemplate(settings, candidate);

    return SlotDefinition.of(-1, template, click -> selectAndSwitch(click, candidate));
  }

  private ItemTemplate playerTemplate(
      @NonNull TpaPickPlayerMenuConfig settings, @NonNull Player candidate) {
    var candidateName = candidate.getName();
    var name = settings.playerName().replace("{player}", candidateName);
    var lore = replacePlayer(settings.playerLore(), candidateName);

    var builder = ItemTemplate.builder(settings.playerIcon());
    applyPlayerHead(builder, settings, candidate.getUniqueId());
    builder.name(name);
    builder.lore(lore.toArray(String[]::new));
    builder.italic(false);

    return builder.build();
  }

  private static void applyPlayerHead(
      @NonNull ItemTemplate.Builder builder,
      @NonNull TpaPickPlayerMenuConfig settings,
      @NonNull UUID candidateId) {
    if (settings.playerIcon() != Material.PLAYER_HEAD) {
      return;
    }
    if (settings.playerUsePlayerHead()) {
      builder.head(candidateId);
      return;
    }
    if (!settings.playerHeadTexture().isBlank()) {
      builder.head(settings.playerHeadTexture());
    }
  }

  private SlotDefinition emptySlot(@NonNull TpaPickPlayerMenuConfig settings, int rows) {
    var slots = contentSlots(settings, rows);
    var center = slots.get(slots.size() / 2);
    var template =
        MenuTemplates.simple(settings.emptyIcon(), settings.emptyName(), settings.emptyLore());

    return SlotDefinition.of(center, template, click -> {});
  }

  private SlotDefinition backSlot(@NonNull TpaPickPlayerMenuConfig settings, int rows) {
    var template =
        MenuTemplates.simple(settings.backIcon(), settings.backName(), settings.backLore());
    var safeSlot = MenuLayouts.sanitizeSlot(settings.backSlot(), rows, 0);

    return SlotDefinition.of(safeSlot, template, click -> click.switchTo(TpaHelpMenu.ID));
  }

  private void selectAndSwitch(@NonNull ClickContext click, @NonNull Player candidate) {
    var viewer = click.player();
    var viewerId = viewer.getUniqueId();

    var selection =
        new TpaTargetSelection(
            candidate.getUniqueId(), candidate.getName(), TeleportRequestType.TPA);
    this.selections.select(viewerId, selection);

    click.switchTo(TpaTargetActionMenu.ID);
  }
}
