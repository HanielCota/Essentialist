package com.hanielcota.essentials.bootstrap;

import com.hanielcota.essentials.core.EssentialsCore;
import com.hanielcota.essentials.core.ShutdownRegistry;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Tears down already-registered infrastructure when a bootstrap stage fails. The teardown order is
 * defined exactly once, by the {@link ShutdownRegistry} that each successful stage populates —
 * adding a new ordered service only requires registering a step in that stage, not editing this
 * class.
 */
final class BootstrapRollbackHandler {

  private BootstrapRollbackHandler() {}

  static void rollback(StageContext context, RuntimeException cause) {
    var services = context.services();
    var coreHandle = services.find(EssentialsCore.class);

    if (coreHandle.isPresent()) {
      suppressAndRun(cause, coreHandle.get()::shutdown);
      return;
    }

    var registryHandle = services.find(ShutdownRegistry.class);
    if (registryHandle.isEmpty()) {
      return;
    }

    var steps = new ArrayList<>(registryHandle.get().steps());
    Collections.reverse(steps);
    for (var step : steps) {
      suppressAndRun(cause, step::run);
    }
  }

  private static void suppressAndRun(RuntimeException primary, Runnable cleanup) {
    try {
      cleanup.run();
    } catch (RuntimeException e) {
      primary.addSuppressed(e);
    }
  }
}
