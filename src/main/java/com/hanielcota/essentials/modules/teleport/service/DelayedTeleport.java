package com.hanielcota.essentials.modules.teleport.service;

import com.hanielcota.essentials.scheduler.Scheduler;
import java.time.Duration;
import java.util.UUID;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Teleports a player after a configurable delay, cancelling on damage or disconnect.
 *
 * <p>Used by {@code /spawn}, {@code /home} and {@code /warp} so warm-ups and cancel rules stay
 * consistent. Holds the only timer state for pending warm-ups â€” one per player. Callers route
 * messaging through {@link Callback}; this class never sends chat itself.
 */
public final class DelayedTeleport {

  private final Scheduler scheduler;
  private final PendingTeleports pending;
  private final TeleportExecutor executor;

  public DelayedTeleport(@NonNull Scheduler scheduler) {
    this.scheduler = scheduler;
    this.pending = new PendingTeleports();
    this.executor = new TeleportExecutor();
  }

  /**
   * Schedules a teleport to {@code destination} after {@code delay}. A {@link Duration#isZero()
   * zero or negative delay} teleports immediately. Any previous pending teleport for the same
   * player is cancelled silently first.
   */
  public void schedule(
      @NonNull Player player,
      @NonNull Location destination,
      @NonNull Duration delay,
      @NonNull Callback callback) {

    var uuid = player.getUniqueId();
    cancel(uuid);

    if (delay.isZero() || delay.isNegative()) {
      callback.onScheduled(0);
      this.scheduler.runOnEntity(
          player, () -> this.executor.teleport(player, destination, callback));
      return;
    }

    callback.onScheduled(Math.max(1, delay.toSeconds()));
    var task = this.scheduler.runLater(() -> finishWarmup(player, destination, callback), delay);
    this.pending.put(uuid, task, callback);
  }

  /**
   * Silently drops the pending teleport for {@code player} — no callback fires. Public so
   * per-module listeners (e.g. {@code HomesSessionCleanupListener}) can own their own cancel rules
   * in addition to the shared damage/quit handling done here.
   */
  public void cancel(@NonNull UUID player) {
    this.pending.cancelSilently(player);
  }

  private void finishWarmup(
      @NonNull Player player, @NonNull Location destination, @NonNull Callback callback) {
    var removed = this.pending.remove(player.getUniqueId());
    if (removed == null || !removed.owns(callback)) {
      return;
    }
    this.scheduler.runOnEntity(player, () -> this.executor.teleport(player, destination, callback));
  }

  /**
   * Atomically drops the pending teleport for {@code player} and fires {@code onCancelled} on its
   * callback. Returns {@code true} when a warm-up was actually cancelled, {@code false} when there
   * was nothing to cancel — callers use the return value to decide which feedback to show without a
   * separate "is-pending" check (which would race against warm-up completion).
   */
  public boolean cancelAndNotify(@NonNull UUID player) {
    var pendingTeleport = this.pending.remove(player);
    if (pendingTeleport == null) {
      return false;
    }
    pendingTeleport.cancelTask();
    pendingTeleport.callback().onCancelled();
    return true;
  }

  /**
   * Lifecycle hooks of a delayed teleport. Completion callbacks run on the player's owning thread.
   */
  public interface Callback {

    /** Called immediately. {@code seconds} is 0 when no delay was applied. */
    default void onScheduled(long seconds) {}

    /** Called after the teleport succeeded. */
    default void onSuccess() {}

    /** Called when the warm-up was cancelled by damage or disconnect. */
    default void onCancelled() {}

    /** Called when the teleport API call itself returned false. */
    default void onFailed() {}
  }
}
