package com.hanielcota.essentials.modules.tpa.service;

import com.hanielcota.essentials.modules.tpa.domain.Destination;
import java.util.Optional;
import org.jspecify.annotations.Nullable;

record TeleportExecution(@Nullable Destination destination) {

  static TeleportExecution failed() {
    return new TeleportExecution(null);
  }

  static TeleportExecution success(Destination destination) {
    return new TeleportExecution(destination);
  }

  boolean succeeded() {
    return this.destination != null;
  }

  Optional<Destination> optionalDestination() {
    return Optional.ofNullable(this.destination);
  }
}
