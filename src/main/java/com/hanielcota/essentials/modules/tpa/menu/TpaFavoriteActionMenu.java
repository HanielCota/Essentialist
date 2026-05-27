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
import com.hanielcota.essentials.modules.tpa.command.TpaSendOrchestrator;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.config.menu.TpaFavoriteActionMenuConfig;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequestType;
import com.hanielcota.essentials.modules.tpa.domain.TpaFavorite;
import com.hanielcota.essentials.modules.tpa.service.TeleportRequestService;
import com.hanielcota.essentials.modules.tpa.service.TpaFavoriteSelections;
import com.hanielcota.essentials.modules.tpa.service.TpaFavoriteService;
import com.hanielcota.essentials.paper.ActorFactory;
import com.hanielcota.essentials.paper.PlayerProvider;
import com.hanielcota.essentials.shared.ComponentUtils;
import com.hanielcota.essentials.shared.Placeholders;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class TpaFavoriteActionMenu implements EssentialsMenu {

  public static final String ID = "essentials.tpa.favorite.action";

  private final ConfigHandle<TpaConfig> config;
  private final TpaFavoriteService favorites;
  private final TpaFavoriteSelections selections;
  private final TeleportRequestService requests;
  private final PlayerProvider players;
  private final ActorFactory actors;
  private final TpaSendOrchestrator dispatcher;

  static List<Integer> contentSlots(@NonNull TpaFavoriteActionMenuConfig settings, int rows) {
    return List.of(
        MenuLayouts.sanitizeSlot(settings.targetSlot(), rows, 0),
        MenuLayouts.sanitizeSlot(settings.tpaSlot(), rows, 0),
        MenuLayouts.sanitizeSlot(settings.tpaHereSlot(), rows, 0),
        MenuLayouts.sanitizeSlot(settings.removeSlot(), rows, 0),
        MenuLayouts.sanitizeSlot(settings.backSlot(), rows, 0));
  }

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void register(@NonNull MenuService menus) {
    var settings = this.config.value().favoriteActionMenu();
    var rows = MenuLayouts.clampRows(settings.rows());
    var pagination = PaginationConfig.builder().contentSlots(contentSlots(settings, rows)).build();

    var builder = MenuFramework.builder(ID, menus);
    builder.rows(rows);
    builder.title(ComponentUtils.mini(settings.title()));
    builder.pagination(pagination);
    builder.dynamicContent(this::buildSlots);

    var menu = builder.build();
    menu.register();
  }

  private List<SlotDefinition> buildSlots(@NonNull Player player, @NonNull MenuSession session) {
    var viewerId = player.getUniqueId();
    var selected = this.selections.of(viewerId);
    var settings = this.config.value().favoriteActionMenu();
    var rows = MenuLayouts.clampRows(settings.rows());

    if (selected == null) {
      return List.of(backSlotDefinition(settings, rows));
    }

    var slots = new ArrayList<SlotDefinition>(5);
    slots.add(targetSlot(settings, rows, selected));
    slots.add(actionSlot(settings, rows, selected, TeleportRequestType.TPA));
    slots.add(actionSlot(settings, rows, selected, TeleportRequestType.TPAHERE));
    slots.add(removeSlot(settings, rows, selected));
    slots.add(backSlotDefinition(settings, rows));
    return slots;
  }

  private SlotDefinition targetSlot(
      @NonNull TpaFavoriteActionMenuConfig settings, int rows, @NonNull TpaFavorite entry) {
    var template = targetTemplate(settings, entry);
    var safeSlot = MenuLayouts.sanitizeSlot(settings.targetSlot(), rows, 0);

    return SlotDefinition.of(safeSlot, template, click -> {});
  }

  private SlotDefinition actionSlot(
      @NonNull TpaFavoriteActionMenuConfig settings,
      int rows,
      @NonNull TpaFavorite entry,
      @NonNull TeleportRequestType type) {
    var nameTemplate =
        type == TeleportRequestType.TPA ? settings.tpaName() : settings.tpaHereName();
    var loreTemplate =
        type == TeleportRequestType.TPA ? settings.tpaLore() : settings.tpaHereLore();
    var icon = type == TeleportRequestType.TPA ? settings.tpaIcon() : settings.tpaHereIcon();
    var slot = type == TeleportRequestType.TPA ? settings.tpaSlot() : settings.tpaHereSlot();

    var name = nameTemplate.replace("{player}", entry.favoriteName());
    var lore = Placeholders.replaceInAll(loreTemplate, "{player}", entry.favoriteName());
    var template = MenuTemplates.simple(icon, name, lore);
    var safeSlot = MenuLayouts.sanitizeSlot(slot, rows, 0);

    return SlotDefinition.of(safeSlot, template, click -> sendRequest(click, entry, type));
  }

  private SlotDefinition removeSlot(
      @NonNull TpaFavoriteActionMenuConfig settings, int rows, @NonNull TpaFavorite entry) {
    var name = settings.removeName().replace("{player}", entry.favoriteName());
    var lore = Placeholders.replaceInAll(settings.removeLore(), "{player}", entry.favoriteName());
    var template = MenuTemplates.simple(settings.removeIcon(), name, lore);
    var safeSlot = MenuLayouts.sanitizeSlot(settings.removeSlot(), rows, 0);

    return SlotDefinition.of(safeSlot, template, click -> removeFavorite(click, entry));
  }

  private SlotDefinition backSlotDefinition(
      @NonNull TpaFavoriteActionMenuConfig settings, int rows) {
    var template =
        MenuTemplates.simple(settings.backIcon(), settings.backName(), settings.backLore());
    var safeSlot = MenuLayouts.sanitizeSlot(settings.backSlot(), rows, 0);

    return SlotDefinition.of(safeSlot, template, this::onBackClicked);
  }

  private void sendRequest(
      @NonNull ClickContext click, @NonNull TpaFavorite entry, @NonNull TeleportRequestType type) {
    var viewer = click.player();
    var actor = this.actors.actorOf(viewer);
    var snap = this.config.value();
    var messages = snap.messages();

    var resolved = this.players.online(entry.favoriteId());
    if (resolved.isEmpty()) {
      var offlineText = messages.favoriteOffline().replace("{player}", entry.favoriteName());
      actor.sendError(offlineText);
      return;
    }

    var target = resolved.get();
    var confirmationTemplate =
        type == TeleportRequestType.TPA ? messages.requestSent() : messages.requestSentHere();

    click.close();
    this.selections.clear(viewer.getUniqueId());
    this.dispatcher.send(actor, target, type, confirmationTemplate);
  }

  private void removeFavorite(@NonNull ClickContext click, @NonNull TpaFavorite entry) {
    var viewer = click.player();
    var viewerId = viewer.getUniqueId();
    var actor = this.actors.actorOf(viewer);

    this.favorites.remove(viewerId, entry.favoriteId());
    this.selections.clear(viewerId);

    var messages = this.config.value().messages();
    var removedText = messages.favoriteRemoved().replace("{player}", entry.favoriteName());
    actor.sendSuccess(removedText);

    click.switchTo(TpaFavoritesMenu.ID);
  }

  private void onBackClicked(@NonNull ClickContext click) {
    var viewerId = click.player().getUniqueId();
    this.selections.clear(viewerId);

    click.switchTo(TpaFavoritesMenu.ID);
  }

  private ItemTemplate targetTemplate(
      @NonNull TpaFavoriteActionMenuConfig settings, @NonNull TpaFavorite entry) {
    var name = settings.targetName().replace("{player}", entry.favoriteName());
    var lore = Placeholders.replaceInAll(settings.targetLore(), "{player}", entry.favoriteName());

    var builder = ItemTemplate.builder(settings.targetIcon());
    MenuTemplates.applyHead(
        builder,
        settings.targetIcon(),
        settings.targetUsePlayerHead(),
        settings.targetHeadTexture(),
        entry.favoriteId());
    builder.name(name);
    builder.lore(lore.toArray(String[]::new));
    builder.italic(false);

    return builder.build();
  }
}
