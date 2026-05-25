package com.hanielcota.essentials.database;

import com.hanielcota.essentials.util.Log;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
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
 *
 * <p>Saturation is logged at most once every {@value #SATURATION_LOG_INTERVAL_MS} ms when the queue
 * is &geq; {@value #SATURATION_THRESHOLD_PERCENT}% full so a sustained spike produces one warn per
 * second rather than thousands.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DefaultAsyncDatabaseWriter implements AsyncDatabaseWriter {

  private static final Log LOG = Log.of(DefaultAsyncDatabaseWriter.class);
  private static final int DRAIN_TIMEOUT_SECONDS = 5;
  private static final int DEFAULT_QUEUE_CAPACITY = 10_000;
  private static final int SATURATION_THRESHOLD_PERCENT = 80;
  private static final long SATURATION_LOG_INTERVAL_MS = 1_000L;

  private final String threadName;
  private final ThreadPoolExecutor executor;
  private final int queueCapacity;
  private final AtomicLong lastSaturationLogMillis = new AtomicLong();

  public DefaultAsyncDatabaseWriter(@NonNull String threadName) {
    this(threadName, DEFAULT_QUEUE_CAPACITY);
  }

  public DefaultAsyncDatabaseWriter(@NonNull String threadName, int queueCapacity) {
    this(threadName, newBoundedExecutor(threadName, queueCapacity), queueCapacity);
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
    warnIfSaturated();

    return CompletableFuture.runAsync(() -> runSafely(operation, work), this.executor)
        .exceptionally(error -> handleSubmitFailure(operation, error));
  }

  private void warnIfSaturated() {
    var queue = this.executor.getQueue();
    var depth = queue.size();
    var threshold = (this.queueCapacity * SATURATION_THRESHOLD_PERCENT) / 100;
    if (depth < threshold) {
      return;
    }

    var now = System.currentTimeMillis();
    var last = this.lastSaturationLogMillis.get();
    if (now - last < SATURATION_LOG_INTERVAL_MS) {
      return;
    }
    if (!this.lastSaturationLogMillis.compareAndSet(last, now)) {
      return;
    }

    LOG.warn("{} queue saturated: {}/{}", this.threadName, depth, this.queueCapacity);
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
          this.queueCapacity);
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
