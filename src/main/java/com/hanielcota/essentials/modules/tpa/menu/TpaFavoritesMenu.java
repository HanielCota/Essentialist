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
import com.hanielcota.essentials.modules.tpa.command.TpaFavoritePromptOrchestrator;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.config.TpaFavoritesMenuConfig;
import com.hanielcota.essentials.modules.tpa.domain.FavoriteOrdering;
import com.hanielcota.essentials.modules.tpa.domain.TpaContact;
import com.hanielcota.essentials.modules.tpa.domain.TpaFavorite;
import com.hanielcota.essentials.modules.tpa.service.TpaContactService;
import com.hanielcota.essentials.modules.tpa.service.TpaFavoriteSelections;
import com.hanielcota.essentials.modules.tpa.service.TpaFavoriteService;
import com.hanielcota.essentials.modules.tpa.service.TpaProfileService;
import com.hanielcota.essentials.paper.PlayerProvider;
import com.hanielcota.essentials.util.ComponentUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class TpaFavoritesMenu implements EssentialsMenu {

  public static final String ID = "essentials.tpa.favorites";

  private final ConfigHandle<TpaConfig> config;
  private final TpaFavoriteService favorites;
  private final TpaContactService contacts;
  private final TpaProfileService profiles;
  private final TpaFavoriteSelections selections;
  private final TpaFavoritePromptOrchestrator prompt;
  private final PlayerProvider players;

  static List<Integer> contentSlots(@NonNull TpaFavoritesMenuConfig settings, int rows) {
    var fallback = MenuLayouts.fallbackContentSlots(rows, Math.min(7, MenuLayouts.slotCount(rows)));

    return MenuLayouts.sanitizeSlots(settings.contentSlots(), rows, fallback);
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

  private static ItemTemplate emptyTemplate(@NonNull TpaFavoritesMenuConfig settings) {
    var builder = ItemTemplate.builder(settings.emptyIcon());
    builder.name(settings.emptyName());
    builder.lore(settings.emptyLore().toArray(String[]::new));
    builder.italic(false);

    return builder.build();
  }

  private static int addSlot(@NonNull TpaFavoritesMenuConfig settings, int rows) {
    return MenuLayouts.sanitizeSlot(settings.addSlot(), rows, 0);
  }

  private static ItemTemplate addTemplate(@NonNull TpaFavoritesMenuConfig settings) {
    var builder = ItemTemplate.builder(settings.addIcon());
    if (settings.addIcon() == Material.PLAYER_HEAD && !settings.addHeadTexture().isBlank()) {
      builder.head(settings.addHeadTexture());
    }
    builder.name(settings.addName());
    builder.lore(settings.addLore().toArray(String[]::new));
    builder.italic(false);

    return builder.build();
  }

  private static int backSlot(@NonNull TpaFavoritesMenuConfig settings, int rows) {
    return MenuLayouts.sanitizeSlot(settings.backSlot(), rows, 0);
  }

  private static ItemTemplate backTemplate(@NonNull TpaFavoritesMenuConfig settings) {
    var builder = ItemTemplate.builder(settings.backIcon());
    builder.name(settings.backName());
    builder.lore(settings.backLore().toArray(String[]::new));
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
    var settings = this.config.value().favoritesMenu();
    var rows = MenuLayouts.clampRows(settings.rows());
    var pagination = PaginationConfig.builder().contentSlots(contentSlots(settings, rows)).build();

    var builder = MenuFramework.builder(ID, menus);
    builder.rows(rows);
    builder.title(ComponentUtils.mini(settings.title()));
    builder.pagination(pagination);
    builder.dynamicContent(this::buildSlots);
    builder.slot(addSlot(settings, rows), addTemplate(settings), this::onAddClicked);
    builder.slot(
        backSlot(settings, rows), backTemplate(settings), click -> click.switchTo(TpaHelpMenu.ID));

    var menu = builder.build();
    menu.register();
  }

  private List<SlotDefinition> buildSlots(@NonNull Player player, @NonNull MenuSession session) {
    var viewerId = player.getUniqueId();
    var ordering = this.profiles.profile(viewerId).favoriteOrdering();
    var entries = sortedFavorites(viewerId, ordering);
    var contentSize =
        contentSlots(this.config.value().favoritesMenu(), rows()).size()
            * Math.max(1, session.totalPages());

    var slots = new ArrayList<SlotDefinition>();
    slots.add(orderingSlot(ordering));

    if (entries.isEmpty() && currentSuggestions(viewerId).isEmpty()) {
      slots.addAll(emptySlot());
      return slots;
    }

    for (var entry : entries) {
      slots.add(favoriteSlot(entry));
    }

    var room = Math.max(0, contentSize - slots.size());
    var suggestions = pickSuggestions(viewerId, entries, room);
    for (var suggestion : suggestions) {
      slots.add(suggestionSlot(suggestion));
    }

    return slots;
  }

  private int rows() {
    var settings = this.config.value().favoritesMenu();
    return MenuLayouts.clampRows(settings.rows());
  }

  private List<TpaFavorite> sortedFavorites(
      @NonNull UUID viewerId, @NonNull FavoriteOrdering ordering) {
    var entries = this.favorites.favoritesOf(viewerId);
    return switch (ordering) {
      case NAME -> entries;
      case RECENT -> sortByRecent(viewerId, entries);
      case ONLINE_FIRST -> sortByOnlineFirst(entries);
    };
  }

  private List<TpaFavorite> sortByRecent(
      @NonNull UUID viewerId, @NonNull List<TpaFavorite> entries) {
    var ownerContacts = this.contacts.top(viewerId, Integer.MAX_VALUE);
    var lastUsedByTarget = new java.util.HashMap<UUID, Long>(ownerContacts.size());
    for (var contact : ownerContacts) {
      lastUsedByTarget.put(contact.targetId(), contact.lastUsedAtEpochMs());
    }

    var copy = new ArrayList<>(entries);
    copy.sort(
        Comparator.<TpaFavorite, Long>comparing(
                fav -> lastUsedByTarget.getOrDefault(fav.favoriteId(), 0L))
            .reversed()
            .thenComparing(TpaFavorite::favoriteName, String.CASE_INSENSITIVE_ORDER));
    return copy;
  }

  private List<TpaFavorite> sortByOnlineFirst(@NonNull List<TpaFavorite> entries) {
    var copy = new ArrayList<>(entries);
    copy.sort(
        Comparator.<TpaFavorite, Boolean>comparing(
                fav -> this.players.online(fav.favoriteId()).isPresent())
            .reversed()
            .thenComparing(TpaFavorite::favoriteName, String.CASE_INSENSITIVE_ORDER));
    return copy;
  }

  private List<TpaContact> currentSuggestions(@NonNull UUID viewerId) {
    return pickSuggestions(viewerId, this.favorites.favoritesOf(viewerId), Integer.MAX_VALUE);
  }

  private List<TpaContact> pickSuggestions(
      @NonNull UUID viewerId, @NonNull List<TpaFavorite> currentFavorites, int room) {
    var settings = this.config.value().favoritesMenu();
    var max = Math.max(0, settings.maxSuggestions());
    if (room <= 0 || max <= 0) {
      return List.of();
    }

    var favoriteIds = new HashSet<UUID>(currentFavorites.size());
    for (var favorite : currentFavorites) {
      favoriteIds.add(favorite.favoriteId());
    }

    return pickFromContacts(viewerId, favoriteIds, Math.min(room, max));
  }

  private List<TpaContact> pickFromContacts(
      @NonNull UUID viewerId, @NonNull Set<UUID> excluded, int limit) {
    var contactsList = this.contacts.top(viewerId, Integer.MAX_VALUE);
    var picked = new ArrayList<TpaContact>(limit);
    for (var contact : contactsList) {
      if (excluded.contains(contact.targetId())) {
        continue;
      }
      picked.add(contact);
      if (picked.size() >= limit) {
        break;
      }
    }
    return picked;
  }

  private SlotDefinition favoriteSlot(@NonNull TpaFavorite entry) {
    var template = favoriteTemplate(entry);

    return SlotDefinition.of(-1, template, click -> onFavoriteClicked(click, entry));
  }

  private SlotDefinition suggestionSlot(@NonNull TpaContact contact) {
    var template = suggestionTemplate(contact);

    return SlotDefinition.of(-1, template, click -> onSuggestionClicked(click, contact));
  }

  private SlotDefinition orderingSlot(@NonNull FavoriteOrdering ordering) {
    var settings = this.config.value().favoritesMenu();
    var rows = MenuLayouts.clampRows(settings.rows());
    var stateLabel = orderingStateLabel(settings, ordering);
    var name = settings.orderingName().replace("{state}", stateLabel);
    var lore = renderOrderingLore(settings, stateLabel, ordering);
    var template = simpleTemplate(settings.orderingIcon(), name, lore);
    var safeSlot = MenuLayouts.sanitizeSlot(settings.orderingSlot(), rows, 0);

    return SlotDefinition.of(safeSlot, template, this::onOrderingClicked);
  }

  private void onFavoriteClicked(@NonNull ClickContext click, @NonNull TpaFavorite entry) {
    var viewerId = click.player().getUniqueId();
    this.selections.select(viewerId, entry);

    click.switchTo(TpaFavoriteActionMenu.ID);
  }

  private void onSuggestionClicked(@NonNull ClickContext click, @NonNull TpaContact contact) {
    var viewerId = click.player().getUniqueId();
    this.favorites.add(viewerId, contact.targetId(), contact.targetName());

    var newFavorite = new TpaFavorite(viewerId, contact.targetId(), contact.targetName());
    this.selections.select(viewerId, newFavorite);

    click.switchTo(TpaFavoriteActionMenu.ID);
  }

  private void onAddClicked(@NonNull ClickContext click) {
    var player = click.player();

    click.close();
    this.prompt.prompt(player);
  }

  private void onOrderingClicked(@NonNull ClickContext click) {
    var viewerId = click.player().getUniqueId();
    this.profiles.cycleFavoriteOrdering(viewerId);
    click.refresh();
  }

  private List<SlotDefinition> emptySlot() {
    var settings = this.config.value().favoritesMenu();
    var rows = MenuLayouts.clampRows(settings.rows());
    var slots = contentSlots(settings, rows);
    var center = slots.get(slots.size() / 2);

    return List.of(SlotDefinition.of(center, emptyTemplate(settings), click -> {}));
  }

  private ItemTemplate favoriteTemplate(@NonNull TpaFavorite entry) {
    var settings = this.config.value().favoritesMenu();
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

  private ItemTemplate suggestionTemplate(@NonNull TpaContact contact) {
    var settings = this.config.value().favoritesMenu();
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
}
