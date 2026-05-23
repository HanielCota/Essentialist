package com.hanielcota.essentials.scheduler;

import java.time.Duration;
import org.bukkit.entity.Entity;

public interface Scheduler {

  void runSync(Runnable task);

  void runAsync(Runnable task);

  /**
   * Runs {@code task} on {@code entity}'s region next tick. On Folia an entity's state (inventory,
   * position, ...) may only be touched from its owning region thread; this routes the task there.
   * The task is silently dropped if the entity is removed before it runs.
   */
  void runOnEntity(Entity entity, Runnable task);

  Task runOnEntityLater(Entity entity, Runnable task, Duration delay);

  Task runLater(Runnable task, Duration delay);

  Task runTimer(Runnable task, Duration initialDelay, Duration period);

  /**
   * Runs {@code task} asynchronously after {@code delay}. Unlike {@link #runLater}, the delay is
   * wall-clock, not tick-bound — appropriate for I/O work (DB, HTTP) that must not touch a region
   * thread.
   */
  Task runAsyncLater(Runnable task, Duration delay);

  /**
   * Runs {@code task} asynchronously every {@code period} after {@code initialDelay}. Wall-clock
   * scheduling, like {@link #runAsyncLater}.
   */
  Task runAsyncTimer(Runnable task, Duration initialDelay, Duration period);
}
