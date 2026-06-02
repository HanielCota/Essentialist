package com.hanielcota.essentials.modules.back.service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;
import org.jspecify.annotations.Nullable;

public final class BackStaffViewState {

  private final Map<UUID, TargetContext> views = new ConcurrentHashMap<>();

  public void startView(@NonNull UUID viewer, @NonNull UUID target, @NonNull String targetName) {
    this.views.put(viewer, new TargetContext(target, targetName));
  }

  public @Nullable UUID targetOf(@NonNull UUID viewer) {
    var ctx = this.views.get(viewer);
    if (ctx == null) {
      return null;
    }
    return ctx.target;
  }

  public @Nullable String targetNameOf(@NonNull UUID viewer) {
    var ctx = this.views.get(viewer);
    if (ctx == null) {
      return null;
    }
    return ctx.targetName;
  }

  public void endView(@NonNull UUID viewer) {
    this.views.remove(viewer);
  }

  private record TargetContext(UUID target, String targetName) {}
}
