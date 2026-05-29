package com.hanielcota.essentials.modules.kit.menu;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;

/** Per-viewer navigation state: the category being browsed and the kit being previewed. */
public final class KitMenuState {

  private final Map<UUID, String> category = new ConcurrentHashMap<>();
  private final Map<UUID, String> kit = new ConcurrentHashMap<>();

  public void setCategory(@NonNull UUID viewer, @NonNull String categoryId) {
    this.category.put(viewer, categoryId);
  }

  public String category(@NonNull UUID viewer) {
    return this.category.get(viewer);
  }

  public void setKit(@NonNull UUID viewer, @NonNull String kitId) {
    this.kit.put(viewer, kitId);
  }

  public String kit(@NonNull UUID viewer) {
    return this.kit.get(viewer);
  }

  public void clear(@NonNull UUID viewer) {
    this.category.remove(viewer);
    this.kit.remove(viewer);
  }
}
