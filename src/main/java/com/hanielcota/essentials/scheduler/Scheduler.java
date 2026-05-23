package com.hanielcota.essentials.scheduler;

import java.time.Duration;
import lombok.NonNull;
import org.bukkit.entity.Entity;

public interface Scheduler {

  void runSync(@NonNull Runnable task);

  void runAsync(@NonNull Runnable task);

  /**
   * Runs {@code task} on {@code entity}'s region next tick. On Folia an entity's state (inventory,
   * position, ...) may only be touched from its owning region thread; this routes the task there.
   * The task is silently dropped if the entity is removed before it runs.
   */
  void runOnEntity(@NonNull Entity entity, @NonNull Runnable task);

  Task runOnEntityLater(@NonNull Entity entity, @NonNull Runnable task, @NonNull Duration delay);

  Task runLater(@NonNull Runnable task, @NonNull Duration delay);

  Task runTimer(@NonNull Runnable task, @NonNull Duration initialDelay, @NonNull Duration period);

  /**
   * Runs {@code task} asynchronously after {@code delay}. Unlike {@link #runLater}, the delay is
   * wall-clock, not tick-bound — appropriate for I/O work (DB, HTTP) that must not touch a region
   * thread.
   */
  Task runAsyncLater(@NonNull Runnable task, @NonNull Duration delay);

  /**
   * Runs {@code task} asynchronously every {@code period} after {@code initialDelay}. Wall-clock
   * scheduling, like {@link #runAsyncLater}.
   */
  Task runAsyncTimer(
      @NonNull Runnable task, @NonNull Duration initialDelay, @NonNull Duration period);
}
