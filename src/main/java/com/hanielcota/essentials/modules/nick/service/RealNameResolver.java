package com.hanielcota.essentials.modules.nick.service;

import com.hanielcota.essentials.paper.PlayerProvider;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Resolves a player's true (non-nick) display name by id. Looks up online players first, then falls
 * back to the cached {@code NickEntry#realName} (kept across restarts), and finally to the offline
 * Bukkit name or the raw UUID.
 */
@RequiredArgsConstructor
public final class RealNameResolver {

  private final NickService nicks;
  private final PlayerProvider players;

  public String resolve(@NonNull UUID id) {
    var online = this.players.online(id).orElse(null);
    if (online != null) {
      return online.getName();
    }

    var entry = this.nicks.nickOf(id).orElse(null);
    if (entry != null) {
      return entry.realName();
    }

    var offline = this.players.offline(id);
    var stored = offline.getName();

    return stored != null ? stored : id.toString();
  }
}
