package com.hanielcota.essentials.scheduler;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import java.util.Objects;

/** Adapts Paper's {@link ScheduledTask} to the {@link Task} contract. */
record ScheduledTaskHandle(ScheduledTask handle) implements Task {

  ScheduledTaskHandle {
    Objects.requireNonNull(handle, "handle");
  }

  @Override
  public void cancel() {
    handle.cancel();
  }

  @Override
  public boolean isCancelled() {
    return handle.isCancelled();
  }
}
