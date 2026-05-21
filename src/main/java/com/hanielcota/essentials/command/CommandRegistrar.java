package com.hanielcota.essentials.command;

import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import java.util.Objects;

public record CommandRegistrar(PaperCommandFramework framework) {

  public CommandRegistrar(PaperCommandFramework framework) {
    this.framework = Objects.requireNonNull(framework, "framework");
  }

  public void register(Class<?> handlerClass) {
    framework.registerClasses(Objects.requireNonNull(handlerClass, "handlerClass"));
  }

  public void register(Object handler) {
    framework.registerAnnotated(Objects.requireNonNull(handler, "handler"));
  }
}
