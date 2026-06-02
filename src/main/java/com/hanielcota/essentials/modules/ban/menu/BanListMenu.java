package com.hanielcota.essentials.modules.ban.menu;

import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.api.MenuSession;
import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.github.hanielcota.menuframework.definition.SlotDefinition;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.EssentialsMenu;
import com.hanielcota.essentials.menu.MenuLayouts;
import com.hanielcota.essentials.menu.MenuTemplates;
import com.hanielcota.essentials.menu.NavigationButtonsConfig;
import com.hanielcota.essentials.menu.PaginatedInfoMenus;
import com.hanielcota.essentials.modules.ban.config.BanConfig;
import com.hanielcota.essentials.modules.ban.config.BanMenuConfig;
import com.hanielcota.essentials.modules.ban.domain.ActiveBan;
import com.hanielcota.essentials.modules.ban.domain.Ban;
import com.hanielcota.essentials.modules.ban.service.BanService;
import com.hanielcota.essentials.shared.DurationFormatter;
import com.hanielcota.essentials.shared.Placeholders;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/** Paginated list of active bans; clicking an entry lifts that ban. */
@RequiredArgsConstructor
public final class BanListMenu implements EssentialsMenu {

  public static final String ID = "essentials.ban.list";

  private final ConfigHandle<BanConfig> config;
  private final BanService service;
  private final BanListClickHandler clicks;

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void register(@NonNull MenuService menus) {
    var snap = this.config.value();
    var menu = snap.menu();
    var rows = MenuLayouts.clampRows(menu.listRows());
    var title = menu.listTitle();

    var lastRow = (rows - 1) * 9;
    var contentSlots = MenuLayouts.fallbackContentSlots(rows, lastRow);
    var navigation = NavigationButtonsConfig.defaults(lastRow + 3, lastRow + 5);
    var infoSlot = lastRow + 8;
    var infoTemplate = MenuTemplates.info(Material.WRITABLE_BOOK, title, List.of());

    PaginatedInfoMenus.register(
        menus, ID, rows, title, contentSlots, navigation, infoSlot, infoTemplate, this::buildSlots);
  }

  private List<SlotDefinition> buildSlots(@NonNull Player viewer, @NonNull MenuSession session) {
    var snap = this.config.value();
    var menu = snap.menu();
    var permanentLabel = snap.permanentLabel();

    var active = sortedActive();

    var slots = new ArrayList<SlotDefinition>(active.size());
    for (var entry : active) {
      var template = entryTemplate(menu, entry, permanentLabel);
      slots.add(SlotDefinition.of(-1, template, click -> this.clicks.unban(click, entry)));
    }

    return slots;
  }

  private List<ActiveBan> sortedActive() {
    var active = new ArrayList<>(this.service.listActive());

    active.sort(Comparator.comparing(ActiveBan::name, String.CASE_INSENSITIVE_ORDER));

    return active;
  }

  private static ItemTemplate entryTemplate(
      @NonNull BanMenuConfig menu, @NonNull ActiveBan entry, @NonNull String permanentLabel) {
    var ban = entry.ban();
    var name = menu.entryName().replace("{player}", entry.name());
    var expires = expiresLabel(ban, permanentLabel);

    var lore = new ArrayList<String>(menu.entryLore().size());
    for (var line : menu.entryLore()) {
      var formatted =
          Placeholders.format(
              line, "reason", ban.reason(), "issuer", ban.issuer(), "expires", expires);
      lore.add(formatted);
    }

    return MenuTemplates.info(Material.PLAYER_HEAD, name, lore);
  }

  private static String expiresLabel(@NonNull Ban ban, @NonNull String permanentLabel) {
    var expiresAt = ban.expiresAt();
    if (expiresAt == null) {
      return permanentLabel;
    }

    var now = Instant.now();
    var remaining = Duration.between(now, expiresAt);

    return DurationFormatter.format(remaining);
  }
}
