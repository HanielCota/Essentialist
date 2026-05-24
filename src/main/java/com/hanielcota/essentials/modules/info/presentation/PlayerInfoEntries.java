package com.hanielcota.essentials.modules.info.presentation;

import com.hanielcota.essentials.user.UserSessionService;
import com.hanielcota.essentials.util.DurationFormatter;
import com.hanielcota.essentials.util.Numbers;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class PlayerInfoEntries {

  private static final String GRAY = "<gray>";

  private final UserSessionService sessions;

  private static int roundedHealth(@NonNull Player player) {
    return (int) Math.round(player.getHealth());
  }

  private static String gameModeName(@NonNull GameMode mode) {
    return switch (mode) {
      case SURVIVAL -> "Sobrevivência";
      case CREATIVE -> "Criativo";
      case ADVENTURE -> "Aventura";
      case SPECTATOR -> "Espectador";
    };
  }

  public List<InfoEntry> entries(@NonNull Player player) {
    var uuid = player.getUniqueId();
    var name = player.getName();
    var location = player.getLocation();

    var coords =
        Numbers.compact(location.getX())
            + ", "
            + Numbers.compact(location.getY())
            + ", "
            + Numbers.compact(location.getZ());

    return List.of(
        InfoEntry.head(uuid, "<yellow>" + name, "<gray>Informações do jogador."),
        InfoEntry.of(
            Material.GOLDEN_APPLE, "<yellow>Vida", GRAY + roundedHealth(player) + " <red>❤"),
        InfoEntry.of(
            Material.COOKED_BEEF,
            "<yellow>Fome",
            GRAY + player.getFoodLevel() + " <dark_gray>/ <gray>20"),
        InfoEntry.of(Material.EXPERIENCE_BOTTLE, "<yellow>Nível", GRAY + player.getLevel()),
        InfoEntry.of(
            Material.GRASS_BLOCK,
            "<yellow>Modo de jogo",
            GRAY + gameModeName(player.getGameMode())),
        InfoEntry.of(Material.MAP, "<yellow>Mundo", GRAY + player.getWorld().getName()),
        InfoEntry.of(Material.COMPASS, "<yellow>Localização", GRAY + coords),
        InfoEntry.of(Material.FEATHER, "<yellow>Ping", GRAY + player.getPing() + " ms"),
        InfoEntry.of(Material.CLOCK, "<yellow>Tempo de sessão", GRAY + sessionDuration(player)));
  }

  private String sessionDuration(@NonNull Player player) {
    var uuid = player.getUniqueId();
    var sessionOpt = this.sessions.sessionOf(uuid);
    var formattedOpt =
        sessionOpt.map(
            session -> {
              var duration = Duration.between(session.connectedAt(), Instant.now());
              return DurationFormatter.format(duration);
            });
    return formattedOpt.orElse("agora mesmo");
  }
}
