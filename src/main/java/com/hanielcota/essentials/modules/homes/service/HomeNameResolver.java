package com.hanielcota.essentials.modules.homes.service;

import java.util.Optional;
import lombok.NonNull;

public record HomeNameResolver(@NonNull HomeNameValidator validator) {

  public Optional<String> resolve(@NonNull String rawName) {
    if (!this.validator.isValid(rawName)) {
      return Optional.empty();
    }

    return Optional.of(rawName);
  }
}
