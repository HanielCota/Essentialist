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
import com.hanielcota.essentials.modules.tpa.domain.TpaFavorite;
import com.hanielcota.essentials.modules.tpa.service.TpaFavoriteSelections;
import com.hanielcota.essentials.modules.tpa.service.TpaFavoriteService;
import com.hanielcota.essentials.util.ComponentUtils;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class TpaFavoritesMenu implements EssentialsMenu {

  public static final String ID = "essentials.tpa.favorites";

  private final ConfigHandle<TpaConfig> config;
  private final TpaFavoriteService favorites;
  private final TpaFavoriteSelections selections;
  private final TpaFavoritePromptOrchestrator prompt;

  static List<Integer> contentSlots(@NonNull TpaFavoritesMenuConfig settings, int rows) {
    var fallback = MenuLayouts.fallbackContentSlots(rows, Math.min(7, MenuLayouts.slotCount(rows)));

    return MenuLayouts.sanitizeSlots(settings.contentSlots(), rows, fallback);
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
    var entries = this.favorites.favoritesOf(player.getUniqueId());
    if (entries.isEmpty()) {
      return emptySlot();
    }

    var slots = new ArrayList<SlotDefinition>(entries.size());
    for (var entry : entries) {
      slots.add(favoriteSlot(entry));
    }
    return slots;
  }

  private SlotDefinition favoriteSlot(@NonNull TpaFavorite entry) {
    var template = favoriteTemplate(entry);

    return SlotDefinition.of(-1, template, click -> onFavoriteClicked(click, entry));
  }

  private void onFavoriteClicked(@NonNull ClickContext click, @NonNull TpaFavorite entry) {
    var viewerId = click.player().getUniqueId();
    this.selections.select(viewerId, entry);

    click.switchTo(TpaFavoriteActionMenu.ID);
  }

  private void onAddClicked(@NonNull ClickContext click) {
    var player = click.player();

    click.close();
    this.prompt.prompt(player);
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
    var name = settings.favoriteName().replace("{player}", entry.favoriteName());
    var lore = replacePlayer(settings.favoriteLore(), entry.favoriteName());

    var builder = ItemTemplate.builder(settings.favoriteIcon());
    applyFavoriteHead(builder, settings, entry);
    builder.name(name);
    builder.lore(lore.toArray(String[]::new));
    builder.italic(false);

    return builder.build();
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

  private static List<String> replacePlayer(@NonNull List<String> lines, @NonNull String player) {
    var replaced = new ArrayList<String>(lines.size());
    for (var line : lines) {
      replaced.add(line.replace("{player}", player));
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
}
