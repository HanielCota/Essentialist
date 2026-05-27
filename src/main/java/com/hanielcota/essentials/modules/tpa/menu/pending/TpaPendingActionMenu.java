package com.hanielcota.essentials.modules.tpa.menu.pending;

import com.github.hanielcota.menuframework.api.ClickContext;
import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.api.MenuSession;
import com.github.hanielcota.menuframework.definition.SlotDefinition;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.EssentialsMenu;
import com.hanielcota.essentials.menu.MenuLayouts;
import com.hanielcota.essentials.menu.PaginatedMenus;
import com.hanielcota.essentials.modules.tpa.command.TpAcceptOutcomeHandler;
import com.hanielcota.essentials.modules.tpa.command.TpaRequestReplyNotifier;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.config.menu.TpaPendingActionMenuConfig;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequest;
import com.hanielcota.essentials.modules.tpa.menu.presentation.TpaPendingActionRenderer;
import com.hanielcota.essentials.modules.tpa.service.TeleportRequestService;
import com.hanielcota.essentials.modules.tpa.service.TpaBlockService;
import com.hanielcota.essentials.modules.tpa.service.TpaPendingSelections;
import com.hanielcota.essentials.paper.ActorFactory;
import com.hanielcota.essentials.scheduler.MainThreadCallbacks;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import org.bukkit.entity.Player;

public final class TpaPendingActionMenu implements EssentialsMenu {

  public static final String ID = "essentials.tpa.pending.action";

  private final ConfigHandle<TpaConfig> config;
  private final TpaPendingSelections selections;
  private final TpaPendingActionClickHandler handler;
  private final TpaPendingActionRenderer renderer;

  public TpaPendingActionMenu(
      @NonNull ConfigHandle<TpaConfig> config,
      @NonNull TeleportRequestService service,
      @NonNull TpaBlockService blocks,
      @NonNull TpaPendingSelections selections,
      @NonNull TpAcceptOutcomeHandler acceptHandler,
      @NonNull TpaRequestReplyNotifier replyNotifier,
      @NonNull MainThreadCallbacks callbacks,
      @NonNull ActorFactory actors) {
    this.config = config;
    this.selections = selections;
    this.renderer = new TpaPendingActionRenderer();
    this.handler =
        new TpaPendingActionClickHandler(
            config, service, blocks, selections, acceptHandler, replyNotifier, callbacks, actors);
  }

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
    var slots = contentSlots(settings, rows);

    PaginatedMenus.register(menus, ID, rows, settings.title(), slots, this::buildSlots);
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
    slots.add(acceptSlot(settings, rows, selected));
    slots.add(denySlot(settings, rows, selected));
    slots.add(blockSlot(settings, rows, selected));
    slots.add(backSlotDefinition(settings, rows));
    return slots;
  }

  private SlotDefinition targetSlot(
      @NonNull TpaPendingActionMenuConfig settings, int rows, @NonNull TeleportRequest request) {
    var template = this.renderer.targetTemplate(settings, request);
    var safeSlot = MenuLayouts.sanitizeSlot(settings.targetSlot(), rows, 0);

    return SlotDefinition.of(safeSlot, template, click -> {});
  }

  private SlotDefinition acceptSlot(
      @NonNull TpaPendingActionMenuConfig settings, int rows, @NonNull TeleportRequest request) {
    var template = this.renderer.acceptTemplate(settings, request);
    var safeSlot = MenuLayouts.sanitizeSlot(settings.acceptSlot(), rows, 0);

    return SlotDefinition.of(safeSlot, template, click -> this.handler.accept(click, request));
  }

  private SlotDefinition denySlot(
      @NonNull TpaPendingActionMenuConfig settings, int rows, @NonNull TeleportRequest request) {
    var template = this.renderer.denyTemplate(settings, request);
    var safeSlot = MenuLayouts.sanitizeSlot(settings.denySlot(), rows, 0);

    return SlotDefinition.of(safeSlot, template, click -> this.handler.deny(click, request));
  }

  private SlotDefinition blockSlot(
      @NonNull TpaPendingActionMenuConfig settings, int rows, @NonNull TeleportRequest request) {
    var template = this.renderer.blockTemplate(settings, request);
    var safeSlot = MenuLayouts.sanitizeSlot(settings.blockSlot(), rows, 0);

    return SlotDefinition.of(safeSlot, template, click -> this.handler.block(click, request));
  }

  private SlotDefinition backSlotDefinition(
      @NonNull TpaPendingActionMenuConfig settings, int rows) {
    var template = this.renderer.backTemplate(settings);
    var safeSlot = MenuLayouts.sanitizeSlot(settings.backSlot(), rows, 0);

    return SlotDefinition.of(safeSlot, template, this::onBackClicked);
  }

  private void onBackClicked(@NonNull ClickContext click) {
    var viewerId = click.player().getUniqueId();
    this.selections.clear(viewerId);

    click.switchTo(TpaPendingMenu.ID);
  }
}
