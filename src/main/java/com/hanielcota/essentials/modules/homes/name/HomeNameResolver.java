package com.hanielcota.essentials.modules.homes.name;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import lombok.NonNull;

public record HomeNameResolver(
    @NonNull ConfigHandle<HomesConfig> config, @NonNull HomeNameValidator validator) {

  public String resolve(@NonNull String rawName) {
    if (rawName.isBlank()) {
      var snap = this.config.value();
      return snap.defaultHomeName();
    }

    if (!this.validator.isValid(rawName)) {
      return null;
    }

    return rawName;
  }
}
