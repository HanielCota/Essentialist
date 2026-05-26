package com.hanielcota.essentials.scheduler;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import lombok.NonNull;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

public record PaperScheduler(@NonNull JavaPlugin plugin) implements Scheduler {

  private static Consumer<ScheduledTask> adapt(@NonNull Runnable task) {
    return scheduled -> task.run();
  }

  @Override
  public void runSync(@NonNull Runnable task) {
    var server = this.plugin.getServer();
    var scheduler = server.getGlobalRegionScheduler();
    var adaptedTask = adapt(task);

    scheduler.run(this.plugin, adaptedTask);
  }

  @Override
  public Executor mainExecutor() {
    return this::runSync;
  }

  @Override
  public void runOnEntity(@NonNull Entity entity, @NonNull Runnable task) {
    var scheduler = entity.getScheduler();
    var adaptedTask = adapt(task);

    scheduler.run(this.plugin, adaptedTask, null);
  }

  @Override
  public Task runOnEntityLater(
      @NonNull Entity entity, @NonNull Runnable task, @NonNull Duration delay) {
    var scheduler = entity.getScheduler();
    var adaptedTask = adapt(task);
    var ticksDelay = Ticks.fromDuration(delay);

    var handle = scheduler.runDelayed(this.plugin, adaptedTask, null, ticksDelay);
    if (handle == null) {
      return Task.noop();
    }

    return new ScheduledTaskHandle(handle);
  }

  @Override
  public Task runLater(@NonNull Runnable task, @NonNull Duration delay) {
    var server = this.plugin.getServer();
    var scheduler = server.getGlobalRegionScheduler();
    var adaptedTask = adapt(task);
    var ticksDelay = Ticks.fromDuration(delay);

    var handle = scheduler.runDelayed(this.plugin, adaptedTask, ticksDelay);
    if (handle == null) {
      return Task.noop();
    }
    return new ScheduledTaskHandle(handle);
  }

  @Override
  public Task runTimer(
      @NonNull Runnable task, @NonNull Duration initialDelay, @NonNull Duration period) {
    var server = this.plugin.getServer();
    var scheduler = server.getGlobalRegionScheduler();
    var adaptedTask = adapt(task);

    var ticksInitialDelay = Ticks.fromDuration(initialDelay);
    var ticksPeriod = Ticks.fromDuration(period);

    var handle = scheduler.runAtFixedRate(this.plugin, adaptedTask, ticksInitialDelay, ticksPeriod);
    if (handle == null) {
      return Task.noop();
    }
    return new ScheduledTaskHandle(handle);
  }
}
