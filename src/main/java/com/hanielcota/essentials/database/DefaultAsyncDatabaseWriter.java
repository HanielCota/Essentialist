package com.hanielcota.essentials.database;

import com.hanielcota.essentials.util.Log;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Default implementation of {@link AsyncDatabaseWriter} that submits tasks to a single-threaded
 * background executor.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DefaultAsyncDatabaseWriter implements AsyncDatabaseWriter {

  private static final Log LOG = Log.of(DefaultAsyncDatabaseWriter.class);
  private static final int DRAIN_TIMEOUT_SECONDS = 5;

  private final String threadName;
  private final ExecutorService executor;

  public DefaultAsyncDatabaseWriter(@NonNull String threadName) {
    this(threadName, newDaemonExecutor(threadName));
  }

  public static DefaultAsyncDatabaseWriter of(
      @NonNull String threadName, @NonNull ExecutorService executor) {
    return new DefaultAsyncDatabaseWriter(threadName, executor);
  }

  private static ExecutorService newDaemonExecutor(@NonNull String threadName) {
    ThreadFactory threadFactory = runnable -> createDaemonThread(runnable, threadName);
    return Executors.newSingleThreadExecutor(threadFactory);
  }

  private static Thread createDaemonThread(@NonNull Runnable runnable, @NonNull String threadName) {
    var thread = new Thread(runnable, threadName);
    thread.setDaemon(true);

    return thread;
  }

  @Override
  public CompletableFuture<Void> submit(@NonNull String operation, @NonNull Runnable work) {
    return CompletableFuture.runAsync(() -> runSafely(operation, work), this.executor)
        .exceptionally(error -> handleSubmitFailure(operation, error));
  }

  private void runSafely(@NonNull String operation, @NonNull Runnable work) {
    try {
      work.run();
    } catch (RuntimeException e) {
      LOG.warn(e, "{} async {} failed", this.threadName, operation);
      throw e;
    }
  }

  private Void handleSubmitFailure(@NonNull String operation, @NonNull Throwable error) {
    if (error instanceof RejectedExecutionException) {
      LOG.warn("{} rejected {} (shutting down?)", this.threadName, operation);
      return null;
    }
    // Other failures already logged in runSafely; rethrow so callers that observe the future see
    // the cause.
    if (error instanceof RuntimeException runtime) {
      throw runtime;
    }
    throw new IllegalStateException(error);
  }

  @Override
  public void close() {
    this.executor.shutdown();

    try {
      var timeout = DRAIN_TIMEOUT_SECONDS;
      var unit = TimeUnit.SECONDS;

      if (this.executor.awaitTermination(timeout, unit)) {
        return;
      }

      var droppedTasks = this.executor.shutdownNow();
      var droppedCount = droppedTasks.size();

      LOG.warn(
          "{} did not drain in {}s; {} write(s) dropped", this.threadName, timeout, droppedCount);

    } catch (InterruptedException _) {
      this.executor.shutdownNow();

      var currentThread = Thread.currentThread();
      currentThread.interrupt();
    }
  }
}
