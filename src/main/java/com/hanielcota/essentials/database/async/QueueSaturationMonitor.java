package com.hanielcota.essentials.database.async;

import com.hanielcota.essentials.shared.Log;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import lombok.NonNull;

/**
 * Rate-limited saturation monitoring for a bounded writer queue. Logs a warning at most once every
 * {@link #LOG_INTERVAL_MS} when the queue is above the configured threshold.
 */
final class QueueSaturationMonitor {

  private static final int DEFAULT_THRESHOLD_PERCENT = 80;
  private static final long LOG_INTERVAL_MS = 1_000L;

  private final Log log;
  private final int capacity;
  private final int threshold;
  private final AtomicLong lastLogMillis = new AtomicLong();

  QueueSaturationMonitor(@NonNull Log log, int capacity) {
    this(log, capacity, DEFAULT_THRESHOLD_PERCENT);
  }

  QueueSaturationMonitor(@NonNull Log log, int capacity, int thresholdPercent) {
    this.log = log;
    this.capacity = capacity;
    this.threshold = (capacity * thresholdPercent) / 100;
  }

  void check(@NonNull BlockingQueue<Runnable> queue, @NonNull String threadName) {
    var depth = queue.size();
    if (depth < this.threshold) {
      return;
    }

    var now = System.currentTimeMillis();
    var last = this.lastLogMillis.get();
    if (now - last < LOG_INTERVAL_MS) {
      return;
    }
    if (!this.lastLogMillis.compareAndSet(last, now)) {
      return;
    }

    this.log.warn("{} queue saturated: {}/{}", threadName, depth, this.capacity);
  }
}
