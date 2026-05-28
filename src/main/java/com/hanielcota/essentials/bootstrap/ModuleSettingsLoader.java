package com.hanielcota.essentials.bootstrap;

import com.hanielcota.essentials.config.YamlReadWriter;
import com.hanielcota.essentials.module.Module;
import com.hanielcota.essentials.module.discovery.ModuleSettings;
import java.nio.file.Path;
import java.util.Collection;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
final class ModuleSettingsLoader {

  private final @NonNull Path dataFolder;

  ModuleSettings load(@NonNull Collection<Module> modules) {
    var file = this.dataFolder.resolve("modules.yml");
    return YamlReadWriter.readMerging(
        file, ModuleSettings.class, () -> ModuleSettings.forModules(modules));
  }
}
