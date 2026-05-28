package com.hanielcota.essentials.bootstrap;

import com.hanielcota.essentials.EssentialsPlugin;
import com.hanielcota.essentials.core.EssentialsCore;
import com.hanielcota.essentials.service.DefaultServiceRegistry;
import com.hanielcota.essentials.service.ServiceRegistry;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;

/**
 * Drives the plugin enable sequence as an ordered list of {@link BootstrapStage}s. Stages share
 * state through the {@link com.hanielcota.essentials.service.ServiceRegistry}; on failure of any
 * stage the {@link BootstrapRollbackHandler} tears down every already-registered piece of
 * infrastructure.
 */
@RequiredArgsConstructor
public class EssentialsBootstrap {

  private final EssentialsPlugin plugin;

  public final EssentialsCore start() {
    var services = createServiceRegistry();
    var context = new StageContext(this.plugin, services);
    var stages = stages(this.plugin);

    return runStages(stages, context);
  }

  protected ServiceRegistry createServiceRegistry() {
    return new DefaultServiceRegistry();
  }

  protected List<BootstrapStage> stages(EssentialsPlugin plugin) {
    return StageFactory.defaultStages(plugin);
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
      StageFailureDiagnostics.attach(e, completed, stages);
      BootstrapRollbackHandler.rollback(context, e);
      throw e;
    }
  }
}
