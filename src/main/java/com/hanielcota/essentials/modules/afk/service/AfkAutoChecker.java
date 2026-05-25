package com.hanielcota.essentials.modules.afk.service;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.afk.config.AfkConfig;
import com.hanielcota.essentials.paper.PlayerProvider;
import com.hanielcota.essentials.scheduler.Scheduler;
import com.hanielcota.essentials.scheduler.Task;
import java.time.Duration;
import lombok.RequiredArgsConstructor;

/**
 * Periodically scans online players and flags those that have been idle past the configured
 * threshold. The sweep runs sync — it only reads player presence and AFK state, no entity work.
 *
 * <p>One shared timer sweeps the whole roster; we never schedule per-player tasks.
 */
@RequiredArgsConstructor
public final class AfkAutoChecker {

  private static final Duration INTERVAL = Duration.ofSeconds(5);

  private final ConfigHandle<AfkConfig> config;
  private final AfkService service;
  private final AfkBroadcaster broadcaster;
  private final PlayerProvider players;
  private final Scheduler scheduler;

  private Task task;

  public void start() {
    this.task = this.scheduler.runTimer(this::sweep, INTERVAL, INTERVAL);
  }

  public void stop() {
    if (this.task == null) {
      return;
    }

    this.task.cancel();
    this.task = null;
  }

  private void sweep() {
    var snap = this.config.value();
    var thresholdSeconds = snap.idleThresholdSeconds();
    if (thresholdSeconds <= 0) {
      return;
    }

    var thresholdMillis = thresholdSeconds * 1000L;
    var now = System.currentTimeMillis();

    for (var player : this.players.all()) {
      var id = player.getUniqueId();
      if (this.service.isAfk(id)) {
        continue;
      }

      var lastActivity = this.service.lastActivity(id, now);
      var idleMillis = now - lastActivity;
      if (idleMillis < thresholdMillis) {
        continue;
      }

      var transitioned = this.service.enter(id, null);
      if (transitioned) {
        var name = player.getName();
        this.broadcaster.broadcastEnter(name, null);
      }
    }
  }
}
