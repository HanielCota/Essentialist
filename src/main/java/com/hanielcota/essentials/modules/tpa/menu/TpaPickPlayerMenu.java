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
import com.hanielcota.essentials.modules.tpa.domain.TpaPickPlayerFilter;
import com.hanielcota.essentials.modules.tpa.domain.TpaTargetSelection;
import com.hanielcota.essentials.modules.tpa.menu.presentation.TpaPickPlayerMenuRenderer;
import com.hanielcota.essentials.modules.tpa.service.TpaPickPlayerCandidates;
import com.hanielcota.essentials.modules.tpa.service.TpaPickPlayerFilters;
import com.hanielcota.essentials.modules.tpa.service.TpaTargetSelections;
import com.hanielcota.essentials.shared.ComponentUtils;
import com.hanielcota.essentials.shared.Placeholders;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/**
 * Paginated picker of every online player (except the viewer) opened from the hub's TPA slot.
 * Clicking a head stashes a {@link TpaTargetSelection} with {@code preferredType=TPA} and switches
 * to {@link TpaTargetActionMenu}. The list can be narrowed via a cycle button cycling between
 * {@link TpaPickPlayerFilter} values; the choice resets on inventory close.
 */
@RequiredArgsConstructor
public final class TpaPickPlayerMenu implements EssentialsMenu {

  public static final String ID = "essentials.tpa.pick.player";

  private final ConfigHandle<TpaConfig> config;
  private final TpaTargetSelections selections;
  private final TpaPickPlayerFilters filters;
  private final TpaPickPlayerCandidates candidates;
  private final TpaPickPlayerMenuRenderer renderer = new TpaPickPlayerMenuRenderer();

  static List<Integer> contentSlots(@NonNull TpaPickPlayerMenuConfig settings, int rows) {
    var slotCount = MenuLayouts.slotCount(rows);
    var fallbackWidth = Math.min(7, slotCount);
    var fallback = MenuLayouts.fallbackContentSlots(rows, fallbackWidth);

    return MenuLayouts.sanitizeSlots(settings.contentSlots(), rows, fallback);
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

    builder.buildAndRegister();
  }

  private List<SlotDefinition> buildSlots(@NonNull Player player, @NonNull MenuSession session) {
    var settings = this.config.value().pickPlayerMenu();
    var rows = MenuLayouts.clampRows(settings.rows());
    var viewerId = player.getUniqueId();
    var filter = this.filters.of(viewerId);

    var slots = new ArrayList<SlotDefinition>();
    slots.add(backSlot(settings, rows));
    slots.add(filterSlot(settings, rows, filter));

    var resolved = this.candidates.resolve(player, filter);
    if (resolved.isEmpty()) {
      slots.add(emptySlot(settings, rows));
      return slots;
    }

    for (var candidate : resolved) {
      slots.add(playerSlot(settings, candidate));
    }
    return slots;
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
    var lore = Placeholders.replaceInAll(settings.playerLore(), "{player}", candidateName);

    var builder = ItemTemplate.builder(settings.playerIcon());
    MenuTemplates.applyHead(
        builder,
        settings.playerIcon(),
        settings.playerUsePlayerHead(),
        settings.playerHeadTexture(),
        candidate.getUniqueId());
    builder.name(name);
    builder.lore(lore.toArray(String[]::new));
    builder.italic(false);

    return builder.build();
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

  private SlotDefinition filterSlot(
      @NonNull TpaPickPlayerMenuConfig settings, int rows, @NonNull TpaPickPlayerFilter filter) {
    var template = this.renderer.filterTemplate(settings, filter);
    var safeSlot = MenuLayouts.sanitizeSlot(settings.filterSlot(), rows, 0);

    return SlotDefinition.of(safeSlot, template, this::cycleFilter);
  }

  private void cycleFilter(@NonNull ClickContext click) {
    var viewerId = click.player().getUniqueId();
    this.filters.cycle(viewerId);
    click.refresh();
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
