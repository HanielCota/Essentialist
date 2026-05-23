package com.hanielcota.essentials.modules.homes.name;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import lombok.NonNull;

public record HomeNameResolver(ConfigHandle<HomesConfig> config, HomeNameValidator validator) {

  public HomeNameResolver(
      @NonNull ConfigHandle<HomesConfig> config, @NonNull HomeNameValidator validator) {
    this.config = config;
    this.validator = validator;
  }

  public String resolve(@NonNull String rawName) {
    if (rawName == null || rawName.isBlank()) {
      return this.config.value().defaultHomeName();
    }

    if (this.validator.isValid(rawName)) {
      return rawName;
    }

    return null;
  }
}
