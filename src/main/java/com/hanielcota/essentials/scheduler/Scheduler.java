package com.hanielcota.essentials.scheduler;

import java.time.Duration;
import java.util.concurrent.Executor;
import lombok.NonNull;
import org.bukkit.entity.Entity;

public interface Scheduler {

  void runSync(@NonNull Runnable task);

  /**
   * Executor that hops back to the main (global region) thread. Use it as the second arg of {@link
   * java.util.concurrent.CompletableFuture#thenAcceptAsync(java.util.function.Consumer, Executor)}
   * and friends when a callback chained off an async future must touch Bukkit API that is not
   * scoped to a single entity (sending messages to a different player, closing menus, calling
   * services that read multiple entities).
   *
   * <p>For callbacks that only touch a single entity, prefer scheduling on that entity via {@link
   * #runOnEntity(Entity, Runnable)} — under Folia the entity's region is more local than the global
   * one.
   */
  Executor mainExecutor();

  /**
   * Runs {@code task} on {@code entity}'s region next tick. On Folia an entity's state (inventory,
   * position, ...) may only be touched from its owning region thread; this routes the task there.
   * The task is silently dropped if the entity is removed before it runs.
   */
  void runOnEntity(@NonNull Entity entity, @NonNull Runnable task);

  Task runOnEntityLater(@NonNull Entity entity, @NonNull Runnable task, @NonNull Duration delay);

  Task runLater(@NonNull Runnable task, @NonNull Duration delay);

  Task runTimer(@NonNull Runnable task, @NonNull Duration initialDelay, @NonNull Duration period);
}
