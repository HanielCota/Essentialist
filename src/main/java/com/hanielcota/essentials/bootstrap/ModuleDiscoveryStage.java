package com.hanielcota.essentials.bootstrap;

import com.hanielcota.essentials.EssentialsPlugin;
import com.hanielcota.essentials.core.ShutdownRegistry;
import com.hanielcota.essentials.core.ShutdownStep;
import com.hanielcota.essentials.module.Module;
import com.hanielcota.essentials.module.control.ModuleControl;
import com.hanielcota.essentials.module.discovery.ModuleFilter;
import com.hanielcota.essentials.module.discovery.ModuleSettings;
import com.hanielcota.essentials.module.registration.ModuleManager;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Discovers modules via {@link ServiceLoader}, filters out anything disabled in {@code
 * modules.yml}, and publishes the resulting {@link ModuleManager} as a service.
 */
@RequiredArgsConstructor
final class ModuleDiscoveryStage implements BootstrapStage {

  private final EssentialsPlugin plugin;

  @Override
  public String name() {
    return "module-discovery";
  }

  @Override
  public void start(@NonNull StageContext context) {
    var dataFolder = this.plugin.getDataFolder().toPath();
    var modules = ModuleManager.createDefault();
    var classLoader = getClass().getClassLoader();
    var loader = ServiceLoader.load(Module.class, classLoader);
    var discovered = new ArrayList<Module>();

    loader.forEach(discovered::add);

    var settingsLoader = new ModuleSettingsLoader(dataFolder);
    var settings = settingsLoader.load(discovered);
    var enabledModules = ModuleFilter.enabled(discovered, settings);

    enabledModules.forEach(modules::register);

    var control = buildControl(dataFolder, discovered, enabledModules, settings, context);

    context.services().register(ModuleManager.class, modules);
    context.services().register(ModuleControl.class, control);
  }

  private static ModuleControl buildControl(
      @NonNull Path dataFolder,
      @NonNull List<Module> discovered,
      @NonNull List<Module> enabledModules,
      @NonNull ModuleSettings settings,
      @NonNull StageContext context) {
    var allIds = discovered.stream().map(Module::id).sorted().toList();
    var runningIds =
        enabledModules.stream().map(Module::id).collect(Collectors.toUnmodifiableSet());
    var settingsFile = dataFolder.resolve("modules.yml");
    var ioExecutor = newIoExecutor();

    var shutdownRegistry = context.services().resolve(ShutdownRegistry.class);
    shutdownRegistry.register(ShutdownStep.of("ModuleControl-IO", ioExecutor::shutdown));

    return new ModuleControl(settingsFile, allIds, runningIds, settings.modules(), ioExecutor);
  }

  // Daemon-backed so module.yml writes leave the main thread without needing an explicit shutdown
  // hook; toggles are rare and serialized through this single thread, preserving write order.
  private static ExecutorService newIoExecutor() {
    return Executors.newSingleThreadExecutor(ModuleDiscoveryStage::ioThread);
  }

  private static Thread ioThread(@NonNull Runnable runnable) {
    var thread = new Thread(runnable, "essentials-module-control-io");
    thread.setDaemon(true);

    return thread;
  }
}
