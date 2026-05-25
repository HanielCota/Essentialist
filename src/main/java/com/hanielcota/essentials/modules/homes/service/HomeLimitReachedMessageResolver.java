package com.hanielcota.essentials.modules.homes.service;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/**
 * Builds the "limit reached" line for {@code /sethome}: substitutes the requested name and the
 * caller's effective limit (derived from their {@code essentials.home.limit.N} permissions).
 */
@RequiredArgsConstructor
public final class HomeLimitReachedMessageResolver {

  private final ConfigHandle<HomesConfig> config;
  private final HomeService service;

  public String resolve(@NonNull String name, @NonNull Player sender) {
    var snap = this.config.value();
    var messages = snap.messages();
    var limitValue = this.service.limit(sender);
    var limit = Integer.toString(limitValue);

    var limitReachedMsg = messages.limitReached();
    var withName = limitReachedMsg.replace("{name}", name);

    return withName.replace("{limit}", limit);
  }
}
