package com.hanielcota.essentials.module.registration;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.EssentialsMenu;
import java.util.function.Supplier;
import lombok.NonNull;
import org.bukkit.event.Listener;

/**
 * Write-side counterpart to {@link ModuleEnvironment}. Modules use this during {@link
 * AbstractModule#onEnable(ModuleEnvironment, ModuleRegistrar)} to publish their collaborators —
 * listeners, commands, menus, services — and to enqueue disposers that run on disable.
 *
 * <p>Splitting this from the environment makes a module's enable phase the only place that mutates
 * shared state: tests can spy on a registrar to assert what a module registered without standing up
 * the full plugin lifecycle.
 */
public interface ModuleRegistrar {

  /** Registers a Bukkit event listener and remembers it so it is unregistered on disable. */
  void listener(@NonNull Listener listener);

  /** Registers an annotated command handler with the framework. */
  void command(@NonNull Object handler);

  /** Registers a menu and queues its un-registration on disable. */
  void menu(@NonNull EssentialsMenu menu);

  /** Adds an {@link AutoCloseable} that runs on module disable, in registration order. */
  void closeable(@NonNull AutoCloseable closeable);

  /** Publishes {@code instance} under {@code type} for other modules to resolve. */
  <T> void provide(@NonNull Class<T> type, @NonNull T instance);

  /**
   * Loads a config and registers {@code service} under its concrete runtime class in one call.
   * Convenience overload for the common case where the service has no public interface and callers
   * look it up as {@code service(ConcreteService.class)}.
   */
  <C, S> ConfigHandle<C> configure(
      @NonNull String name,
      @NonNull Class<C> configType,
      @NonNull Supplier<C> defaults,
      @NonNull S service);
}
