package com.hanielcota.essentials.database;

import lombok.NonNull;

/**
 * Interface for off-thread execution of database mutations.
 *
 * <p>Separating the interface from the implementation allows callers to depend on the writer
 * abstraction rather than a specific thread configuration.
 */
public interface AsyncDatabaseWriter extends AutoCloseable {

  /**
   * Schedules {@code work} on the writer thread.
   *
   * @param operation the name of the operation for logging context
   * @param work the task to run
   * @return {@code true} if the task was accepted by the executor, {@code false} if it was rejected
   *     (typically because the writer is shutting down). Callers that need at-least-once semantics
   *     for writes should check the return value and decide whether to fall back to a synchronous
   *     write or surface the failure to the user.
   */
  boolean submit(@NonNull String operation, @NonNull Runnable work);

  @Override
  void close();
}
