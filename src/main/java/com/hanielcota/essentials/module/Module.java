package com.hanielcota.essentials.module;

import com.hanielcota.essentials.module.environment.ModuleContext;
import lombok.NonNull;

public interface Module {

  ModuleMetadata metadata();

  default String id() {
    return metadata().id();
  }

  void enable(@NonNull ModuleContext context);

  void disable();
}
