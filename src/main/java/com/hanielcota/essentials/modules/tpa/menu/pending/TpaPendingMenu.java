package com.hanielcota.essentials.modules.tpa.menu.pending;

import com.github.hanielcota.menuframework.MenuFramework;
import com.github.hanielcota.menuframework.api.ClickContext;
import com.github.hanielcota.menuframework.api.MenuFeatures;
import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.api.MenuSession;
import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.github.hanielcota.menuframework.definition.PaginationConfig;
import com.github.hanielcota.menuframework.definition.SlotDefinition;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.EssentialsMenu;
import com.hanielcota.essentials.menu.MenuLayouts;
import com.hanielcota.essentials.menu.PageNavigation;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.config.menu.TpaPendingMenuConfig;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequest;
import com.hanielcota.essentials.modules.tpa.menu.TpaHelpMenu;
import com.hanielcota.essentials.modules.tpa.menu.presentation.TpaPendingMenuRenderer;
import com.hanielcota.essentials.modules.tpa.service.TeleportRequestService;
import com.hanielcota.essentials.paper.PlayerProvider;
import com.hanielcota.essentials.shared.ComponentUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class TpaPendingMenu implements EssentialsMenu {

  public static final String ID = "essentials.tpa.pending";

  private static final int MIN_ROWS = 1;
  private static final long COUNTDOWN_REFRESH_TICKS = 20L;

  private final ConfigHandle<TpaConfig> config;
  private final TeleportRequestService requests;
  private final TpaPendingClickHandler clicks;
  private final PlayerProvider players;
  private final TpaPendingMenuRenderer renderer;

  public static List<Integer> contentSlots(@NonNull TpaPendingMenuConfig settings, int rows) {
    var fallback = MenuLayouts.fallbackContentSlots(rows, Math.min(7, MenuLayouts.slotCount(rows)));

    return MenuLayouts.sanitizeSlots(settings.contentSlots(), rows, fallback);
  }

  private static int backSlot(@NonNull TpaPendingMenuConfig settings, int rows) {
    return MenuLayouts.sanitizeSlot(settings.backSlot(), rows, 0);
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
    var paginationBuilder = PaginationConfig.builder().contentSlots(contentSlots(settings, rows));
    if (rows > MIN_ROWS) {
      PageNavigation.apply(menus, paginationBuilder, ID, rows, settings.navigation());
    }
    var pagination = paginationBuilder.build();

    var countdownFeature = MenuFeatures.refreshInterval(COUNTDOWN_REFRESH_TICKS);

    var builder = MenuFramework.builder(ID, menus);
    builder.rows(rows);
    builder.title(title);
    builder.pagination(pagination);
    builder.dynamicContent(this::buildSlots);
    builder.feature(countdownFeature);
    builder.slot(
        backSlot(settings, rows),
        this.renderer.backTemplate(settings),
        click -> click.switchTo(TpaHelpMenu.ID));

    builder.buildAndRegister();
  }

  private SlotDefinition bulkSlot(
      int configuredSlot,
      int rows,
      @NonNull Material icon,
      @NonNull String nameTemplate,
      @NonNull List<String> loreTemplate,
      int pending,
      @NonNull Consumer<ClickContext> handler) {
    var template = this.renderer.bulkTemplate(icon, nameTemplate, loreTemplate, pending);
    var safeSlot = MenuLayouts.sanitizeSlot(configuredSlot, rows, 0);
    return SlotDefinition.of(safeSlot, template, handler::accept);
  }

  private List<SlotDefinition> buildSlots(@NonNull Player player, @NonNull MenuSession session) {
    var playerId = player.getUniqueId();
    var pending = this.requests.incoming(playerId);
    var settings = this.config.value().pendingMenu();
    var rows = MenuLayouts.clampRows(settings.rows());

    var slots = new ArrayList<SlotDefinition>();

    if (pending.isEmpty()) {
      slots.addAll(emptySlot());
    } else {
      for (var request : pending) {
        slots.add(requestSlot(request, player));
      }
    }

    slots.add(
        bulkSlot(
            settings.acceptAllSlot(),
            rows,
            settings.acceptAllIcon(),
            settings.acceptAllName(),
            settings.acceptAllLore(),
            pending.size(),
            this.clicks::acceptAll));
    slots.add(
        bulkSlot(
            settings.denyAllSlot(),
            rows,
            settings.denyAllIcon(),
            settings.denyAllName(),
            settings.denyAllLore(),
            pending.size(),
            this.clicks::denyAll));

    return slots;
  }

  private List<SlotDefinition> emptySlot() {
    var settings = this.config.value().pendingMenu();
    var rows = MenuLayouts.clampRows(settings.rows());
    var slots = contentSlots(settings, rows);
    var center = slots.get(slots.size() / 2);

    var template = this.renderer.emptyTemplate(settings);

    return List.of(SlotDefinition.of(center, template, click -> {}));
  }

  private SlotDefinition requestSlot(@NonNull TeleportRequest request, @NonNull Player viewer) {
    var template = requestTemplate(request, viewer);

    return SlotDefinition.of(-1, template, click -> this.clicks.handle(click, request));
  }

  private ItemTemplate requestTemplate(@NonNull TeleportRequest request, @NonNull Player viewer) {
    var settings = this.config.value().pendingMenu();
    var requesterPlayer = this.players.online(request.requester().id()).orElse(null);

    return this.renderer.requestTemplate(settings, request, requesterPlayer, viewer);
  }
}
