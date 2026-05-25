package com.hanielcota.essentials.modules.seen.service;

import com.hanielcota.essentials.modules.nick.service.NickService;
import com.hanielcota.essentials.service.ServiceRegistry;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

/**
 * Resolves a query string to a cached {@link OfflinePlayer}. Tries the nick index first (so {@code
 * /seen CoolName} finds the player even after a Mojang rename) and falls back to Bukkit's name
 * cache.
 *
 * <p>The nick lookup is optional — {@link NickService} may not be registered when the nick module
 * is disabled. The registry is resolved lazily so module load order does not matter.
 */
@RequiredArgsConstructor
public final class SeenService {

  private final ServiceRegistry registry;

  public Optional<OfflinePlayer> findPlayer(@NonNull String query) {
    var fromNick = lookupViaNick(query);
    if (fromNick.isPresent()) {
      return fromNick;
    }

    return lookupCached(query);
  }

  private Optional<OfflinePlayer> lookupViaNick(@NonNull String query) {
    var nicks = this.registry.find(NickService.class).orElse(null);
    if (nicks == null) {
      return Optional.empty();
    }

    var id = nicks.idByNick(query).orElse(null);
    if (id == null) {
      return Optional.empty();
    }

    var offline = Bukkit.getOfflinePlayer(id);

    return Optional.of(offline);
  }

  private static Optional<OfflinePlayer> lookupCached(@NonNull String query) {
    var cached = Bukkit.getOfflinePlayerIfCached(query);

    return Optional.ofNullable(cached);
  }
}
