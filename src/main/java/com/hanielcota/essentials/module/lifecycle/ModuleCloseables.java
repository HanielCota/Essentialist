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
    // Reverse (LIFO) teardown: a closeable registered later may depend on an earlier one (e.g. a
    // cache that flushes through an async writer), so close it first. Mirrors the reverse-order
    // shutdown used elsewhere in the lifecycle.
    for (var index = this.closeables.size() - 1; index >= 0; index--) {
      var closeable = this.closeables.get(index);

      try {
        closeable.close();
      } catch (Exception e) {
        log.warn(e, "Closeable threw during disable of {}", moduleId);
      }
    }
    this.closeables.clear();
  }
}
