package com.hanielcota.essentials.modules.homes.rename;

import com.hanielcota.essentials.scheduler.Scheduler;
import com.hanielcota.essentials.scheduler.Task;
import java.time.Duration;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/**
 * Schedules the rename-prompt timeout on the player's owning thread. A zero or negative timeout
 * yields a {@link Task#noop()} so the session never expires, letting the chat listener cancel the
 * session instead.
 */
@RequiredArgsConstructor
public final class HomeRenameTimer {

  private final Scheduler scheduler;

  public Task schedule(
      @NonNull Player player, @NonNull Duration timeout, @NonNull Runnable onTimeout) {
    var disabled = timeout.isZero() || timeout.isNegative();
    if (disabled) {
      return Task.noop();
    }

    return this.scheduler.runOnEntityLater(player, onTimeout, timeout);
  }
}
