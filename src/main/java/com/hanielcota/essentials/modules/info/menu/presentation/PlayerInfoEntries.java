package com.hanielcota.essentials.modules.info.menu.presentation;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.info.config.InfoConfig;
import com.hanielcota.essentials.modules.info.config.PlayerEntriesSection;
import com.hanielcota.essentials.shared.DurationFormatter;
import com.hanielcota.essentials.shared.Numbers;
import com.hanielcota.essentials.user.UserSessionService;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class PlayerInfoEntries {

  private final UserSessionService sessions;
  private final ConfigHandle<InfoConfig> config;

  private static int roundedHealth(@NonNull Player player) {
    return (int) Math.round(player.getHealth());
  }

  public List<InfoEntry> entries(@NonNull Player player) {
    var snap = this.config.value();
    var section = snap.player();

    var uuid = player.getUniqueId();
    var name = player.getName();
    var location = player.getLocation();
    var worldName = player.getWorld().getName();

    var health = roundedHealth(player);
    var foodLevel = player.getFoodLevel();
    var level = player.getLevel();
    var gameModeLabel = snap.gameModeLabel(player.getGameMode());
    var ping = player.getPing();
    var sessionTime = sessionDuration(section, player);

    var x = Numbers.display(location.getX());
    var y = Numbers.display(location.getY());
    var z = Numbers.display(location.getZ());
    var coords = x + ", " + y + ", " + z;

    var headEntry = InfoEntry.headFrom(uuid, section.head(), Map.of("player", name));
    var healthEntry = InfoEntry.from(section.health(), Map.of("health", health));
    var foodEntry = InfoEntry.from(section.food(), Map.of("food", foodLevel));
    var levelEntry = InfoEntry.from(section.level(), Map.of("level", level));
    var modeEntry = InfoEntry.from(section.mode(), Map.of("mode", gameModeLabel));
    var worldEntry = InfoEntry.from(section.world(), Map.of("world", worldName));
    var locationEntry =
        InfoEntry.from(section.location(), Map.of("x", x, "y", y, "z", z, "coords", coords));
    var pingEntry = InfoEntry.from(section.ping(), Map.of("ping", ping));
    var sessionEntry = InfoEntry.from(section.session(), Map.of("duration", sessionTime));

    return List.of(
        headEntry,
        healthEntry,
        foodEntry,
        levelEntry,
        modeEntry,
        worldEntry,
        locationEntry,
        pingEntry,
        sessionEntry);
  }

  private String sessionDuration(@NonNull PlayerEntriesSection section, @NonNull Player player) {
    var uuid = player.getUniqueId();
    var sessionOpt = this.sessions.sessionOf(uuid);

    if (sessionOpt.isEmpty()) {
      return section.noSessionLabel();
    }

    var session = sessionOpt.get();
    var connectedAt = session.connectedAt();
    var now = Instant.now();
    var duration = Duration.between(connectedAt, now);

    return DurationFormatter.format(duration);
  }
}
