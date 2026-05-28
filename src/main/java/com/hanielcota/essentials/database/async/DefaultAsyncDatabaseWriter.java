package com.hanielcota.essentials.database.async;

import com.hanielcota.essentials.shared.Log;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Single-threaded {@link AsyncDatabaseWriter} with a <em>bounded</em> queue. Unbounded queues hide
 * back-pressure — a burst of writes (mass disconnect, restart) keeps the executor accepting work
 * the writer thread cannot drain, growing the heap until OOM. With a cap, submissions past the
 * threshold are rejected at enqueue time and surface via the returned future's {@link
 * RejectedExecutionException}, letting callers decide between dropping the write, retrying, or
 * surfacing the failure to the user.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DefaultAsyncDatabaseWriter implements AsyncDatabaseWriter {

  private static final Log LOG = Log.of(DefaultAsyncDatabaseWriter.class);
  private static final int DRAIN_TIMEOUT_SECONDS = 5;
  private static final int DEFAULT_QUEUE_CAPACITY = 10_000;

  private final String threadName;
  private final ThreadPoolExecutor executor;
  private final QueueSaturationMonitor saturationMonitor;

  public DefaultAsyncDatabaseWriter(@NonNull String threadName) {
    this(threadName, DEFAULT_QUEUE_CAPACITY);
  }

  public DefaultAsyncDatabaseWriter(@NonNull String threadName, int queueCapacity) {
    var executor = newBoundedExecutor(threadName, queueCapacity);
    var monitor = new QueueSaturationMonitor(LOG, queueCapacity);

    this.threadName = threadName;
    this.executor = executor;
    this.saturationMonitor = monitor;
  }

  private static ThreadPoolExecutor newBoundedExecutor(
      @NonNull String threadName, int queueCapacity) {
    ThreadFactory threadFactory = runnable -> createDaemonThread(runnable, threadName);
    var queue = new LinkedBlockingQueue<Runnable>(queueCapacity);
    return new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, queue, threadFactory);
  }

  private static Thread createDaemonThread(@NonNull Runnable runnable, @NonNull String threadName) {
    var thread = new Thread(runnable, threadName);
    thread.setDaemon(true);

    return thread;
  }

  @Override
  public CompletableFuture<Void> submit(@NonNull String operation, @NonNull Runnable work) {
    this.saturationMonitor.check(this.executor.getQueue(), this.threadName);

    try {
      return CompletableFuture.runAsync(() -> runSafely(operation, work), this.executor)
          .exceptionally(error -> handleSubmitFailure(operation, error));
    } catch (RejectedExecutionException e) {
      handleSubmitFailure(operation, e);
      return CompletableFuture.failedFuture(e);
    }
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
      LOG.warn(
          "{} rejected {} (queue full or shutting down, capacity {})",
          this.threadName,
          operation,
          this.executor.getQueue().remainingCapacity() + this.executor.getQueue().size());
      return null;
    }
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
