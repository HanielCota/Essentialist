package com.hanielcota.essentials.scheduler;

/** Handle for a scheduled task that can be cancelled. */
public interface Task {

  static Task noop() {
    return NoopTask.INSTANCE;
  }

  void cancel();

  boolean isCancelled();

  enum NoopTask implements Task {
    INSTANCE;

    @Override
    public void cancel() {}

    @Override
    public boolean isCancelled() {
      return true;
    }
  }
}
