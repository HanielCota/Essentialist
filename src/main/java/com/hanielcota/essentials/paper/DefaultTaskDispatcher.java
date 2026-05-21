package com.hanielcota.essentials.paper;

import com.hanielcota.essentials.scheduler.Scheduler;
import java.util.Objects;

public record DefaultTaskDispatcher(Scheduler scheduler) implements TaskDispatcher {

  public DefaultTaskDispatcher {
    Objects.requireNonNull(scheduler, "scheduler");
  }

  @Override
  public void sync(Runnable task) {
    scheduler.runSync(Objects.requireNonNull(task, "task"));
  }

  @Override
  public void async(Runnable task) {
    scheduler.runAsync(Objects.requireNonNull(task, "task"));
  }
}
