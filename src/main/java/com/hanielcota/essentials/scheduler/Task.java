package com.hanielcota.essentials.scheduler;

/** Handle for a scheduled task that can be cancelled. */
public interface Task {

  void cancel();

  boolean isCancelled();
}
