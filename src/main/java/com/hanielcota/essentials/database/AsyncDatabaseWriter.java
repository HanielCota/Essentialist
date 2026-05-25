package com.hanielcota.essentials.database;

import java.util.concurrent.CompletableFuture;
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
   * @return a {@link CompletableFuture} that completes when {@code work} finishes; completes
   *     exceptionally with the {@code work}'s thrown {@link RuntimeException}, or with a {@link
   *     java.util.concurrent.RejectedExecutionException} if the executor refused the task
   *     (typically because the writer is shutting down). Fire-and-forget callers can ignore the
   *     return value; callers that need to chain follow-up work (e.g. surface a message after the
   *     persist resolves) should attach to it via {@code .thenRun} / {@code .whenComplete}.
   */
  CompletableFuture<Void> submit(@NonNull String operation, @NonNull Runnable work);

  @Override
  void close();
}
