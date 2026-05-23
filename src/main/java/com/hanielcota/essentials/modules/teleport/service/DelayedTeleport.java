package com.hanielcota.essentials.modules.teleport.service;

import com.hanielcota.essentials.scheduler.Scheduler;
import com.hanielcota.essentials.scheduler.Task;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Teleports a player after a configurable delay, cancelling on movement or damage.
 *
 * <p>Used by {@code /spawn}, {@code /home} and {@code /warp} so warm-ups and cancel rules stay
 * consistent. Holds the only timer state for pending warm-ups — one per player. Callers route
 * messaging through {@link Callback}; this class never sends chat itself.
 */
public final class DelayedTeleport implements Listener {

  private final Scheduler scheduler;
  private final TeleportService teleport;
  private final Map<UUID, Pending> pending = new ConcurrentHashMap<>();

  public DelayedTeleport(Scheduler scheduler, TeleportService teleport) {
    this.scheduler = Objects.requireNonNull(scheduler, "scheduler");
    this.teleport = Objects.requireNonNull(teleport, "teleport");
  }

  /**
   * Schedules a teleport to {@code destination} after {@code delay}. A {@link Duration#isZero()
   * zero or negative delay} teleports immediately. Any previous pending teleport for the same
   * player is cancelled silently first.
   */
  public void schedule(Player player, Location destination, Duration delay, Callback callback) {
    Objects.requireNonNull(player, "player");
    Objects.requireNonNull(destination, "destination");
    Objects.requireNonNull(delay, "delay");
    Objects.requireNonNull(callback, "callback");

    var uuid = player.getUniqueId();
    cancelSilently(uuid);

    if (delay.isZero() || delay.isNegative()) {
      callback.onScheduled(0);
      complete(player, destination, callback);
      return;
    }

    callback.onScheduled(Math.max(1, delay.toSeconds()));
    var task = scheduler.runLater(() -> complete(player, destination, callback), delay);
    pending.put(uuid, new Pending(task, callback));
  }

  /** Whether {@code player} has a pending delayed teleport. */
  public boolean isPending(UUID player) {
    return pending.containsKey(player);
  }

  private void cancelSilently(UUID player) {
    var p = pending.remove(player);
    if (p != null) {
      p.task.cancel();
    }
  }

  private void complete(Player player, Location destination, Callback callback) {
    pending.remove(player.getUniqueId());
    if (!player.isOnline()) {
      callback.onCancelled();
      return;
    }
    if (teleport.teleportTo(player, destination)) {
      callback.onSuccess();
    } else {
      callback.onFailed();
    }
  }

  @EventHandler
  public void onMove(PlayerMoveEvent event) {
    var uuid = event.getPlayer().getUniqueId();
    var p = pending.get(uuid);
    if (p == null) {
      return;
    }
    var from = event.getFrom();
    var to = event.getTo();
    if (from.getBlockX() == to.getBlockX()
        && from.getBlockY() == to.getBlockY()
        && from.getBlockZ() == to.getBlockZ()) {
      return;
    }
    fireCancelled(uuid);
  }

  @EventHandler
  public void onDamage(EntityDamageEvent event) {
    if (event.getEntity() instanceof Player player) {
      fireCancelled(player.getUniqueId());
    }
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent event) {
    cancelSilently(event.getPlayer().getUniqueId());
  }

  private void fireCancelled(UUID player) {
    var p = pending.remove(player);
    if (p != null) {
      p.task.cancel();
      p.callback.onCancelled();
    }
  }

  private record Pending(Task task, Callback callback) {}

  /** Lifecycle hooks of a delayed teleport. All callbacks run on the server thread. */
  public interface Callback {

    /** Called immediately. {@code seconds} is 0 when no delay was applied. */
    default void onScheduled(long seconds) {}

    /** Called after the teleport succeeded. */
    default void onSuccess() {}

    /** Called when the warm-up was cancelled (movement, damage, disconnect). */
    default void onCancelled() {}

    /** Called when the teleport API call itself returned false. */
    default void onFailed() {}
  }
}
