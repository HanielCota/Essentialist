package com.hanielcota.essentials.module.lifecycle;

import com.hanielcota.essentials.shared.Log;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;

public final class ModuleCloseables {

  private final List<AutoCloseable> closeables = new ArrayList<>();

  public void register(@NonNull AutoCloseable closeable) {
    this.closeables.add(closeable);
  }

  public void closeAll(@NonNull String moduleId, @NonNull Log log) {
    for (var closeable : this.closeables) {
      try {
        closeable.close();
      } catch (Exception e) {
        log.warn(e, "Closeable threw during disable of {}", moduleId);
      }
    }
    this.closeables.clear();
  }
}
