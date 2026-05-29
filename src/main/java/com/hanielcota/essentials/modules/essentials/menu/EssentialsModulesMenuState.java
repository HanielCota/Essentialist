package com.hanielcota.essentials.modules.essentials.menu;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;

/** Per-viewer open category for the module-control menu. */
public final class EssentialsModulesMenuState {

  private final Map<UUID, ModuleCategory> openCategory = new ConcurrentHashMap<>();

  public ModuleCategory category(@NonNull UUID viewer) {
    return this.openCategory.getOrDefault(viewer, ModuleCategory.PROTECTION);
  }

  public void switchCategory(@NonNull UUID viewer, @NonNull ModuleCategory category) {
    this.openCategory.put(viewer, category);
  }

  public void clear(@NonNull UUID viewer) {
    this.openCategory.remove(viewer);
  }
}
