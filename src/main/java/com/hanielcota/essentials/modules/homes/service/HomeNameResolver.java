package com.hanielcota.essentials.modules.homes.service;

import lombok.NonNull;

public record HomeNameResolver(@NonNull HomeNameValidator validator) {

  public String resolve(@NonNull String rawName) {
    if (!this.validator.isValid(rawName)) {
      return null;
    }

    return rawName;
  }
}
