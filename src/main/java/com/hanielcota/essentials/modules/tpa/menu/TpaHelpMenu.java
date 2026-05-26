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
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.config.TpaHelpMenuConfig;
import com.hanielcota.essentials.modules.tpa.domain.TpaProfile;
import com.hanielcota.essentials.modules.tpa.service.TeleportRequestService;
import com.hanielcota.essentials.modules.tpa.service.TpaFavoriteService;
import com.hanielcota.essentials.modules.tpa.service.TpaProfileService;
import com.hanielcota.essentials.util.ComponentUtils;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Hub shown when {@code /tpa} is invoked without a target. It renders the viewer profile,
 * request-help, history and settings shortcuts.
 */
@RequiredArgsConstructor
public final class TpaHelpMenu implements EssentialsMenu {

  // Matches the convention shared by every other menu (WhitelistMenu.ID, BackMenu.ID, etc.).
  // The id()/ID lookalike is intentional — keeping the constant uppercase and the accessor
  // lowercase preserves both the field-as-constant convention and the EssentialsMenu interface.
  @SuppressWarnings("java:S1845")
  public static final String ID = "essentials.tpa.help";

  private final ConfigHandle<TpaConfig> config;
  private final TpaProfileService profiles;
  private final TeleportRequestService requests;
  private final TpaFavoriteService favorites;

  static List<Integer> contentSlots(@NonNull TpaHelpMenuConfig helpMenu, int rows) {
    return List.of(
        MenuLayouts.sanitizeSlot(helpMenu.profileSlot(), rows, 0),
        MenuLayouts.sanitizeSlot(helpMenu.tpaSlot(), rows, 0),
        MenuLayouts.sanitizeSlot(helpMenu.pendingSlot(), rows, 0),
        MenuLayouts.sanitizeSlot(helpMenu.historySlot(), rows, 0),
        MenuLayouts.sanitizeSlot(helpMenu.settingsSlot(), rows, 0),
        MenuLayouts.sanitizeSlot(helpMenu.favoritesSlot(), rows, 0));
  }

  private static ItemTemplate template(
      @NonNull Material icon,
      @NonNull String headTexture,
      @NonNull String name,
      @NonNull List<String> lore) {
    var loreArray = lore.toArray(String[]::new);

    var builder = ItemTemplate.builder(icon);
    if (icon == Material.PLAYER_HEAD && !headTexture.isBlank()) {
      builder.head(headTexture);
    }
    builder.name(name);
    builder.lore(loreArray);
    builder.italic(false);

    return builder.build();
  }

  private static List<String> applyProfilePlaceholders(
      @NonNull List<String> lore,
      @NonNull Player player,
      @NonNull TpaProfile profile,
      int pending,
      @NonNull TpaHelpMenuConfig settings) {
    var replaced = new ArrayList<String>(lore.size());

    for (var line : lore) {
      replaced.add(applyProfilePlaceholders(line, player, profile, pending, settings));
    }

    return replaced;
  }

  private static String applyProfilePlaceholders(
      @NonNull String raw,
      @NonNull Player player,
      @NonNull TpaProfile profile,
      int pending,
      @NonNull TpaHelpMenuConfig settings) {
    var playerName = player.getName();
    var sent = Long.toString(profile.sentRequests());
    var received = Long.toString(profile.receivedRequests());
    var pendingRequests = Integer.toString(pending);
    var receiveTpa = profile.receiveTpa() ? settings.enabledLabel() : settings.disabledLabel();
    var receiveTpaHere =
        profile.receiveTpaHere() ? settings.enabledLabel() : settings.disabledLabel();

    return raw.replace("{player}", playerName)
        .replace("{sent}", sent)
        .replace("{received}", received)
        .replace("{pending}", pendingRequests)
        .replace("{receive_tpa}", receiveTpa)
        .replace("{receive_tpahere}", receiveTpaHere);
  }

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void register(@NonNull MenuService menus) {
    var snap = this.config.value();
    var helpMenu = snap.helpMenu();

    var rows = MenuLayouts.clampRows(helpMenu.rows());

    var rawTitle = helpMenu.title();
    var menuTitle = ComponentUtils.mini(rawTitle);

    var builder = MenuFramework.builder(ID, menus);
    builder.rows(rows);
    builder.title(menuTitle);
    builder.pagination(
        PaginationConfig.builder().contentSlots(contentSlots(helpMenu, rows)).build());
    builder.dynamicContent(this::buildSlots);

    var menu = builder.build();
    menu.register();
  }

  private List<SlotDefinition> buildSlots(@NonNull Player player, @NonNull MenuSession session) {
    var snap = this.config.value();
    var helpMenu = snap.helpMenu();
    var rows = MenuLayouts.clampRows(helpMenu.rows());

    var playerId = player.getUniqueId();
    var profile = this.profiles.profile(playerId);
    var pending = this.requests.incoming(playerId).size();
    var favoriteCount = this.favorites.favoritesOf(playerId).size();

    var slots = new ArrayList<SlotDefinition>();
    slots.add(profileSlot(player, profile, pending, helpMenu, rows));
    slots.add(tpaSlot(helpMenu, pending, rows));
    slots.add(pendingSlot(helpMenu, pending, rows));
    slots.add(historySlot(helpMenu, rows));
    slots.add(settingsSlot(helpMenu, rows));
    slots.add(favoritesSlot(helpMenu, favoriteCount, rows));

    return slots;
  }

  private SlotDefinition profileSlot(
      @NonNull Player player,
      @NonNull TpaProfile profile,
      int pending,
      @NonNull TpaHelpMenuConfig helpMenu,
      int rows) {
    var profileName =
        applyProfilePlaceholders(helpMenu.profileName(), player, profile, pending, helpMenu);
    var profileLore =
        applyProfilePlaceholders(helpMenu.profileLore(), player, profile, pending, helpMenu);

    var builder = ItemTemplate.builder(helpMenu.profileIcon());
    if (helpMenu.profileUsePlayerHead()) {
      var playerId = player.getUniqueId();
      builder.head(playerId);
    } else if (helpMenu.profileIcon() == Material.PLAYER_HEAD
        && !helpMenu.profileHeadTexture().isBlank()) {
      builder.head(helpMenu.profileHeadTexture());
    }
    builder.name(profileName);
    builder.lore(profileLore.toArray(String[]::new));
    builder.italic(false);

    var template = builder.build();
    var safeSlot = MenuLayouts.sanitizeSlot(helpMenu.profileSlot(), rows, 0);

    return SlotDefinition.of(safeSlot, template, click -> {});
  }

  private SlotDefinition tpaSlot(@NonNull TpaHelpMenuConfig helpMenu, int pending, int rows) {
    var template =
        template(
            helpMenu.tpaIcon(),
            helpMenu.tpaHeadTexture(),
            helpMenu.tpaName(),
            replacePending(helpMenu.tpaLore(), pending));
    var safeSlot = MenuLayouts.sanitizeSlot(helpMenu.tpaSlot(), rows, 0);

    return SlotDefinition.of(safeSlot, template, click -> {});
  }

  private SlotDefinition pendingSlot(@NonNull TpaHelpMenuConfig helpMenu, int pending, int rows) {
    var name = helpMenu.pendingName().replace("{pending}", Integer.toString(pending));
    var lore = replacePending(helpMenu.pendingLore(), pending);
    var template = template(helpMenu.pendingIcon(), helpMenu.pendingHeadTexture(), name, lore);
    var safeSlot = MenuLayouts.sanitizeSlot(helpMenu.pendingSlot(), rows, 0);

    return SlotDefinition.of(safeSlot, template, this::openPending);
  }

  private static List<String> replacePending(@NonNull List<String> lore, int pending) {
    var replaced = new ArrayList<String>(lore.size());
    var pendingText = Integer.toString(pending);
    for (var line : lore) {
      replaced.add(line.replace("{pending}", pendingText));
    }
    return replaced;
  }

  private SlotDefinition historySlot(@NonNull TpaHelpMenuConfig helpMenu, int rows) {
    var template =
        template(
            helpMenu.historyIcon(),
            helpMenu.historyHeadTexture(),
            helpMenu.historyName(),
            helpMenu.historyLore());
    var safeSlot = MenuLayouts.sanitizeSlot(helpMenu.historySlot(), rows, 0);

    return SlotDefinition.of(safeSlot, template, this::openHistory);
  }

  private SlotDefinition settingsSlot(@NonNull TpaHelpMenuConfig helpMenu, int rows) {
    var template =
        template(
            helpMenu.settingsIcon(),
            helpMenu.settingsHeadTexture(),
            helpMenu.settingsName(),
            helpMenu.settingsLore());
    var safeSlot = MenuLayouts.sanitizeSlot(helpMenu.settingsSlot(), rows, 0);

    return SlotDefinition.of(safeSlot, template, this::openSettings);
  }

  private SlotDefinition favoritesSlot(
      @NonNull TpaHelpMenuConfig helpMenu, int favoriteCount, int rows) {
    var countText = Integer.toString(favoriteCount);
    var name = helpMenu.favoritesName().replace("{favorites}", countText);
    var lore = replaceFavorites(helpMenu.favoritesLore(), favoriteCount);
    var template = template(helpMenu.favoritesIcon(), helpMenu.favoritesHeadTexture(), name, lore);
    var safeSlot = MenuLayouts.sanitizeSlot(helpMenu.favoritesSlot(), rows, 0);

    return SlotDefinition.of(safeSlot, template, this::openFavorites);
  }

  private static List<String> replaceFavorites(@NonNull List<String> lore, int favoriteCount) {
    var replaced = new ArrayList<String>(lore.size());
    var countText = Integer.toString(favoriteCount);
    for (var line : lore) {
      replaced.add(line.replace("{favorites}", countText));
    }
    return replaced;
  }

  private void openHistory(@NonNull ClickContext click) {
    click.switchTo(TpaHistoryMenu.ID);
  }

  private void openPending(@NonNull ClickContext click) {
    click.switchTo(TpaPendingMenu.ID);
  }

  private void openSettings(@NonNull ClickContext click) {
    click.switchTo(TpaSettingsMenu.ID);
  }

  private void openFavorites(@NonNull ClickContext click) {
    click.switchTo(TpaFavoritesMenu.ID);
  }
}
