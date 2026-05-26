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
import com.hanielcota.essentials.modules.tpa.command.TpaProfileStatsFormatter;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.config.TpaHelpMenuConfig;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequest;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequestType;
import com.hanielcota.essentials.modules.tpa.domain.TpaContact;
import com.hanielcota.essentials.modules.tpa.domain.TpaProfile;
import com.hanielcota.essentials.modules.tpa.service.TeleportRequestService;
import com.hanielcota.essentials.modules.tpa.service.TpaContactService;
import com.hanielcota.essentials.modules.tpa.service.TpaDndCycle;
import com.hanielcota.essentials.modules.tpa.service.TpaFavoriteService;
import com.hanielcota.essentials.modules.tpa.service.TpaProfileService;
import com.hanielcota.essentials.util.ComponentUtils;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Hub shown when {@code /tpa} is invoked without a target. It renders the viewer profile,
 * request-help, history, settings, favorites, outgoing-request, DND and last-contacted shortcuts.
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

  static List<Integer> contentSlots(@NonNull TpaHelpMenuConfig helpMenu, int rows) {
    return List.of(
        MenuLayouts.sanitizeSlot(helpMenu.profileSlot(), rows, 0),
        MenuLayouts.sanitizeSlot(helpMenu.tpaSlot(), rows, 0),
        MenuLayouts.sanitizeSlot(helpMenu.pendingSlot(), rows, 0),
        MenuLayouts.sanitizeSlot(helpMenu.historySlot(), rows, 0),
        MenuLayouts.sanitizeSlot(helpMenu.settingsSlot(), rows, 0),
        MenuLayouts.sanitizeSlot(helpMenu.favoritesSlot(), rows, 0),
        MenuLayouts.sanitizeSlot(helpMenu.outgoingSlot(), rows, 0),
        MenuLayouts.sanitizeSlot(helpMenu.dndSlot(), rows, 0),
        MenuLayouts.sanitizeSlot(helpMenu.lastContactedSlot(), rows, 0));
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
      @org.jspecify.annotations.Nullable String mostContacted,
      @NonNull TpaHelpMenuConfig settings) {
    var replaced = new ArrayList<String>(lore.size());

    for (var line : lore) {
      replaced.add(
          applyProfilePlaceholders(line, player, profile, pending, mostContacted, settings));
    }

    return replaced;
  }

  private static String applyProfilePlaceholders(
      @NonNull String raw,
      @NonNull Player player,
      @NonNull TpaProfile profile,
      int pending,
      @org.jspecify.annotations.Nullable String mostContacted,
      @NonNull TpaHelpMenuConfig settings) {
    var playerName = player.getName();
    var sent = Long.toString(profile.sentRequests());
    var received = Long.toString(profile.receivedRequests());
    var pendingRequests = Integer.toString(pending);
    var receiveTpa = profile.receiveTpa() ? settings.enabledLabel() : settings.disabledLabel();
    var receiveTpaHere =
        profile.receiveTpaHere() ? settings.enabledLabel() : settings.disabledLabel();
    var statsFallback = settings.statsFallback();
    var acceptRate = TpaProfileStatsFormatter.acceptRate(profile, statsFallback);
    var avgAccept = TpaProfileStatsFormatter.averageAccept(profile, statsFallback);
    var mostContactedText =
        TpaProfileStatsFormatter.mostContactedName(mostContacted, statsFallback);

    return raw.replace("{player}", playerName)
        .replace("{sent}", sent)
        .replace("{received}", received)
        .replace("{pending}", pendingRequests)
        .replace("{receive_tpa}", receiveTpa)
        .replace("{receive_tpahere}", receiveTpaHere)
        .replace("{accept_rate}", acceptRate)
        .replace("{avg_accept}", avgAccept)
        .replace("{most_contacted}", mostContactedText);
  }

  private static List<String> replacePending(@NonNull List<String> lore, int pending) {
    var replaced = new ArrayList<String>(lore.size());
    var pendingText = Integer.toString(pending);
    for (var line : lore) {
      replaced.add(line.replace("{pending}", pendingText));
    }
    return replaced;
  }

  private static List<String> replaceFavorites(@NonNull List<String> lore, int favoriteCount) {
    var replaced = new ArrayList<String>(lore.size());
    var countText = Integer.toString(favoriteCount);
    for (var line : lore) {
      replaced.add(line.replace("{favorites}", countText));
    }
    return replaced;
  }

  private static ItemTemplate idleOutgoingTemplate(@NonNull TpaHelpMenuConfig helpMenu) {
    var builder = ItemTemplate.builder(helpMenu.outgoingIdleIcon());
    builder.name(helpMenu.outgoingIdleName());
    builder.lore(helpMenu.outgoingIdleLore().toArray(String[]::new));
    builder.italic(false);
    return builder.build();
  }

  private static ItemTemplate activeOutgoingTemplate(
      @NonNull TpaHelpMenuConfig helpMenu, @NonNull TeleportRequest request) {
    var targetName = request.target().name();
    var typeLabel =
        request.type() == TeleportRequestType.TPA
            ? helpMenu.outgoingTypeTpa()
            : helpMenu.outgoingTypeTpaHere();
    var seconds = Long.toString(secondsLeft(request));

    var name =
        helpMenu
            .outgoingName()
            .replace("{target}", targetName)
            .replace("{type}", typeLabel)
            .replace("{seconds}", seconds);
    var lore =
        helpMenu.outgoingLore().stream()
            .map(line -> line.replace("{target}", targetName))
            .map(line -> line.replace("{type}", typeLabel))
            .map(line -> line.replace("{seconds}", seconds))
            .toList();

    var builder = ItemTemplate.builder(helpMenu.outgoingIcon());
    if (helpMenu.outgoingIcon() == Material.PLAYER_HEAD) {
      if (helpMenu.outgoingUsePlayerHead()) {
        builder.head(request.target().id());
      } else if (!helpMenu.outgoingHeadTexture().isBlank()) {
        builder.head(helpMenu.outgoingHeadTexture());
      }
    }
    builder.name(name);
    builder.lore(lore.toArray(String[]::new));
    builder.italic(false);

    return builder.build();
  }

  private static long secondsLeft(@NonNull TeleportRequest request) {
    var now = Instant.now();
    var remaining = Duration.between(now, request.window().expiresAt()).toSeconds();

    return Math.max(0, remaining);
  }

  private static String stageLabel(
      @NonNull TpaHelpMenuConfig helpMenu, @NonNull TpaDndCycle.Stage stage) {
    return switch (stage) {
      case OFF -> helpMenu.dndStateOff();
      case THIRTY_MINUTES -> helpMenu.dndState30m();
      case ONE_HOUR -> helpMenu.dndState1h();
      case FOUR_HOURS -> helpMenu.dndState4h();
    };
  }

  private static String stageRemaining(
      @NonNull TpaHelpMenuConfig helpMenu, @NonNull TpaProfile profile, long now) {
    var until = profile.dndUntilEpochMs();
    if (until <= now) {
      return helpMenu.statsFallback();
    }
    var remaining = Duration.ofMillis(until - now);
    var totalMinutes = remaining.toMinutes();
    if (totalMinutes < 1) {
      return "<1m";
    }
    if (totalMinutes < 60) {
      return totalMinutes + "m";
    }
    var hours = totalMinutes / 60;
    var mins = totalMinutes % 60;
    if (mins == 0) {
      return hours + "h";
    }
    return hours + "h" + mins + "m";
  }

  private static ItemTemplate lastContactedTemplate(
      @NonNull TpaHelpMenuConfig helpMenu, @NonNull TpaContact contact) {
    var name = helpMenu.lastContactedName().replace("{player}", contact.targetName());
    var lore =
        helpMenu.lastContactedLore().stream()
            .map(line -> line.replace("{player}", contact.targetName()))
            .toList();

    var builder = ItemTemplate.builder(helpMenu.lastContactedIcon());
    if (helpMenu.lastContactedIcon() == Material.PLAYER_HEAD) {
      if (helpMenu.lastContactedUsePlayerHead()) {
        builder.head(contact.targetId());
      } else if (!helpMenu.lastContactedHeadTexture().isBlank()) {
        builder.head(helpMenu.lastContactedHeadTexture());
      }
    }
    builder.name(name);
    builder.lore(lore.toArray(String[]::new));
    builder.italic(false);

    return builder.build();
  }

  private static ItemTemplate simpleTemplate(
      @NonNull Material icon, @NonNull String name, @NonNull List<String> lore) {
    var builder = ItemTemplate.builder(icon);
    builder.name(name);
    builder.lore(lore.toArray(String[]::new));
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
    var mostContacted =
        this.contacts.mostContacted(playerId).map(TpaContact::targetName).orElse(null);
    var outgoing = this.requests.outgoing(playerId);
    var lastContacted = this.contacts.lastContacted(playerId);

    var slots = new ArrayList<SlotDefinition>();
    slots.add(profileSlot(player, profile, pending, mostContacted, helpMenu, rows));
    slots.add(tpaSlot(helpMenu, pending, rows));
    slots.add(pendingSlot(helpMenu, pending, rows));
    slots.add(historySlot(helpMenu, rows));
    slots.add(settingsSlot(helpMenu, rows));
    slots.add(favoritesSlot(helpMenu, favoriteCount, rows));
    slots.add(outgoingSlot(helpMenu, outgoing, rows));
    slots.add(dndSlot(helpMenu, profile, rows));
    slots.add(lastContactedSlot(helpMenu, lastContacted, rows));

    return slots;
  }

  private SlotDefinition profileSlot(
      @NonNull Player player,
      @NonNull TpaProfile profile,
      int pending,
      @org.jspecify.annotations.Nullable String mostContacted,
      @NonNull TpaHelpMenuConfig helpMenu,
      int rows) {
    var profileName =
        applyProfilePlaceholders(
            helpMenu.profileName(), player, profile, pending, mostContacted, helpMenu);
    var profileLore =
        applyProfilePlaceholders(
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

  private SlotDefinition outgoingSlot(
      @NonNull TpaHelpMenuConfig helpMenu, @NonNull Optional<TeleportRequest> outgoing, int rows) {
    var safeSlot = MenuLayouts.sanitizeSlot(helpMenu.outgoingSlot(), rows, 0);

    if (outgoing.isEmpty()) {
      var idleTemplate = idleOutgoingTemplate(helpMenu);
      return SlotDefinition.of(safeSlot, idleTemplate, click -> {});
    }

    var request = outgoing.get();
    var activeTemplate = activeOutgoingTemplate(helpMenu, request);

    return SlotDefinition.of(
        safeSlot, activeTemplate, click -> this.clicks.cancelOutgoing(click, request));
  }

  private SlotDefinition dndSlot(
      @NonNull TpaHelpMenuConfig helpMenu, @NonNull TpaProfile profile, int rows) {
    var now = System.currentTimeMillis();
    var stage = TpaDndCycle.stageOf(profile.dndUntilEpochMs(), now);
    var stateLabel = stageLabel(helpMenu, stage);
    var remainingLabel = stageRemaining(helpMenu, profile, now);

    var name =
        helpMenu.dndName().replace("{state}", stateLabel).replace("{remaining}", remainingLabel);
    var lore =
        helpMenu.dndLore().stream()
            .map(line -> line.replace("{state}", stateLabel))
            .map(line -> line.replace("{remaining}", remainingLabel))
            .toList();
    var icon = stage == TpaDndCycle.Stage.OFF ? helpMenu.dndOffIcon() : helpMenu.dndOnIcon();
    var template = simpleTemplate(icon, name, lore);
    var safeSlot = MenuLayouts.sanitizeSlot(helpMenu.dndSlot(), rows, 0);

    return SlotDefinition.of(safeSlot, template, this.clicks::cycleDnd);
  }

  private SlotDefinition lastContactedSlot(
      @NonNull TpaHelpMenuConfig helpMenu, @NonNull Optional<TpaContact> lastContacted, int rows) {
    var safeSlot = MenuLayouts.sanitizeSlot(helpMenu.lastContactedSlot(), rows, 0);

    if (lastContacted.isEmpty()) {
      var emptyTemplate =
          simpleTemplate(
              helpMenu.lastContactedEmptyIcon(),
              helpMenu.lastContactedEmptyName(),
              helpMenu.lastContactedEmptyLore());
      return SlotDefinition.of(safeSlot, emptyTemplate, click -> {});
    }

    var contact = lastContacted.get();
    var template = lastContactedTemplate(helpMenu, contact);

    return SlotDefinition.of(
        safeSlot, template, click -> this.clicks.repeatLastContacted(click, contact));
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
