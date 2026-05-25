package com.hanielcota.essentials.bootstrap;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.EssentialsPlugin;
import com.hanielcota.essentials.core.EssentialsCore;
import com.hanielcota.essentials.database.DatabaseProvider;
import com.hanielcota.essentials.service.DefaultServiceRegistry;
import com.hanielcota.essentials.service.ServiceRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;

/**
 * Drives the plugin enable sequence as an ordered list of {@link BootstrapStage}s. Stages share
 * state through the {@link ServiceRegistry}; on failure of any stage every already-registered piece
 * of infrastructure is torn down via a single global rollback (the same shape used before stages
 * existed — see {@link #rollback}).
 *
 * <p>To extend the bootstrap from an addon, subclass and override {@link #stages(EssentialsPlugin)}
 * to insert, reorder or replace stages. The default list is exposed via {@link
 * #defaultStages(EssentialsPlugin)}.
 */
@RequiredArgsConstructor
public class EssentialsBootstrap {

  private final EssentialsPlugin plugin;

  /**
   * Returns the default stage sequence. Centralised so subclasses can call it, prepend their own
   * stages, or build their list from scratch using the same shipped stages.
   */
  public static List<BootstrapStage> defaultStages(EssentialsPlugin plugin) {
    return List.of(
        new CoreServicesBootstrap(plugin),
        new DatabaseBootstrap(plugin),
        new ModuleDiscoveryStage(plugin),
        new CommandSystemBootstrap(plugin),
        new MenuBootstrap(plugin),
        new UserStackBootstrap(plugin),
        new CoreInstanceStage(plugin),
        new CommandMirrorStage(),
        new EnableModulesStage());
  }

  public final EssentialsCore start() {
    var services = new DefaultServiceRegistry();
    var context = new StageContext(this.plugin, services);
    var stages = stages(this.plugin);

    return runStages(stages, context);
  }

  /** Override in a subclass to customise the stage sequence. Defaults to {@link #defaultStages}. */
  protected List<BootstrapStage> stages(EssentialsPlugin plugin) {
    return defaultStages(plugin);
  }

  private static EssentialsCore runStages(List<BootstrapStage> stages, StageContext context) {
    var completed = new ArrayList<BootstrapStage>(stages.size());

    try {
      for (var stage : stages) {
        stage.start(context);
        completed.add(stage);
      }
      return context.services().resolve(EssentialsCore.class);
    } catch (RuntimeException e) {
      attachStageBreadcrumb(e, completed, stages);
      rollback(context, e);
      throw e;
    }
  }

  private static void attachStageBreadcrumb(
      RuntimeException cause, List<BootstrapStage> completed, List<BootstrapStage> stages) {
    var completedNames = completed.stream().map(BootstrapStage::name).toList();
    var failedIndex = completed.size();
    var failedName = failedIndex < stages.size() ? stages.get(failedIndex).name() : "<unknown>";

    var breadcrumb =
        new IllegalStateException(
            "Bootstrap failed at stage '" + failedName + "'; completed: " + completedNames);
    cause.addSuppressed(breadcrumb);
  }

  private static void rollback(StageContext context, RuntimeException cause) {
    var services = context.services();
    var core = services.find(EssentialsCore.class).orElse(null);

    if (core != null) {
      suppressAndRun(cause, core::shutdown);
      return;
    }

    // Pre-core failure: close any infra already registered so the JVM doesn't keep the HikariCP
    // pool / MenuService executor alive after onEnable bails.
    var databaseHandle = services.find(DatabaseProvider.class);
    Consumer<DatabaseProvider> closeDatabase = db -> suppressAndRun(cause, db::close);
    databaseHandle.ifPresent(closeDatabase);

    var menuHandle = services.find(MenuService.class);
    Consumer<MenuService> shutdownMenu = menu -> suppressAndRun(cause, menu::shutdown);
    menuHandle.ifPresent(shutdownMenu);
  }

  private static void suppressAndRun(RuntimeException primary, Runnable cleanup) {
    try {
      cleanup.run();
    } catch (RuntimeException e) {
      primary.addSuppressed(e);
    }
  }
}
