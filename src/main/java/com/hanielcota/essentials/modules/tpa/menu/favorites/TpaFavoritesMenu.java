package com.hanielcota.essentials.modules.tpa.menu.favorites;

import com.github.hanielcota.menuframework.MenuFramework;
import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.api.MenuSession;
import com.github.hanielcota.menuframework.definition.PaginationConfig;
import com.github.hanielcota.menuframework.definition.SlotDefinition;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.EssentialsMenu;
import com.hanielcota.essentials.menu.MenuLayouts;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.config.menu.TpaFavoritesMenuConfig;
import com.hanielcota.essentials.modules.tpa.domain.FavoriteOrdering;
import com.hanielcota.essentials.modules.tpa.domain.TpaContact;
import com.hanielcota.essentials.modules.tpa.domain.TpaFavorite;
import com.hanielcota.essentials.modules.tpa.menu.help.TpaHelpMenu;
import com.hanielcota.essentials.modules.tpa.menu.presentation.TpaFavoriteBrowser;
import com.hanielcota.essentials.modules.tpa.menu.presentation.TpaFavoriteMenuRenderer;
import com.hanielcota.essentials.modules.tpa.service.TpaProfileService;
import com.hanielcota.essentials.shared.ComponentUtils;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class TpaFavoritesMenu implements EssentialsMenu {

  public static final String ID = "essentials.tpa.favorites";

  private final @NonNull ConfigHandle<TpaConfig> config;
  private final @NonNull TpaProfileService profiles;
  private final @NonNull TpaFavoriteBrowser browser;
  private final @NonNull TpaFavoriteClickHandler clicks;
  private final @NonNull TpaFavoriteMenuRenderer renderer;

  static List<Integer> contentSlots(@NonNull TpaFavoritesMenuConfig settings, int rows) {
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
    var settings = this.config.value().favoritesMenu();
    var rows = MenuLayouts.clampRows(settings.rows());
    var slots = contentSlots(settings, rows);
    var pagination = PaginationConfig.builder().contentSlots(slots).build();
    var title = ComponentUtils.mini(settings.title());

    var addSlot = this.renderer.addSlot(settings, rows);
    var addTemplate = this.renderer.addTemplate(settings);
    var backSlot = this.renderer.backSlot(settings, rows);
    var backTemplate = this.renderer.backTemplate(settings);

    var builder = MenuFramework.builder(ID, menus);
    builder.rows(rows);
    builder.title(title);
    builder.pagination(pagination);
    builder.dynamicContent(this::buildSlots);
    builder.slot(addSlot, addTemplate, this.clicks::add);
    builder.slot(backSlot, backTemplate, click -> click.switchTo(TpaHelpMenu.ID));

    builder.buildAndRegister();
  }

  private List<SlotDefinition> buildSlots(@NonNull Player player, @NonNull MenuSession session) {
    var viewerId = player.getUniqueId();
    var ordering = this.profiles.profile(viewerId).favoriteOrdering();
    var contentSize = contentSize(session);
    var view = this.browser.view(viewerId, ordering, contentSize);

    var slots = new ArrayList<SlotDefinition>();
    slots.add(orderingSlot(ordering));

    if (view.isEmpty()) {
      slots.add(emptySlot());
      return slots;
    }

    for (var entry : view.favorites()) {
      slots.add(favoriteSlot(entry));
    }

    for (var suggestion : view.suggestions()) {
      slots.add(suggestionSlot(suggestion));
    }

    return slots;
  }

  private int contentSize(@NonNull MenuSession session) {
    var settings = this.config.value().favoritesMenu();
    var rows = MenuLayouts.clampRows(settings.rows());
    var slotCount = contentSlots(settings, rows).size();
    var pageCount = Math.max(1, session.totalPages());

    return slotCount * pageCount;
  }

  private SlotDefinition favoriteSlot(@NonNull TpaFavorite entry) {
    var settings = this.config.value().favoritesMenu();
    var template = this.renderer.favoriteTemplate(settings, entry);

    return SlotDefinition.of(-1, template, click -> this.clicks.selectFavorite(click, entry));
  }

  private SlotDefinition suggestionSlot(@NonNull TpaContact contact) {
    var settings = this.config.value().favoritesMenu();
    var template = this.renderer.suggestionTemplate(settings, contact);

    return SlotDefinition.of(-1, template, click -> this.clicks.selectSuggestion(click, contact));
  }

  private SlotDefinition orderingSlot(@NonNull FavoriteOrdering ordering) {
    var settings = this.config.value().favoritesMenu();
    var rows = MenuLayouts.clampRows(settings.rows());
    var template = this.renderer.orderingTemplate(settings, ordering);
    var safeSlot = MenuLayouts.sanitizeSlot(settings.orderingSlot(), rows, 0);

    return SlotDefinition.of(safeSlot, template, this.clicks::cycleOrdering);
  }

  private SlotDefinition emptySlot() {
    var settings = this.config.value().favoritesMenu();
    var rows = MenuLayouts.clampRows(settings.rows());
    var slots = contentSlots(settings, rows);
    var center = slots.get(slots.size() / 2);
    var template = this.renderer.emptyTemplate(settings);

    return SlotDefinition.of(center, template, click -> {});
  }
}
