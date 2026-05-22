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

  Task runLater(Runnable task, Duration delay);

  Task runTimer(Runnable task, Duration initialDelay, Duration period);

  interface Task {
    void cancel();

    boolean isCancelled();
  }
}
