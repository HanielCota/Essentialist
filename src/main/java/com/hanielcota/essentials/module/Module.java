package com.hanielcota.essentials.module;

import io.github.hanielcota.commandframework.paper.PaperCommandFramework;

public interface Module {

  ModuleMetadata metadata();

  default String id() {
    return metadata().id();
  }

  default void customizeCommands(PaperCommandFramework.Builder builder) {}

  void enable(ModuleContext context);

  void disable();
}
