package com.hanielcota.essentials.modules.tpa.service;

import com.hanielcota.essentials.scheduler.Scheduler;
import com.hanielcota.essentials.scheduler.Task;
import java.time.Duration;
import java.time.Instant;
import lombok.RequiredArgsConstructor;

/**
 * Drives request expiry.
 *
 * <p>Sole responsibility: once a second, hand every request that has outlived its deadline to
 * {@link TeleportRequestService#expire}. One shared timer sweeps the whole {@link RequestStore} â€”
 * never one delayed task per request â€” so spamming {@code /tpa} cannot pile up scheduler work.
 * Holds no request state of its own.
 */
@RequiredArgsConstructor
public final class TeleportRequestExpiry {

  private static final Duration INTERVAL = Duration.ofSeconds(1);

  private final Scheduler scheduler;
  private final RequestStore store;
  private final TeleportRequestService service;

  private Task task;

  /** Starts the periodic sweep. */
  public void start() {
    task = scheduler.runTimer(this::sweep, INTERVAL, INTERVAL);
  }

  /** Stops the periodic sweep. Pending requests are simply dropped on shutdown. */
  public void stop() {
    if (task != null) {
      task.cancel();
      task = null;
    }
  }

  private void sweep() {
    var now = Instant.now();
    for (var request : store.expiredAt(now)) {
      service.expire(request);
    }
  }
}
