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
      // Noop is "never scheduled", not "scheduled then cancelled". Real handles return false until
      // cancel() is called; honoring that postcondition prevents callers branching on
      // !task.isCancelled() from silently skipping work that was supposed to be a no-op.
      return false;
    }
  }
}
