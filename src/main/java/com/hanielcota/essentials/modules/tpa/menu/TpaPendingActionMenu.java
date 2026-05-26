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
import com.hanielcota.essentials.modules.tpa.command.TpAcceptResultHandler;
import com.hanielcota.essentials.modules.tpa.command.TpaRequestReplyNotifier;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.config.TpaPendingActionMenuConfig;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequest;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequestType;
import com.hanielcota.essentials.modules.tpa.service.AcceptResult;
import com.hanielcota.essentials.modules.tpa.service.TeleportRequestService;
import com.hanielcota.essentials.modules.tpa.service.TpaBlockService;
import com.hanielcota.essentials.modules.tpa.service.TpaPendingSelections;
import com.hanielcota.essentials.paper.ActorFactory;
import com.hanielcota.essentials.scheduler.MainThreadCallbacks;
import com.hanielcota.essentials.util.ComponentUtils;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Per-request action sub-menu opened from {@link TpaPendingMenu}. Surfaces the Accept / Deny /
 * Block actions as explicit buttons so the block action isn't hidden behind a shift-click.
 */
@RequiredArgsConstructor
public final class TpaPendingActionMenu implements EssentialsMenu {

  public static final String ID = "essentials.tpa.pending.action";

  private final ConfigHandle<TpaConfig> config;
  private final TeleportRequestService service;
  private final TpaBlockService blocks;
  private final TpaPendingSelections selections;
  private final TpAcceptResultHandler acceptHandler;
  private final TpaRequestReplyNotifier replyNotifier;
  private final MainThreadCallbacks callbacks;
  private final ActorFactory actors;

  static List<Integer> contentSlots(@NonNull TpaPendingActionMenuConfig settings, int rows) {
    return List.of(
        MenuLayouts.sanitizeSlot(settings.targetSlot(), rows, 0),
        MenuLayouts.sanitizeSlot(settings.acceptSlot(), rows, 0),
        MenuLayouts.sanitizeSlot(settings.denySlot(), rows, 0),
        MenuLayouts.sanitizeSlot(settings.blockSlot(), rows, 0),
        MenuLayouts.sanitizeSlot(settings.backSlot(), rows, 0));
  }

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void register(@NonNull MenuService menus) {
    var settings = this.config.value().pendingActionMenu();
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

  private List<SlotDefinition> buildSlots(@NonNull Player viewer, @NonNull MenuSession session) {
    var viewerId = viewer.getUniqueId();
    var selected = this.selections.of(viewerId);
    var settings = this.config.value().pendingActionMenu();
    var rows = MenuLayouts.clampRows(settings.rows());

    if (selected == null) {
      return List.of(backSlotDefinition(settings, rows));
    }

    var slots = new ArrayList<SlotDefinition>(5);
    slots.add(targetSlot(settings, rows, selected));
    slots.add(acceptSlotDefinition(settings, rows, selected));
    slots.add(denySlotDefinition(settings, rows, selected));
    slots.add(blockSlotDefinition(settings, rows, selected));
    slots.add(backSlotDefinition(settings, rows));
    return slots;
  }

  private SlotDefinition targetSlot(
      @NonNull TpaPendingActionMenuConfig settings, int rows, @NonNull TeleportRequest request) {
    var template = targetTemplate(settings, request);
    var safeSlot = MenuLayouts.sanitizeSlot(settings.targetSlot(), rows, 0);

    return SlotDefinition.of(safeSlot, template, click -> {});
  }

  private SlotDefinition acceptSlotDefinition(
      @NonNull TpaPendingActionMenuConfig settings, int rows, @NonNull TeleportRequest request) {
    var requesterName = request.requester().name();
    var name = settings.acceptName().replace("{player}", requesterName);
    var lore = replacePlayer(settings.acceptLore(), requesterName);
    var template = simpleTemplate(settings.acceptIcon(), name, lore);
    var safeSlot = MenuLayouts.sanitizeSlot(settings.acceptSlot(), rows, 0);

    return SlotDefinition.of(safeSlot, template, click -> accept(click, request));
  }

  private SlotDefinition denySlotDefinition(
      @NonNull TpaPendingActionMenuConfig settings, int rows, @NonNull TeleportRequest request) {
    var requesterName = request.requester().name();
    var name = settings.denyName().replace("{player}", requesterName);
    var lore = replacePlayer(settings.denyLore(), requesterName);
    var template = simpleTemplate(settings.denyIcon(), name, lore);
    var safeSlot = MenuLayouts.sanitizeSlot(settings.denySlot(), rows, 0);

    return SlotDefinition.of(safeSlot, template, click -> deny(click, request));
  }

  private SlotDefinition blockSlotDefinition(
      @NonNull TpaPendingActionMenuConfig settings, int rows, @NonNull TeleportRequest request) {
    var requesterName = request.requester().name();
    var name = settings.blockName().replace("{player}", requesterName);
    var lore = replacePlayer(settings.blockLore(), requesterName);
    var template = simpleTemplate(settings.blockIcon(), name, lore);
    var safeSlot = MenuLayouts.sanitizeSlot(settings.blockSlot(), rows, 0);

    return SlotDefinition.of(safeSlot, template, click -> block(click, request));
  }

  private SlotDefinition backSlotDefinition(
      @NonNull TpaPendingActionMenuConfig settings, int rows) {
    var template = simpleTemplate(settings.backIcon(), settings.backName(), settings.backLore());
    var safeSlot = MenuLayouts.sanitizeSlot(settings.backSlot(), rows, 0);

    return SlotDefinition.of(safeSlot, template, this::onBackClicked);
  }

  private void accept(@NonNull ClickContext click, @NonNull TeleportRequest request) {
    var viewer = click.player();
    var viewerId = viewer.getUniqueId();
    var actor = this.actors.actorOf(viewer);
    var claim = this.service.tryAccept(request);

    this.acceptHandler.handleClaim(claim, request, actor);
    this.selections.clear(viewerId);

    if (claim != AcceptResult.ACCEPTED) {
      click.switchTo(TpaPendingMenu.ID);
      return;
    }

    click.close();
    var pending = this.service.dispatchTeleport(request);
    this.callbacks.hop(
        pending,
        success -> this.acceptHandler.handleTeleportOutcome(success, actor),
        "tpa pending action accept");
  }

  private void deny(@NonNull ClickContext click, @NonNull TeleportRequest request) {
    var viewer = click.player();
    var viewerId = viewer.getUniqueId();
    var actor = this.actors.actorOf(viewer);
    var messages = this.config.value().messages();

    this.service.deny(request);

    var requesterName = request.requester().name();
    var deniedSelf = messages.deniedSelf().replace("{player}", requesterName);
    actor.sendSuccess(deniedSelf);

    var deniedTemplate = messages.denied();
    this.replyNotifier.notifyDenied(request, deniedTemplate);

    this.selections.clear(viewerId);
    click.switchTo(TpaPendingMenu.ID);
  }

  private void block(@NonNull ClickContext click, @NonNull TeleportRequest request) {
    var viewer = click.player();
    var viewerId = viewer.getUniqueId();
    var actor = this.actors.actorOf(viewer);
    var messages = this.config.value().messages();
    var requesterId = request.requester().id();
    var requesterName = request.requester().name();

    this.service.deny(request);
    this.blocks.block(viewerId, requesterId, requesterName);

    var blockedMsg = messages.blockedPlayer().replace("{player}", requesterName);
    actor.sendSuccess(blockedMsg);

    this.selections.clear(viewerId);
    click.switchTo(TpaPendingMenu.ID);
  }

  private void onBackClicked(@NonNull ClickContext click) {
    var viewerId = click.player().getUniqueId();
    this.selections.clear(viewerId);

    click.switchTo(TpaPendingMenu.ID);
  }

  private ItemTemplate targetTemplate(
      @NonNull TpaPendingActionMenuConfig settings, @NonNull TeleportRequest request) {
    var requesterName = request.requester().name();
    var typeLabel = typeLabel(settings, request.type());
    var seconds = Long.toString(secondsLeft(request));

    var name =
        settings
            .targetName()
            .replace("{player}", requesterName)
            .replace("{type}", typeLabel)
            .replace("{seconds}", seconds);
    var lore = applyTargetPlaceholders(settings.targetLore(), requesterName, typeLabel, seconds);

    var builder = ItemTemplate.builder(settings.targetIcon());
    applyTargetHead(builder, settings, request);
    builder.name(name);
    builder.lore(lore.toArray(String[]::new));
    builder.italic(false);

    return builder.build();
  }

  private static void applyTargetHead(
      @NonNull ItemTemplate.Builder builder,
      @NonNull TpaPendingActionMenuConfig settings,
      @NonNull TeleportRequest request) {
    if (settings.targetIcon() != Material.PLAYER_HEAD) {
      return;
    }
    if (settings.targetUsePlayerHead()) {
      builder.head(request.requester().id());
      return;
    }
    if (!settings.targetHeadTexture().isBlank()) {
      builder.head(settings.targetHeadTexture());
    }
  }

  private static String typeLabel(
      @NonNull TpaPendingActionMenuConfig settings, @NonNull TeleportRequestType type) {
    return switch (type) {
      case TPA -> settings.typeTpa();
      case TPAHERE -> settings.typeTpaHere();
    };
  }

  private static long secondsLeft(@NonNull TeleportRequest request) {
    var now = Instant.now();
    var remaining = Duration.between(now, request.window().expiresAt()).toSeconds();

    return Math.max(0, remaining);
  }

  private static List<String> applyTargetPlaceholders(
      @NonNull List<String> lines,
      @NonNull String player,
      @NonNull String type,
      @NonNull String seconds) {
    var replaced = new ArrayList<String>(lines.size());
    for (var line : lines) {
      var withPlayer = line.replace("{player}", player);
      var withType = withPlayer.replace("{type}", type);
      var withSeconds = withType.replace("{seconds}", seconds);
      replaced.add(withSeconds);
    }
    return replaced;
  }

  private static List<String> replacePlayer(@NonNull List<String> lines, @NonNull String player) {
    var replaced = new ArrayList<String>(lines.size());
    for (var line : lines) {
      replaced.add(line.replace("{player}", player));
    }
    return replaced;
  }

  private static ItemTemplate simpleTemplate(
      @NonNull Material material, @NonNull String name, @NonNull List<String> lore) {
    var builder = ItemTemplate.builder(material);
    builder.name(name);
    builder.lore(lore.toArray(String[]::new));
    builder.italic(false);

    return builder.build();
  }
}
