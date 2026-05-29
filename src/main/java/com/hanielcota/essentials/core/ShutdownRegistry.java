package com.hanielcota.essentials.core;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.NonNull;

/**
 * Ordered registry of {@link ShutdownStep}s populated by bootstrap stages as they finish wiring
 * their infrastructure. {@link CoreLifecycle#shutdown()} walks this list in reverse, so the most
 * recently registered step tears down first.
 *
 * <p>Bootstrap rollback consumes the same registry, ensuring there is exactly one place that knows
 * the teardown order.
 */
public final class ShutdownRegistry {

  private final List<ShutdownStep> steps = new CopyOnWriteArrayList<>();

  public void register(@NonNull ShutdownStep step) {
    this.steps.add(step);
  }

  public List<ShutdownStep> steps() {
    return Collections.unmodifiableList(this.steps);
  }
}
