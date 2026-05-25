package com.hanielcota.essentials.scheduler;

import com.hanielcota.essentials.util.Log;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Hops a {@link CompletableFuture} back to the main (global region) thread before invoking the
 * caller's consumer, applies a default timeout so a stuck future does not hang user-visible state
 * (menus, command feedback) forever, and logs uncaught exceptions instead of letting them die on
 * the future.
 *
 * <p>Required for every async future returned by Bukkit (e.g. {@code player.teleportAsync(...)})
 * whose continuation touches Bukkit API for an entity other than the original or for cross-entity
 * state (sender messages, menus, services that read multiple entities). Without it the callback
 * runs on the entity's region completion thread, which under Folia is illegal for non-owned API.
 */
@RequiredArgsConstructor
public final class MainThreadCallbacks {

  private static final Log LOG = Log.of(MainThreadCallbacks.class);
  private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(30);

  private final Scheduler scheduler;

  public <T> void hop(@NonNull CompletableFuture<T> future, @NonNull Consumer<T> onValue) {
    hop(future, onValue, "async callback");
  }

  public <T> void hop(
      @NonNull CompletableFuture<T> future,
      @NonNull Consumer<T> onValue,
      @NonNull String operation) {
    hop(future, onValue, DEFAULT_TIMEOUT, operation);
  }

  public <T> void hop(
      @NonNull CompletableFuture<T> future,
      @NonNull Consumer<T> onValue,
      @NonNull Duration timeout,
      @NonNull String operation) {
    var timeoutMillis = timeout.toMillis();
    var unit = TimeUnit.MILLISECONDS;
    var executor = this.scheduler.mainExecutor();

    future
        .orTimeout(timeoutMillis, unit)
        .thenAcceptAsync(onValue, executor)
        .exceptionally(error -> logFailure(operation, error));
  }

  private Void logFailure(@NonNull String operation, @NonNull Throwable error) {
    LOG.warn(error, "{} failed", operation);
    return null;
  }
}
