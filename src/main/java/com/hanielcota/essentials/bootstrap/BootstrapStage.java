package com.hanielcota.essentials.bootstrap;

import lombok.NonNull;

/**
 * One step in the plugin enable sequence. The runner ({@link EssentialsBootstrap}) calls every
 * stage's {@link #start} in declared order; any thrown {@link RuntimeException} triggers a global
 * rollback that tears down whatever infrastructure has already been registered (database pool, menu
 * service, enabled modules).
 *
 * <p>Stages communicate by registering services in the shared {@link
 * com.hanielcota.essentials.service.ServiceRegistry}. To extend the bootstrap from an addon,
 * subclass {@link EssentialsBootstrap} and override {@link EssentialsBootstrap#stages} to add or
 * reorder stages.
 */
public interface BootstrapStage {

  /** Stable identifier used in logs and rollback diagnostics. */
  String name();

  /** Runs the stage. Throwing aborts bootstrap and triggers global rollback. */
  void start(@NonNull StageContext context);
}
