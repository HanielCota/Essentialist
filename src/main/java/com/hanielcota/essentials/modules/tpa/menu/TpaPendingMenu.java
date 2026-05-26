package com.hanielcota.essentials.modules.tpa.menu;

import com.github.hanielcota.menuframework.MenuFramework;
import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.api.MenuSession;
import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.github.hanielcota.menuframework.definition.PaginationConfig;
import com.github.hanielcota.menuframework.definition.SlotDefinition;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.EssentialsMenu;
import com.hanielcota.essentials.menu.MenuLayouts;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.config.TpaPendingMenuConfig;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequest;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequestType;
import com.hanielcota.essentials.modules.tpa.service.TeleportRequestService;
import com.hanielcota.essentials.util.ComponentUtils;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class TpaPendingMenu implements EssentialsMenu {

  public static final String ID = "essentials.tpa.pending";

  private final ConfigHandle<TpaConfig> config;
  private final TeleportRequestService requests;
  private final TpaPendingClickHandler clicks;

  static List<Integer> contentSlots(@NonNull TpaPendingMenuConfig settings, int rows) {
    var fallback = MenuLayouts.fallbackContentSlots(rows, Math.min(7, MenuLayouts.slotCount(rows)));

    return MenuLayouts.sanitizeSlots(settings.contentSlots(), rows, fallback);
  }

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void register(@NonNull MenuService menus) {
    var settings = this.config.value().pendingMenu();
    var rows = MenuLayouts.clampRows(settings.rows());
    var title = ComponentUtils.mini(settings.title());
    var pagination = PaginationConfig.builder().contentSlots(contentSlots(settings, rows)).build();

    var builder = MenuFramework.builder(ID, menus);
    builder.rows(rows);
    builder.title(title);
    builder.pagination(pagination);
    builder.dynamicContent(this::buildSlots);
    builder.slot(
        backSlot(settings, rows), backTemplate(settings), click -> click.switchTo(TpaHelpMenu.ID));

    var menu = builder.build();
    menu.register();
  }

  private List<SlotDefinition> buildSlots(@NonNull Player player, @NonNull MenuSession session) {
    var playerId = player.getUniqueId();
    var pending = this.requests.incoming(playerId);

    if (pending.isEmpty()) {
      return emptySlot();
    }

    var slots = new ArrayList<SlotDefinition>(pending.size());
    for (var request : pending) {
      slots.add(requestSlot(request));
    }

    return slots;
  }

  private List<SlotDefinition> emptySlot() {
    var settings = this.config.value().pendingMenu();
    var rows = MenuLayouts.clampRows(settings.rows());
    var slots = contentSlots(settings, rows);
    var center = slots.get(slots.size() / 2);

    return List.of(SlotDefinition.of(center, emptyTemplate(settings), click -> {}));
  }

  private SlotDefinition requestSlot(@NonNull TeleportRequest request) {
    var template = requestTemplate(request);

    return SlotDefinition.of(-1, template, click -> this.clicks.handle(click, request));
  }

  private ItemTemplate requestTemplate(@NonNull TeleportRequest request) {
    var settings = this.config.value().pendingMenu();
    var name = applyRequestPlaceholders(settings.requestName(), request);
    var lore = applyRequestPlaceholders(settings.requestLore(), request);

    var builder = ItemTemplate.builder(settings.requestIcon());
    applyHead(builder, settings, request);
    builder.name(name);
    builder.lore(lore.toArray(String[]::new));
    builder.italic(false);

    return builder.build();
  }

  private static void applyHead(
      @NonNull ItemTemplate.Builder builder,
      @NonNull TpaPendingMenuConfig settings,
      @NonNull TeleportRequest request) {
    if (settings.requestIcon() != Material.PLAYER_HEAD) {
      return;
    }
    if (settings.requestUsePlayerHead()) {
      builder.head(request.requester().id());
      return;
    }
    if (!settings.requestHeadTexture().isBlank()) {
      builder.head(settings.requestHeadTexture());
    }
  }

  private List<String> applyRequestPlaceholders(
      @NonNull List<String> lines, @NonNull TeleportRequest request) {
    var replaced = new ArrayList<String>(lines.size());
    for (var line : lines) {
      replaced.add(applyRequestPlaceholders(line, request));
    }
    return replaced;
  }

  private String applyRequestPlaceholders(@NonNull String raw, @NonNull TeleportRequest request) {
    var requesterName = request.requester().name();
    var type = requestTypeLabel(request.type());
    var seconds = Long.toString(secondsLeft(request));

    return raw.replace("{player}", requesterName)
        .replace("{type}", type)
        .replace("{seconds}", seconds);
  }

  private String requestTypeLabel(@NonNull TeleportRequestType type) {
    var settings = this.config.value().pendingMenu();

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

  private ItemTemplate emptyTemplate(@NonNull TpaPendingMenuConfig settings) {
    var builder = ItemTemplate.builder(settings.emptyIcon());
    builder.name(settings.emptyName());
    builder.lore(settings.emptyLore().toArray(String[]::new));
    builder.italic(false);

    return builder.build();
  }

  private static int backSlot(@NonNull TpaPendingMenuConfig settings, int rows) {
    return MenuLayouts.sanitizeSlot(settings.backSlot(), rows, 0);
  }

  private static ItemTemplate backTemplate(@NonNull TpaPendingMenuConfig settings) {
    var builder = ItemTemplate.builder(settings.backIcon());
    builder.name(settings.backName());
    builder.lore(settings.backLore().toArray(String[]::new));
    builder.italic(false);

    return builder.build();
  }
}
