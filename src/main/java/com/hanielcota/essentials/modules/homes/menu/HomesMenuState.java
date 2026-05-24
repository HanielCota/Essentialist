package com.hanielcota.essentials.modules.homes.menu;

import com.hanielcota.essentials.modules.homes.domain.Home;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;

public final class HomesMenuState {

  private final Map<UUID, List<Home>> prefetched = new ConcurrentHashMap<>();

  public void prefetch(@NonNull UUID viewer, @NonNull List<Home> entries) {
    this.prefetched.put(viewer, List.copyOf(entries));
  }

  public List<Home> consumePrefetch(@NonNull UUID viewer) {
    return this.prefetched.remove(viewer);
  }

  public void clear(@NonNull UUID viewer) {
    this.prefetched.remove(viewer);
  }
}
