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
import com.hanielcota.essentials.modules.tpa.command.TpaFavoriteAddNotifier;
import com.hanielcota.essentials.modules.tpa.command.TpaSendOrchestrator;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.config.menu.TpaTargetActionMenuConfig;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequestType;
import com.hanielcota.essentials.modules.tpa.domain.TpaTargetSelection;
import com.hanielcota.essentials.modules.tpa.service.TpaFavoriteService;
import com.hanielcota.essentials.modules.tpa.service.TpaTargetSelections;
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
public final class TpaTargetActionMenu implements EssentialsMenu {

  public static final String ID = "essentials.tpa.target.action";

  private final ConfigHandle<TpaConfig> config;
  private final TpaTargetSelections selections;
  private final TpaFavoriteService favorites;
  private final TpaFavoriteAddNotifier addNotifier;
  private final PlayerProvider players;
  private final ActorFactory actors;
  private final TpaSendOrchestrator dispatcher;

  static List<Integer> contentSlots(@NonNull TpaTargetActionMenuConfig settings, int rows) {
    var configured =
        List.of(
            settings.targetSlot(),
            settings.tpaSlot(),
            settings.tpaHereSlot(),
            settings.favoriteAddSlot(),
            settings.favoriteRemoveSlot(),
            settings.backSlot());
    var fallback = MenuLayouts.fallbackContentSlots(rows, configured.size());

    return MenuLayouts.sanitizeSlots(configured, rows, fallback);
  }

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void register(@NonNull MenuService menus) {
    var settings = this.config.value().targetActionMenu();
    var rows = MenuLayouts.clampRows(settings.rows());
    var title = ComponentUtils.mini(settings.title());
    var pagination = PaginationConfig.builder().contentSlots(contentSlots(settings, rows)).build();

    var builder = MenuFramework.builder(ID, menus);
    builder.rows(rows);
    builder.title(title);
    builder.pagination(pagination);
    builder.dynamicContent(this::buildSlots);

    builder.buildAndRegister();
  }

  private List<SlotDefinition> buildSlots(@NonNull Player player, @NonNull MenuSession session) {
    var viewerId = player.getUniqueId();
    var selected = this.selections.of(viewerId);
    var settings = this.config.value().targetActionMenu();
    var rows = MenuLayouts.clampRows(settings.rows());

    if (selected == null) {
      return List.of(backSlotDefinition(settings, rows));
    }

    var isFavorite = this.favorites.isFavorite(viewerId, selected.targetId());

    var slots = new ArrayList<SlotDefinition>(5);
    slots.add(targetSlot(settings, rows, selected));
    slots.add(actionSlot(settings, rows, selected, TeleportRequestType.TPA));
    slots.add(actionSlot(settings, rows, selected, TeleportRequestType.TPAHERE));
    slots.add(favoriteToggleSlot(settings, rows, selected, isFavorite));
    slots.add(backSlotDefinition(settings, rows));
    return slots;
  }

  private SlotDefinition targetSlot(
      @NonNull TpaTargetActionMenuConfig settings, int rows, @NonNull TpaTargetSelection entry) {
    var template = targetTemplate(settings, entry);
    var safeSlot = MenuLayouts.sanitizeSlot(settings.targetSlot(), rows, 0);

    return SlotDefinition.of(safeSlot, template, click -> {});
  }

  private SlotDefinition actionSlot(
      @NonNull TpaTargetActionMenuConfig settings,
      int rows,
      @NonNull TpaTargetSelection entry,
      @NonNull TeleportRequestType type) {
    var isTpa = type == TeleportRequestType.TPA;
    var nameTemplate = isTpa ? settings.tpaName() : settings.tpaHereName();
    var loreTemplate = isTpa ? settings.tpaLore() : settings.tpaHereLore();
    var icon = isTpa ? settings.tpaIcon() : settings.tpaHereIcon();
    var slot = isTpa ? settings.tpaSlot() : settings.tpaHereSlot();

    var name = nameTemplate.replace("{player}", entry.targetName());
    var lore = buildActionLore(settings, loreTemplate, entry, type);
    var template = MenuTemplates.simple(icon, name, lore);
    var safeSlot = MenuLayouts.sanitizeSlot(slot, rows, 0);

    return SlotDefinition.of(safeSlot, template, click -> sendRequest(click, entry, type));
  }

  private List<String> buildActionLore(
      @NonNull TpaTargetActionMenuConfig settings,
      @NonNull List<String> loreTemplate,
      @NonNull TpaTargetSelection entry,
      @NonNull TeleportRequestType type) {
    var replaced = Placeholders.replaceInAll(loreTemplate, "{player}", entry.targetName());
    var isRecommended = entry.preferredType() == type;
    var hasTag = !settings.recommendedTag().isBlank();
    if (!isRecommended || !hasTag) {
      return replaced;
    }

    var lore = new ArrayList<String>(replaced.size() + 2);
    lore.add(settings.recommendedTag());
    lore.add("");
    lore.addAll(replaced);
    return lore;
  }

  private SlotDefinition favoriteToggleSlot(
      @NonNull TpaTargetActionMenuConfig settings,
      int rows,
      @NonNull TpaTargetSelection entry,
      boolean isFavorite) {
    var icon = isFavorite ? settings.favoriteRemoveIcon() : settings.favoriteAddIcon();
    var nameTemplate = isFavorite ? settings.favoriteRemoveName() : settings.favoriteAddName();
    var loreTemplate = isFavorite ? settings.favoriteRemoveLore() : settings.favoriteAddLore();
    var slot = isFavorite ? settings.favoriteRemoveSlot() : settings.favoriteAddSlot();

    var name = nameTemplate.replace("{player}", entry.targetName());
    var lore = Placeholders.replaceInAll(loreTemplate, "{player}", entry.targetName());
    var template = MenuTemplates.simple(icon, name, lore);
    var safeSlot = MenuLayouts.sanitizeSlot(slot, rows, 0);

    return SlotDefinition.of(safeSlot, template, click -> toggleFavorite(click, entry, isFavorite));
  }

  private SlotDefinition backSlotDefinition(@NonNull TpaTargetActionMenuConfig settings, int rows) {
    var template =
        MenuTemplates.simple(settings.backIcon(), settings.backName(), settings.backLore());
    var safeSlot = MenuLayouts.sanitizeSlot(settings.backSlot(), rows, 0);

    return SlotDefinition.of(safeSlot, template, this::onBackClicked);
  }

  private void sendRequest(
      @NonNull ClickContext click,
      @NonNull TpaTargetSelection entry,
      @NonNull TeleportRequestType type) {
    var viewer = click.player();
    var actor = this.actors.actorOf(viewer);
    var snap = this.config.value();
    var messages = snap.messages();

    var resolved = this.players.online(entry.targetId());
    if (resolved.isEmpty()) {
      var offlineText = messages.favoriteOffline().replace("{player}", entry.targetName());
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

  private void toggleFavorite(
      @NonNull ClickContext click, @NonNull TpaTargetSelection entry, boolean isFavorite) {
    var viewer = click.player();
    var viewerId = viewer.getUniqueId();
    var actor = this.actors.actorOf(viewer);
    var messages = this.config.value().messages();

    if (isFavorite) {
      this.favorites.remove(viewerId, entry.targetId());
      var removedText = messages.favoriteRemoved().replace("{player}", entry.targetName());
      actor.sendSuccess(removedText);
      click.refresh();
      return;
    }

    var added = this.favorites.add(viewerId, entry.targetId(), entry.targetName());
    if (!added) {
      var alreadyText = messages.favoriteAlready().replace("{player}", entry.targetName());
      actor.sendError(alreadyText);
      return;
    }

    var addedText = messages.favoriteAdded().replace("{player}", entry.targetName());
    actor.sendSuccess(addedText);
    this.addNotifier.notify(viewer.getName(), entry.targetId());
    click.refresh();
  }

  private void onBackClicked(@NonNull ClickContext click) {
    var viewerId = click.player().getUniqueId();
    this.selections.clear(viewerId);

    click.switchTo(TpaHelpMenu.ID);
  }

  private ItemTemplate targetTemplate(
      @NonNull TpaTargetActionMenuConfig settings, @NonNull TpaTargetSelection entry) {
    var name = settings.targetName().replace("{player}", entry.targetName());
    var lore = Placeholders.replaceInAll(settings.targetLore(), "{player}", entry.targetName());

    var builder = ItemTemplate.builder(settings.targetIcon());
    MenuTemplates.applyHead(
        builder,
        settings.targetIcon(),
        settings.targetUsePlayerHead(),
        settings.targetHeadTexture(),
        entry.targetId());
    builder.name(name);
    builder.lore(lore.toArray(String[]::new));
    builder.italic(false);

    return builder.build();
  }
}
