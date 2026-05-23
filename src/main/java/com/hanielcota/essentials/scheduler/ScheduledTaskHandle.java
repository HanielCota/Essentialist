package com.hanielcota.essentials.scheduler;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;

/** Adapts Paper's {@link ScheduledTask} to the {@link Task} contract. */
record ScheduledTaskHandle(ScheduledTask handle) implements Task {

  @Override
  public void cancel() {
    this.handle.cancel();
  }

  @Override
  public boolean isCancelled() {
    return this.handle.isCancelled();
  }
}
