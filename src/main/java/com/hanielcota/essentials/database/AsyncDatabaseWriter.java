package com.hanielcota.essentials.database;

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
   */
  void submit(String operation, Runnable work);

  @Override
  void close();
}
