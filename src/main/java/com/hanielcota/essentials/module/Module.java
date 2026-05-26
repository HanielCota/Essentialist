package com.hanielcota.essentials.module;

import lombok.NonNull;

public interface Module {

  ModuleMetadata metadata();

  default String id() {
    return metadata().id();
  }

  void enable(@NonNull ModuleContext context);

  void disable();
}
