package com.hanielcota.essentials.menu;

import com.github.hanielcota.menuframework.api.MenuService;
import java.util.UUID;
import java.util.function.Consumer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Generic per-viewer menu-state cleanup. Runs {@code onClear} when the viewer closes the inventory
 * belonging to {@code menuId} (verified against the live {@link MenuService} session and view) or
 * when they quit mid-menu. Shared by every menu that keeps per-viewer state so the close/quit
 * plumbing is written once.
 */
@RequiredArgsConstructor
public final class MenuViewCleanupListener implements Listener {

  private final MenuService menus;
  private final String menuId;
  private final Consumer<UUID> onClear;

  @EventHandler(priority = EventPriority.MONITOR)
  public void onClose(@NonNull InventoryCloseEvent event) {
    var human = event.getPlayer();
    if (!(human instanceof Player viewer)) {
      return;
    }

    var viewerId = viewer.getUniqueId();
    var sessionOpt = this.menus.getSession(viewerId);
    var session = sessionOpt.orElse(null);
    if (session == null) {
      return;
    }

    var openMenuId = session.menuId();
    if (!openMenuId.equals(this.menuId)) {
      return;
    }

    var view = event.getView();
    if (!session.isSameView(view)) {
      return;
    }

    this.onClear.accept(viewerId);
  }

  @EventHandler
  public void onQuit(@NonNull PlayerQuitEvent event) {
    var quitter = event.getPlayer();
    var viewerId = quitter.getUniqueId();

    this.onClear.accept(viewerId);
  }
}
