package com.hanielcota.essentials.scheduler;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

public record PaperScheduler(JavaPlugin plugin) implements Scheduler {

  public PaperScheduler {
    Objects.requireNonNull(plugin, "plugin");
  }

  @Override
  public void runSync(Runnable task) {
    Objects.requireNonNull(task, "task");

    plugin.getServer().getGlobalRegionScheduler().run(plugin, adapt(task));
  }

  @Override
  public void runAsync(Runnable task) {
    Objects.requireNonNull(task, "task");

    plugin.getServer().getAsyncScheduler().runNow(plugin, adapt(task));
  }

  @Override
  public void runOnEntity(Entity entity, Runnable task) {
    Objects.requireNonNull(entity, "entity");
    Objects.requireNonNull(task, "task");

    entity.getScheduler().run(plugin, adapt(task), null);
  }

  @Override
  public Task runLater(Runnable task, Duration delay) {
    Objects.requireNonNull(task, "task");
    Objects.requireNonNull(delay, "delay");

    var handle =
        plugin
            .getServer()
            .getGlobalRegionScheduler()
            .runDelayed(plugin, adapt(task), Ticks.fromDuration(delay));
    return new ScheduledTaskHandle(handle);
  }

  @Override
  public Task runTimer(Runnable task, Duration initialDelay, Duration period) {
    Objects.requireNonNull(task, "task");
    Objects.requireNonNull(initialDelay, "initialDelay");
    Objects.requireNonNull(period, "period");

    var handle =
        plugin
            .getServer()
            .getGlobalRegionScheduler()
            .runAtFixedRate(
                plugin, adapt(task), Ticks.fromDuration(initialDelay), Ticks.fromDuration(period));
    return new ScheduledTaskHandle(handle);
  }

  @Override
  public Task runAsyncLater(Runnable task, Duration delay) {
    Objects.requireNonNull(task, "task");
    Objects.requireNonNull(delay, "delay");

    var handle =
        plugin
            .getServer()
            .getAsyncScheduler()
            .runDelayed(plugin, adapt(task), Math.max(0L, delay.toMillis()), TimeUnit.MILLISECONDS);
    return new ScheduledTaskHandle(handle);
  }

  @Override
  public Task runAsyncTimer(Runnable task, Duration initialDelay, Duration period) {
    Objects.requireNonNull(task, "task");
    Objects.requireNonNull(initialDelay, "initialDelay");
    Objects.requireNonNull(period, "period");

    var handle =
        plugin
            .getServer()
            .getAsyncScheduler()
            .runAtFixedRate(
                plugin,
                adapt(task),
                Math.max(0L, initialDelay.toMillis()),
                Math.max(1L, period.toMillis()),
                TimeUnit.MILLISECONDS);
    return new ScheduledTaskHandle(handle);
  }

  private static Consumer<ScheduledTask> adapt(Runnable task) {
    return scheduled -> task.run();
  }
}
