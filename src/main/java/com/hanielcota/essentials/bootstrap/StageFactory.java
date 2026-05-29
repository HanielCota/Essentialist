package com.hanielcota.essentials.bootstrap;

import com.hanielcota.essentials.EssentialsPlugin;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Produces the default {@link BootstrapStage} sequence. Extracted from {@link EssentialsBootstrap}
 * so the orchestrator no longer couples to every concrete stage class.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StageFactory {

  public static List<BootstrapStage> defaultStages(EssentialsPlugin plugin) {
    return List.of(
        new CoreServicesBootstrap(plugin),
        new DatabaseBootstrap(plugin),
        new ModuleDiscoveryStage(plugin),
        new CommandSystemBootstrap(plugin),
        new MenuBootstrap(plugin),
        new UserStackBootstrap(plugin),
        new CoreInstanceStage(plugin),
        new EnableModulesStage());
  }
}
