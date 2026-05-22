package com.hanielcota.essentials.modules.invsee.service;

import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

/** Schedules the write-back of an /invsee GUI to its target player. */
public final class InvseeSynchronizer {

  private final Plugin plugin;
  private final InvseeService service;

  public InvseeSynchronizer(Plugin plugin, InvseeService service) {
    this.plugin = Objects.requireNonNull(plugin, "plugin");
    this.service = Objects.requireNonNull(service, "service");
  }

  /** Syncs {@code view} back to its target next tick, once the current click/drag is applied. */
  public void scheduleSync(InvseeHolder holder, Inventory view) {
    Objects.requireNonNull(holder, "holder");
    Objects.requireNonNull(view, "view");

    Bukkit.getScheduler()
        .runTask(
            plugin,
            () -> {
              Player target = Bukkit.getPlayer(holder.targetId());
              if (target != null) {
                service.sync(target, view);
              }
            });
  }
}
