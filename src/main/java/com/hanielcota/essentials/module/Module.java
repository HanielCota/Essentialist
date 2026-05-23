package com.hanielcota.essentials.module;

import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import lombok.NonNull;

public interface Module {

  ModuleMetadata metadata();

  default String id() {
    return metadata().id();
  }

  default void customizeCommands(@NonNull PaperCommandFramework.Builder builder) {}

  void enable(@NonNull ModuleContext context);

  void disable();
}
