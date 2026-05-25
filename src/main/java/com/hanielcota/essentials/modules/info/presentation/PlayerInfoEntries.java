package com.hanielcota.essentials.modules.info.presentation;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.info.config.InfoConfig;
import com.hanielcota.essentials.user.UserSessionService;
import com.hanielcota.essentials.util.DurationFormatter;
import com.hanielcota.essentials.util.Numbers;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class PlayerInfoEntries {

  private static final String GRAY = "<gray>";

  private final UserSessionService sessions;
  private final ConfigHandle<InfoConfig> config;

  private static int roundedHealth(@NonNull Player player) {
    return (int) Math.round(player.getHealth());
  }

  private static String formatCoords(@NonNull Location location) {
    var x = Numbers.compact(location.getX());
    var y = Numbers.compact(location.getY());
    var z = Numbers.compact(location.getZ());

    return x + ", " + y + ", " + z;
  }

  public List<InfoEntry> entries(@NonNull Player player) {
    var uuid = player.getUniqueId();
    var name = player.getName();
    var location = player.getLocation();
    var world = player.getWorld();
    var worldName = world.getName();

    var health = roundedHealth(player);
    var foodLevel = player.getFoodLevel();
    var level = player.getLevel();
    var gameMode = player.getGameMode();
    var gameModeLabel = labelFor(gameMode);
    var ping = player.getPing();
    var coords = formatCoords(location);
    var sessionTime = sessionDuration(player);

    var headTitle = "<yellow>" + name;
    var headLore = "<gray>Informações do jogador.";
    var healthLore = GRAY + health + " <red>❤";
    var foodLore = GRAY + foodLevel + " <dark_gray>/ <gray>20";
    var levelLore = GRAY + level;
    var gameModeLore = GRAY + gameModeLabel;
    var worldLore = GRAY + worldName;
    var coordsLore = GRAY + coords;
    var pingLore = GRAY + ping + " ms";
    var sessionLore = GRAY + sessionTime;

    var headEntry = InfoEntry.head(uuid, headTitle, headLore);
    var healthEntry = InfoEntry.of(Material.GOLDEN_APPLE, "<yellow>Vida", healthLore);
    var foodEntry = InfoEntry.of(Material.COOKED_BEEF, "<yellow>Fome", foodLore);
    var levelEntry = InfoEntry.of(Material.EXPERIENCE_BOTTLE, "<yellow>Nível", levelLore);
    var modeEntry = InfoEntry.of(Material.GRASS_BLOCK, "<yellow>Modo de jogo", gameModeLore);
    var worldEntry = InfoEntry.of(Material.MAP, "<yellow>Mundo", worldLore);
    var locationEntry = InfoEntry.of(Material.COMPASS, "<yellow>Localização", coordsLore);
    var pingEntry = InfoEntry.of(Material.FEATHER, "<yellow>Ping", pingLore);
    var sessionEntry = InfoEntry.of(Material.CLOCK, "<yellow>Tempo de sessão", sessionLore);

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

  private String labelFor(@NonNull GameMode mode) {
    var snap = this.config.value();

    return snap.gameModeLabel(mode);
  }

  private String sessionDuration(@NonNull Player player) {
    var uuid = player.getUniqueId();
    var sessionOpt = this.sessions.sessionOf(uuid);

    if (sessionOpt.isEmpty()) {
      return "agora mesmo";
    }

    var session = sessionOpt.get();
    var connectedAt = session.connectedAt();
    var now = Instant.now();
    var duration = Duration.between(connectedAt, now);

    return DurationFormatter.format(duration);
  }
}
