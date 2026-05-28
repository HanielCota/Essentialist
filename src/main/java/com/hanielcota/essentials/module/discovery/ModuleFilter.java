package com.hanielcota.essentials.module.discovery;

import com.hanielcota.essentials.module.Module;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ModuleFilter {

  public static List<Module> enabled(
      @NonNull Collection<Module> modules, @NonNull ModuleSettings settings) {

    var enabled = new ArrayList<Module>();

    for (var module : modules) {
      if (settings.enabled(module.id())) {
        enabled.add(module);
      }
    }

    return ModuleDependencyResolver.resolve(enabled);
  }
}
