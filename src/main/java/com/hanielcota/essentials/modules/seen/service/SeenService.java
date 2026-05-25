package com.hanielcota.essentials.modules.seen.service;

import com.hanielcota.essentials.modules.nick.service.NickService;
import com.hanielcota.essentials.service.ServiceRegistry;
import com.hanielcota.essentials.util.DurationFormatter;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

/**
 * Resolves a query string to a cached {@link OfflinePlayer} and describes whether they are online
 * or offline along with the elapsed duration since the relevant transition.
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

  /**
   * Resolves the query, picks the online vs offline duration source and returns a structured
   * description. Empty when the player has never been seen on this server.
   */
  public Optional<SeenLine> describe(@NonNull String query, @NonNull Instant now) {
    var target = findPlayer(query).orElse(null);
    if (target == null) {
      return Optional.empty();
    }

    var displayName = resolveName(target, query);
    if (target.isOnline()) {
      var loginMillis = target.getLastLogin();
      var duration = sinceMillis(loginMillis, now);
      var line = new SeenLine(SeenLine.Kind.ONLINE, displayName, duration);

      return Optional.of(line);
    }

    var seenMillis = target.getLastSeen();
    var duration = sinceMillis(seenMillis, now);
    var line = new SeenLine(SeenLine.Kind.OFFLINE, displayName, duration);

    return Optional.of(line);
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

  private static String sinceMillis(long sourceMillis, @NonNull Instant now) {
    if (sourceMillis <= 0L) {
      return DurationFormatter.format(Duration.ZERO);
    }

    var source = Instant.ofEpochMilli(sourceMillis);
    var elapsed = Duration.between(source, now);

    return DurationFormatter.format(elapsed);
  }

  private static String resolveName(@NonNull OfflinePlayer target, @NonNull String fallback) {
    var stored = target.getName();

    return stored != null ? stored : fallback;
  }
}
