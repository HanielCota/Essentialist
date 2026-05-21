package com.hanielcota.essentials.scheduler;

import java.time.Duration;

public interface Scheduler {

  void runSync(Runnable task);

  void runAsync(Runnable task);

  Task runLater(Runnable task, Duration delay);

  Task runTimer(Runnable task, Duration initialDelay, Duration period);

  interface Task {
    void cancel();

    boolean isCancelled();
  }
}
