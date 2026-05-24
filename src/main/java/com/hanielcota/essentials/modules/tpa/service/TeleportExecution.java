package com.hanielcota.essentials.modules.tpa.service;

import com.hanielcota.essentials.modules.tpa.model.Destination;
import java.util.Optional;
import org.jspecify.annotations.Nullable;

record TeleportExecution(AcceptResult result, @Nullable Destination destination) {

  static TeleportExecution failed(AcceptResult result) {
    return new TeleportExecution(result, null);
  }

  static TeleportExecution success(Destination destination) {
    return new TeleportExecution(AcceptResult.SUCCESS, destination);
  }

  Optional<Destination> optionalDestination() {
    return Optional.ofNullable(this.destination);
  }
}
