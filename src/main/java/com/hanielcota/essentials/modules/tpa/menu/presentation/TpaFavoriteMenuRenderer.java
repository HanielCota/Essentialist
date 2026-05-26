package com.hanielcota.essentials.modules.tpa.menu.presentation;

import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.hanielcota.essentials.menu.MenuLayouts;
import com.hanielcota.essentials.menu.MenuTemplates;
import com.hanielcota.essentials.modules.tpa.config.menu.TpaFavoritesMenuConfig;
import com.hanielcota.essentials.modules.tpa.domain.FavoriteOrdering;
import com.hanielcota.essentials.modules.tpa.domain.TpaContact;
import com.hanielcota.essentials.modules.tpa.domain.TpaFavorite;
import com.hanielcota.essentials.paper.PlayerProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;

@RequiredArgsConstructor
public final class TpaFavoriteMenuRenderer {

  private final @NonNull PlayerProvider players;

  public int addSlot(@NonNull TpaFavoritesMenuConfig settings, int rows) {
    return MenuLayouts.sanitizeSlot(settings.addSlot(), rows, 0);
  }

  public ItemTemplate addTemplate(@NonNull TpaFavoritesMenuConfig settings) {
    var builder = ItemTemplate.builder(settings.addIcon());
    if (settings.addIcon() == Material.PLAYER_HEAD && !settings.addHeadTexture().isBlank()) {
      builder.head(settings.addHeadTexture());
    }
    builder.name(settings.addName());
    builder.lore(settings.addLore().toArray(String[]::new));
    builder.italic(false);

    return builder.build();
  }

  public int backSlot(@NonNull TpaFavoritesMenuConfig settings, int rows) {
    return MenuLayouts.sanitizeSlot(settings.backSlot(), rows, 0);
  }

  public ItemTemplate backTemplate(@NonNull TpaFavoritesMenuConfig settings) {
    return MenuTemplates.simple(settings.backIcon(), settings.backName(), settings.backLore());
  }

  public ItemTemplate emptyTemplate(@NonNull TpaFavoritesMenuConfig settings) {
    return MenuTemplates.simple(settings.emptyIcon(), settings.emptyName(), settings.emptyLore());
  }

  public ItemTemplate orderingTemplate(
      @NonNull TpaFavoritesMenuConfig settings, @NonNull FavoriteOrdering ordering) {
    var stateLabel = orderingStateLabel(settings, ordering);
    var name = settings.orderingName().replace("{state}", stateLabel);
    var lore = renderOrderingLore(settings, stateLabel, ordering);

    return MenuTemplates.simple(settings.orderingIcon(), name, lore);
  }

  public ItemTemplate favoriteTemplate(
      @NonNull TpaFavoritesMenuConfig settings, @NonNull TpaFavorite entry) {
    var statusLabel = statusLabel(entry.favoriteId(), settings);
    var name =
        settings
            .favoriteName()
            .replace("{player}", entry.favoriteName())
            .replace("{status}", statusLabel);
    var lore =
        applyFavoritePlaceholders(settings.favoriteLore(), entry.favoriteName(), statusLabel);

    var builder = ItemTemplate.builder(settings.favoriteIcon());
    applyFavoriteHead(builder, settings, entry);
    builder.name(name);
    builder.lore(lore.toArray(String[]::new));
    builder.italic(false);

    return builder.build();
  }

  public ItemTemplate suggestionTemplate(
      @NonNull TpaFavoritesMenuConfig settings, @NonNull TpaContact contact) {
    var statusLabel = statusLabel(contact.targetId(), settings);
    var count = Long.toString(contact.count());
    var name =
        settings
            .suggestionName()
            .replace("{player}", contact.targetName())
            .replace("{status}", statusLabel)
            .replace("{count}", count);
    var lore =
        applySuggestionPlaceholders(
            settings.suggestionLore(), contact.targetName(), statusLabel, count);

    var builder = ItemTemplate.builder(settings.suggestionIcon());
    applySuggestionHead(builder, settings, contact);
    builder.name(name);
    builder.lore(lore.toArray(String[]::new));
    builder.italic(false);

    return builder.build();
  }

  private String statusLabel(@NonNull UUID targetId, @NonNull TpaFavoritesMenuConfig settings) {
    var online = this.players.online(targetId).isPresent();
    return online ? settings.statusOnline() : settings.statusOffline();
  }

  private static String orderingStateLabel(
      @NonNull TpaFavoritesMenuConfig settings, @NonNull FavoriteOrdering ordering) {
    return switch (ordering) {
      case NAME -> settings.orderingStateName();
      case RECENT -> settings.orderingStateRecent();
      case ONLINE_FIRST -> settings.orderingStateOnlineFirst();
    };
  }

  private static List<String> renderOrderingLore(
      @NonNull TpaFavoritesMenuConfig settings,
      @NonNull String stateLabel,
      @NonNull FavoriteOrdering ordering) {
    var lines = new ArrayList<String>(settings.orderingLore().size() + 2);
    for (var template : settings.orderingLore()) {
      if (template.contains("{options}")) {
        lines.addAll(orderingOptions(settings, ordering));
        continue;
      }
      lines.add(template.replace("{state}", stateLabel));
    }
    return lines;
  }

  private static List<String> orderingOptions(
      @NonNull TpaFavoritesMenuConfig settings, @NonNull FavoriteOrdering current) {
    var marker = settings.orderingActiveMarker();
    return List.of(
        markActive(settings.orderingStateName(), marker, current == FavoriteOrdering.NAME),
        markActive(settings.orderingStateRecent(), marker, current == FavoriteOrdering.RECENT),
        markActive(
            settings.orderingStateOnlineFirst(), marker, current == FavoriteOrdering.ONLINE_FIRST));
  }

  private static String markActive(@NonNull String label, @NonNull String marker, boolean active) {
    return active ? label + marker : label;
  }

  private static void applyFavoriteHead(
      @NonNull ItemTemplate.Builder builder,
      @NonNull TpaFavoritesMenuConfig settings,
      @NonNull TpaFavorite entry) {
    if (settings.favoriteIcon() != Material.PLAYER_HEAD) {
      return;
    }
    if (settings.favoriteUsePlayerHead()) {
      builder.head(entry.favoriteId());
      return;
    }
    if (!settings.favoriteHeadTexture().isBlank()) {
      builder.head(settings.favoriteHeadTexture());
    }
  }

  private static void applySuggestionHead(
      @NonNull ItemTemplate.Builder builder,
      @NonNull TpaFavoritesMenuConfig settings,
      @NonNull TpaContact contact) {
    if (settings.suggestionIcon() != Material.PLAYER_HEAD) {
      return;
    }
    if (settings.suggestionUsePlayerHead()) {
      builder.head(contact.targetId());
      return;
    }
    if (!settings.suggestionHeadTexture().isBlank()) {
      builder.head(settings.suggestionHeadTexture());
    }
  }

  private static List<String> applyFavoritePlaceholders(
      @NonNull List<String> lines, @NonNull String player, @NonNull String status) {
    var replaced = new ArrayList<String>(lines.size());
    for (var line : lines) {
      var withPlayer = line.replace("{player}", player);
      var withStatus = withPlayer.replace("{status}", status);
      replaced.add(withStatus);
    }
    return replaced;
  }

  private static List<String> applySuggestionPlaceholders(
      @NonNull List<String> lines,
      @NonNull String player,
      @NonNull String status,
      @NonNull String count) {
    var replaced = new ArrayList<String>(lines.size());
    for (var line : lines) {
      var withPlayer = line.replace("{player}", player);
      var withStatus = withPlayer.replace("{status}", status);
      var withCount = withStatus.replace("{count}", count);
      replaced.add(withCount);
    }
    return replaced;
  }
}
