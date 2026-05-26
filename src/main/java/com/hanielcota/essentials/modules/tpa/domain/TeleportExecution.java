package com.hanielcota.essentials.modules.tpa.domain;

import java.util.Optional;
import org.jspecify.annotations.Nullable;

public record TeleportExecution(@Nullable Destination destination) {

  public static TeleportExecution failed() {
    return new TeleportExecution(null);
  }

  public static TeleportExecution success(Destination destination) {
    return new TeleportExecution(destination);
  }

  public boolean succeeded() {
    return this.destination != null;
  }

  public Optional<Destination> optionalDestination() {
    return Optional.ofNullable(this.destination);
  }
}
