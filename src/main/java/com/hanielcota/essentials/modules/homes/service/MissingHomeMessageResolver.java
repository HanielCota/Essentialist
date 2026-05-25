package com.hanielcota.essentials.modules.homes.service;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Picks the right "missing home" line for {@code /home}: {@code noHomes} when the player owns none,
 * otherwise {@code unknownHome} with the requested name substituted in.
 */
@RequiredArgsConstructor
public final class MissingHomeMessageResolver {

  private static final String NAME = "{name}";

  private final ConfigHandle<HomesConfig> config;
  private final HomeService service;

  public String resolve(@NonNull UUID owner, @NonNull String name) {
    var snap = this.config.value();
    var messages = snap.messages();

    if (this.service.count(owner) == 0) {
      return messages.noHomes();
    }

    var unknownHomeMsg = messages.unknownHome();

    return unknownHomeMsg.replace(NAME, name);
  }
}
