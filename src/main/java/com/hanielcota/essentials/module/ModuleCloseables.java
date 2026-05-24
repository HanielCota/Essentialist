package com.hanielcota.essentials.module;

import com.hanielcota.essentials.util.Log;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;

final class ModuleCloseables {

  private final List<AutoCloseable> closeables = new ArrayList<>();

  void register(@NonNull AutoCloseable closeable) {
    this.closeables.add(closeable);
  }

  void closeAll(@NonNull String moduleId, @NonNull Log log) {
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
