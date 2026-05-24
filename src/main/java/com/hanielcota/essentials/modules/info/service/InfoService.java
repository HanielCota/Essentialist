package com.hanielcota.essentials.modules.info.service;

import com.hanielcota.essentials.user.UserSessionService;
import com.hanielcota.essentials.util.DurationFormatter;
import com.hanielcota.essentials.util.Numbers;
import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

@RequiredArgsConstructor
public final class InfoService {

  private static final long BYTES_PER_MB = 1024L * 1024L;
  private static final String GRAY = "<gray>";

  private final Plugin plugin;
  private final UserSessionService sessions;

  private static String formattedTps() {
    var tps = Math.min(20.0, Bukkit.getTPS()[0]);
    return String.format(Locale.US, "%.2f", tps);
  }

  private static String formattedUptime() {
    var runtimeMx = ManagementFactory.getRuntimeMXBean();
    var uptime = Duration.ofMillis(runtimeMx.getUptime());
    return DurationFormatter.format(uptime);
  }

  private static String formattedMemory() {
    var runtime = Runtime.getRuntime();
    var usedMb = (runtime.totalMemory() - runtime.freeMemory()) / BYTES_PER_MB;
    var maxMb = runtime.maxMemory() / BYTES_PER_MB;

    return usedMb + " MB <dark_gray>/ <gray>" + maxMb + " MB";
  }

  private static String authors(@NonNull List<String> authors) {
    if (authors.isEmpty()) {
      return "Desconhecido";
    }
    return String.join(", ", authors);
  }

  private static String gameModeName(@NonNull GameMode mode) {
    return switch (mode) {
      case SURVIVAL -> "Sobrevivência";
      case CREATIVE -> "Criativo";
      case ADVENTURE -> "Aventura";
      case SPECTATOR -> "Espectador";
    };
  }

  /** Server status: TPS, players, version, uptime, memory and worlds. */
  public List<InfoEntry> serverEntries() {
    var onlineCount = Bukkit.getOnlinePlayers().size();
    var maxPlayers = Bukkit.getMaxPlayers();
    var playerFormat = onlineCount + " <dark_gray>/ <gray>" + maxPlayers;

    var worldCount = Bukkit.getWorlds().size();

    var tpsText = GRAY + formattedTps();
    var versionText = GRAY + Bukkit.getVersion();
    var uptimeText = GRAY + formattedUptime();
    var memoryText = GRAY + formattedMemory();
    var worldText = GRAY + worldCount + " carregado(s)";

    return List.of(
        InfoEntry.of(Material.CLOCK, "<yellow>TPS", tpsText),
        InfoEntry.of(Material.PLAYER_HEAD, "<yellow>Jogadores online", GRAY + playerFormat),
        InfoEntry.of(Material.NAME_TAG, "<yellow>Versão", versionText),
        InfoEntry.of(Material.COMPARATOR, "<yellow>Tempo ligado", uptimeText),
        InfoEntry.of(Material.REDSTONE, "<yellow>Memória", memoryText),
        InfoEntry.of(Material.GRASS_BLOCK, "<yellow>Mundos", worldText));
  }

  /** How long the player has been connected this session, or a fallback for a fresh join. */
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

  /** Live information about a single player. */
  public List<InfoEntry> playerEntries(@NonNull Player player) {
    var uuid = player.getUniqueId();
    var name = player.getName();
    var location = player.getLocation();

    var health = (int) Math.round(player.getHealth());
    var food = player.getFoodLevel();
    var level = player.getLevel();
    var ping = player.getPing();

    var gameMode = gameModeName(player.getGameMode());
    var worldName = player.getWorld().getName();
    var duration = sessionDuration(player);

    var coords =
        Numbers.compact(location.getX())
            + ", "
            + Numbers.compact(location.getY())
            + ", "
            + Numbers.compact(location.getZ());

    var healthText = GRAY + health + " <red>❤";
    var foodText = GRAY + food + " <dark_gray>/ <gray>20";
    var levelText = GRAY + level;
    var gameModeText = GRAY + gameMode;
    var worldText = GRAY + worldName;
    var pingText = GRAY + ping + " ms";
    var durationText = GRAY + duration;

    return List.of(
        InfoEntry.head(uuid, "<yellow>" + name, "<gray>Informações do jogador."),
        InfoEntry.of(Material.GOLDEN_APPLE, "<yellow>Vida", healthText),
        InfoEntry.of(Material.COOKED_BEEF, "<yellow>Fome", foodText),
        InfoEntry.of(Material.EXPERIENCE_BOTTLE, "<yellow>Nível", levelText),
        InfoEntry.of(Material.GRASS_BLOCK, "<yellow>Modo de jogo", gameModeText),
        InfoEntry.of(Material.MAP, "<yellow>Mundo", worldText),
        InfoEntry.of(Material.COMPASS, "<yellow>Localização", GRAY + coords),
        InfoEntry.of(Material.FEATHER, "<yellow>Ping", pingText),
        InfoEntry.of(Material.CLOCK, "<yellow>Tempo de sessão", durationText));
  }

  /** Information about the Essentialist plugin itself. */
  public List<InfoEntry> aboutEntries() {
    var meta = this.plugin.getPluginMeta();
    var name = meta.getName();
    var version = meta.getVersion();
    var authorsList = authors(meta.getAuthors());
    var mcVersion = Bukkit.getMinecraftVersion();

    var pluginName = "<yellow>" + name;
    var versionText = "<gray>Versão <white>" + version;
    var authorsText = GRAY + authorsList;
    var mcText = GRAY + mcVersion;

    return List.of(
        InfoEntry.of(Material.NETHER_STAR, pluginName, versionText),
        InfoEntry.of(Material.WRITABLE_BOOK, "<yellow>Autor", authorsText),
        InfoEntry.of(Material.GRASS_BLOCK, "<yellow>Minecraft", mcText));
  }
}
