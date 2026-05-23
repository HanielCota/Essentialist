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

    return List.of(
        InfoEntry.of(Material.CLOCK, "<yellow>TPS", "<gray>" + formattedTps()),
        InfoEntry.of(Material.PLAYER_HEAD, "<yellow>Jogadores online", "<gray>" + playerFormat),
        InfoEntry.of(Material.NAME_TAG, "<yellow>Versão", "<gray>" + Bukkit.getVersion()),
        InfoEntry.of(Material.COMPARATOR, "<yellow>Tempo ligado", "<gray>" + formattedUptime()),
        InfoEntry.of(Material.REDSTONE, "<yellow>Memória", "<gray>" + formattedMemory()),
        InfoEntry.of(
            Material.GRASS_BLOCK, "<yellow>Mundos", "<gray>" + worldCount + " carregado(s)"));
  }

  /** How long the player has been connected this session, or a fallback for a fresh join. */
  private String sessionDuration(@NonNull Player player) {
    var uuid = player.getUniqueId();

    return sessions
        .sessionOf(uuid)
        .map(
            session -> {
              var duration = Duration.between(session.connectedAt(), Instant.now());
              return DurationFormatter.format(duration);
            })
        .orElse("agora mesmo");
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

    return List.of(
        InfoEntry.head(uuid, "<yellow>" + name, "<gray>Informações do jogador."),
        InfoEntry.of(Material.GOLDEN_APPLE, "<yellow>Vida", "<gray>" + health + " <red>❤"),
        InfoEntry.of(
            Material.COOKED_BEEF, "<yellow>Fome", "<gray>" + food + " <dark_gray>/ <gray>20"),
        InfoEntry.of(Material.EXPERIENCE_BOTTLE, "<yellow>Nível", "<gray>" + level),
        InfoEntry.of(Material.GRASS_BLOCK, "<yellow>Modo de jogo", "<gray>" + gameMode),
        InfoEntry.of(Material.MAP, "<yellow>Mundo", "<gray>" + worldName),
        InfoEntry.of(Material.COMPASS, "<yellow>Localização", "<gray>" + coords),
        InfoEntry.of(Material.FEATHER, "<yellow>Ping", "<gray>" + ping + " ms"),
        InfoEntry.of(Material.CLOCK, "<yellow>Tempo de sessão", "<gray>" + duration));
  }

  /** Information about the Essentialist plugin itself. */
  public List<InfoEntry> aboutEntries() {
    var meta = plugin.getPluginMeta();
    var name = meta.getName();
    var version = meta.getVersion();
    var authorsList = authors(meta.getAuthors());
    var mcVersion = Bukkit.getMinecraftVersion();

    return List.of(
        InfoEntry.of(Material.NETHER_STAR, "<yellow>" + name, "<gray>Versão <white>" + version),
        InfoEntry.of(Material.WRITABLE_BOOK, "<yellow>Autor", "<gray>" + authorsList),
        InfoEntry.of(Material.GRASS_BLOCK, "<yellow>Minecraft", "<gray>" + mcVersion));
  }
}
