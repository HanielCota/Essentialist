package com.hanielcota.essentials.module;

import com.hanielcota.essentials.module.environment.ModuleContext;
import lombok.NonNull;

/**
 * A self-contained feature unit. Modules are discovered, ordered by their declared dependencies,
 * and enabled once at boot on the main thread; they are disabled in reverse order on shutdown. Most
 * features extend {@link AbstractModule} rather than implementing this interface directly.
 *
 * <p>Implementations must be re-runnable across an {@code enable}/{@code disable} cycle: {@code
 * disable} has to release everything {@code enable} acquired so the module can be enabled again
 * cleanly.
 */
public interface Module {

  /** Identity, version and dependency declaration used to order and report on the module. */
  ModuleMetadata metadata();

  /** Convenience shortcut for {@code metadata().id()}. */
  default String id() {
    return metadata().id();
  }

  /**
   * Brings the module online: wires collaborators and publishes listeners, commands, menus and
   * services. Called once, on the main thread, after every declared dependency is already enabled.
   *
   * @throws RuntimeException to abort enabling; the lifecycle marks the module {@code FAILED} and
   *     rolls back the modules enabled before it.
   */
  void enable(@NonNull ModuleContext context);

  /** Tears down everything {@link #enable(ModuleContext)} registered. Must not throw. */
  void disable();
}
