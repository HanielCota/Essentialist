package com.hanielcota.essentials.database;

import com.hanielcota.essentials.util.Log;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
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
    this(
        threadName,
        Executors.newSingleThreadExecutor(
            runnable -> {
              var thread = new Thread(runnable, threadName);
              thread.setDaemon(true);
              return thread;
            }));
  }

  public static DefaultAsyncDatabaseWriter of(
      @NonNull String threadName, @NonNull ExecutorService executor) {
    return new DefaultAsyncDatabaseWriter(threadName, executor);
  }

  @Override
  public void submit(@NonNull String operation, @NonNull Runnable work) {
    try {
      executor.execute(
          () -> {
            try {
              work.run();
            } catch (RuntimeException e) {
              LOG.warn(e, "{} async {} failed", threadName, operation);
            }
          });
    } catch (RejectedExecutionException _) {
      LOG.warn("{} rejected {} (shutting down?)", threadName, operation);
    }
  }

  @Override
  public void close() {
    executor.shutdown();

    try {
      var timeout = DRAIN_TIMEOUT_SECONDS;
      var unit = TimeUnit.SECONDS;

      if (executor.awaitTermination(timeout, unit)) {
        return;
      }

      var droppedTasks = executor.shutdownNow();
      var droppedCount = droppedTasks.size();

      LOG.warn("{} did not drain in {}s; {} write(s) dropped", threadName, timeout, droppedCount);

    } catch (InterruptedException _) {
      executor.shutdownNow();

      var currentThread = Thread.currentThread();
      currentThread.interrupt();
    }
  }
}
