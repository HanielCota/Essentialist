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
import com.hanielcota.essentials.modules.tpa.config.menu.TpaHelpMenuConfig;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequest;
import com.hanielcota.essentials.modules.tpa.domain.TpaContact;
import com.hanielcota.essentials.modules.tpa.domain.TpaProfile;
import com.hanielcota.essentials.modules.tpa.menu.presentation.TpaHelpMenuRenderer;
import com.hanielcota.essentials.modules.tpa.service.TeleportRequestService;
import com.hanielcota.essentials.modules.tpa.service.TpaContactService;
import com.hanielcota.essentials.modules.tpa.service.TpaFavoriteService;
import com.hanielcota.essentials.modules.tpa.service.TpaProfileService;
import com.hanielcota.essentials.shared.ComponentUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

/**
 * Hub shown when {@code /tpa} is invoked without a target. It renders the viewer profile,
 * request-help, history, settings, favorites and outgoing-request shortcuts. DND lives in the
 * settings sub-menu.
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
  private final TpaContactService contacts;
  private final TpaHubClickHandler clicks;
  private final TpaHelpMenuRenderer renderer = new TpaHelpMenuRenderer();

  static List<Integer> contentSlots(@NonNull TpaHelpMenuConfig helpMenu, int rows) {
    return List.of(
        MenuLayouts.sanitizeSlot(helpMenu.profileSlot(), rows, 0),
        MenuLayouts.sanitizeSlot(helpMenu.tpaSlot(), rows, 0),
        MenuLayouts.sanitizeSlot(helpMenu.pendingSlot(), rows, 0),
        MenuLayouts.sanitizeSlot(helpMenu.historySlot(), rows, 0),
        MenuLayouts.sanitizeSlot(helpMenu.settingsSlot(), rows, 0),
        MenuLayouts.sanitizeSlot(helpMenu.favoritesSlot(), rows, 0),
        MenuLayouts.sanitizeSlot(helpMenu.outgoingSlot(), rows, 0));
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
    var pagination = PaginationConfig.builder().contentSlots(contentSlots(helpMenu, rows)).build();

    var builder = MenuFramework.builder(ID, menus);
    builder.rows(rows);
    builder.title(menuTitle);
    builder.pagination(pagination);
    builder.dynamicContent(this::buildSlots);

    builder.buildAndRegister();
  }

  private List<SlotDefinition> buildSlots(@NonNull Player player, @NonNull MenuSession session) {
    var snap = this.config.value();
    var helpMenu = snap.helpMenu();
    var rows = MenuLayouts.clampRows(helpMenu.rows());

    var playerId = player.getUniqueId();
    var profile = this.profiles.profile(playerId);
    var pending = this.requests.incoming(playerId).size();
    var favoriteCount = this.favorites.favoritesOf(playerId).size();
    var mostContacted =
        this.contacts.mostContacted(playerId).map(TpaContact::targetName).orElse(null);
    var outgoing = this.requests.outgoing(playerId);

    var slots = new ArrayList<SlotDefinition>();
    slots.add(profileSlot(player, profile, pending, mostContacted, helpMenu, rows));
    slots.add(tpaSlot(helpMenu, pending, rows));
    slots.add(pendingSlot(helpMenu, pending, rows));
    slots.add(historySlot(helpMenu, rows));
    slots.add(settingsSlot(helpMenu, rows));
    slots.add(favoritesSlot(helpMenu, favoriteCount, rows));
    slots.add(outgoingSlot(helpMenu, outgoing, rows));

    return slots;
  }

  private SlotDefinition profileSlot(
      @NonNull Player player,
      @NonNull TpaProfile profile,
      int pending,
      @Nullable String mostContacted,
      @NonNull TpaHelpMenuConfig helpMenu,
      int rows) {
    var profileName =
        this.renderer.applyProfilePlaceholders(
            helpMenu.profileName(), player, profile, pending, mostContacted, helpMenu);
    var profileLore =
        this.renderer.applyProfilePlaceholders(
            helpMenu.profileLore(), player, profile, pending, mostContacted, helpMenu);

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

    return SlotDefinition.of(safeSlot, template, click -> click.switchTo(TpaProfileMenu.ID));
  }

  private SlotDefinition tpaSlot(@NonNull TpaHelpMenuConfig helpMenu, int pending, int rows) {
    var template =
        this.renderer.template(
            helpMenu.tpaIcon(),
            helpMenu.tpaHeadTexture(),
            helpMenu.tpaName(),
            this.renderer.replacePending(helpMenu.tpaLore(), pending));
    var safeSlot = MenuLayouts.sanitizeSlot(helpMenu.tpaSlot(), rows, 0);

    return SlotDefinition.of(safeSlot, template, click -> click.switchTo(TpaPickPlayerMenu.ID));
  }

  private SlotDefinition pendingSlot(@NonNull TpaHelpMenuConfig helpMenu, int pending, int rows) {
    var name = helpMenu.pendingName().replace("{pending}", Integer.toString(pending));
    var lore = this.renderer.replacePending(helpMenu.pendingLore(), pending);
    var template =
        this.renderer.template(helpMenu.pendingIcon(), helpMenu.pendingHeadTexture(), name, lore);
    var safeSlot = MenuLayouts.sanitizeSlot(helpMenu.pendingSlot(), rows, 0);

    return SlotDefinition.of(safeSlot, template, this::openPending);
  }

  private SlotDefinition historySlot(@NonNull TpaHelpMenuConfig helpMenu, int rows) {
    var template =
        this.renderer.template(
            helpMenu.historyIcon(),
            helpMenu.historyHeadTexture(),
            helpMenu.historyName(),
            helpMenu.historyLore());
    var safeSlot = MenuLayouts.sanitizeSlot(helpMenu.historySlot(), rows, 0);

    return SlotDefinition.of(safeSlot, template, this::openHistory);
  }

  private SlotDefinition settingsSlot(@NonNull TpaHelpMenuConfig helpMenu, int rows) {
    var template =
        this.renderer.template(
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
    var lore = this.renderer.replaceFavorites(helpMenu.favoritesLore(), favoriteCount);
    var template =
        this.renderer.template(
            helpMenu.favoritesIcon(), helpMenu.favoritesHeadTexture(), name, lore);
    var safeSlot = MenuLayouts.sanitizeSlot(helpMenu.favoritesSlot(), rows, 0);

    return SlotDefinition.of(safeSlot, template, this::openFavorites);
  }

  private SlotDefinition outgoingSlot(
      @NonNull TpaHelpMenuConfig helpMenu, @NonNull Optional<TeleportRequest> outgoing, int rows) {
    var safeSlot = MenuLayouts.sanitizeSlot(helpMenu.outgoingSlot(), rows, 0);

    if (outgoing.isEmpty()) {
      var idleTemplate = this.renderer.idleOutgoingTemplate(helpMenu);
      return SlotDefinition.of(safeSlot, idleTemplate, click -> {});
    }

    var request = outgoing.get();
    var activeTemplate = this.renderer.activeOutgoingTemplate(helpMenu, request);

    return SlotDefinition.of(
        safeSlot, activeTemplate, click -> this.clicks.cancelOutgoing(click, request));
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
