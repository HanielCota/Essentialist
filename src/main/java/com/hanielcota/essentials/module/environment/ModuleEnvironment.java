package com.hanielcota.essentials.module.environment;

import com.hanielcota.essentials.EssentialsPlugin;
import com.hanielcota.essentials.config.ConfigHandle;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.NonNull;

/**
 * Read-only view of the surrounding plugin handed to {@link
 * AbstractModule#onEnable(ModuleEnvironment, ModuleRegistrar)}. Modules look up services and load
 * configs through this interface; collaborators that need to write back (register listeners,
 * commands, menus, services) go through {@link ModuleRegistrar}.
 *
 * <p>Tests substitute a fake implementation to drive the module without spinning up a real Paper
 * server or service registry.
 */
public interface ModuleEnvironment {

  /**
   * The owning {@link EssentialsPlugin}. Kept on the environment because a handful of Bukkit APIs
   * (event registration, scheduler tasks, player visibility) require a {@code JavaPlugin}
   * reference. Modules that only need scheduler / config / DI lookups should prefer the typed
   * accessors and stay {@code plugin()}-free for testability.
   */
  EssentialsPlugin plugin();

  /** Resolves a registered service. Throws {@link IllegalStateException} if absent. */
  <T> T service(@NonNull Class<T> type);

  /** Optional service lookup — empty when no module registered {@code type}. */
  <T> Optional<T> findService(@NonNull Class<T> type);

  /** Loads (or returns the existing handle for) a YAML config under {@code name}. */
  <T> ConfigHandle<T> config(
      @NonNull String name, @NonNull Class<T> type, @NonNull Supplier<T> defaults);
}
