package com.hanielcota.essentials.scheduler;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import lombok.NonNull;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

public record PaperScheduler(JavaPlugin plugin) implements Scheduler {

  public PaperScheduler(@NonNull JavaPlugin plugin) {
    this.plugin = plugin;
  }

  private static Consumer<ScheduledTask> adapt(@NonNull Runnable task) {
    return scheduled -> task.run();
  }

  @Override
  public void runSync(@NonNull Runnable task) {
    var server = plugin.getServer();
    var scheduler = server.getGlobalRegionScheduler();
    var adaptedTask = adapt(task);

    scheduler.run(plugin, adaptedTask);
  }

  @Override
  public void runAsync(@NonNull Runnable task) {
    var server = plugin.getServer();
    var scheduler = server.getAsyncScheduler();
    var adaptedTask = adapt(task);

    scheduler.runNow(plugin, adaptedTask);
  }

  @Override
  public void runOnEntity(@NonNull Entity entity, @NonNull Runnable task) {
    var scheduler = entity.getScheduler();
    var adaptedTask = adapt(task);

    scheduler.run(plugin, adaptedTask, null);
  }

  @Override
  public Task runOnEntityLater(
      @NonNull Entity entity, @NonNull Runnable task, @NonNull Duration delay) {
    var scheduler = entity.getScheduler();
    var adaptedTask = adapt(task);
    var ticksDelay = Ticks.fromDuration(delay);

    var handle = scheduler.runDelayed(plugin, adaptedTask, null, ticksDelay);
    if (handle == null) {
      return Task.noop();
    }

    return new ScheduledTaskHandle(handle);
  }

  @Override
  public Task runLater(@NonNull Runnable task, @NonNull Duration delay) {
    var server = plugin.getServer();
    var scheduler = server.getGlobalRegionScheduler();
    var adaptedTask = adapt(task);
    var ticksDelay = Ticks.fromDuration(delay);

    var handle = scheduler.runDelayed(plugin, adaptedTask, ticksDelay);
    return new ScheduledTaskHandle(handle);
  }

  @Override
  public Task runTimer(
      @NonNull Runnable task, @NonNull Duration initialDelay, @NonNull Duration period) {
    var server = plugin.getServer();
    var scheduler = server.getGlobalRegionScheduler();
    var adaptedTask = adapt(task);

    var ticksInitialDelay = Ticks.fromDuration(initialDelay);
    var ticksPeriod = Ticks.fromDuration(period);

    var handle = scheduler.runAtFixedRate(plugin, adaptedTask, ticksInitialDelay, ticksPeriod);
    return new ScheduledTaskHandle(handle);
  }

  @Override
  public Task runAsyncLater(@NonNull Runnable task, @NonNull Duration delay) {
    var server = plugin.getServer();
    var scheduler = server.getAsyncScheduler();
    var adaptedTask = adapt(task);

    var millisDelay = Math.max(0L, delay.toMillis());
    var timeUnit = TimeUnit.MILLISECONDS;

    var handle = scheduler.runDelayed(plugin, adaptedTask, millisDelay, timeUnit);
    return new ScheduledTaskHandle(handle);
  }

  @Override
  public Task runAsyncTimer(
      @NonNull Runnable task, @NonNull Duration initialDelay, @NonNull Duration period) {
    var server = plugin.getServer();
    var scheduler = server.getAsyncScheduler();
    var adaptedTask = adapt(task);

    var millisInitialDelay = Math.max(0L, initialDelay.toMillis());
    var millisPeriod = Math.max(1L, period.toMillis());
    var timeUnit = TimeUnit.MILLISECONDS;

    var handle =
        scheduler.runAtFixedRate(plugin, adaptedTask, millisInitialDelay, millisPeriod, timeUnit);
    return new ScheduledTaskHandle(handle);
  }
}
