package com.hanielcota.essentials.scheduler;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import java.time.Duration;
import java.util.Objects;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

public record PaperScheduler(JavaPlugin plugin) implements Scheduler {

  public PaperScheduler {
    Objects.requireNonNull(plugin, "plugin");
  }

  private static Task wrap(ScheduledTask handle) {
    return new Task() {
      @Override
      public void cancel() {
        handle.cancel();
      }

      @Override
      public boolean isCancelled() {
        return handle.isCancelled();
      }
    };
  }

  @Override
  public void runSync(Runnable task) {
    plugin.getServer().getGlobalRegionScheduler().run(plugin, t -> task.run());
  }

  @Override
  public void runAsync(Runnable task) {
    plugin.getServer().getAsyncScheduler().runNow(plugin, t -> task.run());
  }

  @Override
  public void runOnEntity(Entity entity, Runnable task) {
    entity.getScheduler().run(plugin, t -> task.run(), null);
  }

  @Override
  public Task runLater(Runnable task, Duration delay) {
    long ticks = Math.max(1L, delay.toMillis() / 50L);
    var handle =
        plugin.getServer().getGlobalRegionScheduler().runDelayed(plugin, t -> task.run(), ticks);
    return wrap(handle);
  }

  @Override
  public Task runTimer(Runnable task, Duration initialDelay, Duration period) {
    long initialTicks = Math.max(1L, initialDelay.toMillis() / 50L);
    long periodTicks = Math.max(1L, period.toMillis() / 50L);
    var scheduler = plugin.getServer().getGlobalRegionScheduler();
    return wrap(scheduler.runAtFixedRate(plugin, t -> task.run(), initialTicks, periodTicks));
  }
}
