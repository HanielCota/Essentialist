package com.hanielcota.essentials.modules.info.service;

import com.hanielcota.essentials.user.UserSessionService;
import com.hanielcota.essentials.util.DurationFormatter;
import com.hanielcota.essentials.util.Numbers;
import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
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
    double tps = Math.min(20.0, Bukkit.getTPS()[0]);
    return String.format(Locale.US, "%.2f", tps);
  }

  private static String formattedUptime() {
    var uptime = Duration.ofMillis(ManagementFactory.getRuntimeMXBean().getUptime());
    return DurationFormatter.format(uptime);
  }

  private static String formattedMemory() {
    var runtime = Runtime.getRuntime();
    long usedMb = (runtime.totalMemory() - runtime.freeMemory()) / BYTES_PER_MB;
    long maxMb = runtime.maxMemory() / BYTES_PER_MB;
    return usedMb + " MB <dark_gray>/ <gray>" + maxMb + " MB";
  }

  private static String authors(List<String> authors) {
    return authors.isEmpty() ? "Desconhecido" : String.join(", ", authors);
  }

  private static String gameModeName(GameMode mode) {
    return switch (mode) {
      case SURVIVAL -> "SobrevivÃªncia";
      case CREATIVE -> "Criativo";
      case ADVENTURE -> "Aventura";
      case SPECTATOR -> "Espectador";
    };
  }

  /** Server status: TPS, players, version, uptime, memory and worlds. */
  public List<InfoEntry> serverEntries() {
    return List.of(
        InfoEntry.of(Material.CLOCK, "<yellow>TPS", "<gray>" + formattedTps()),
        InfoEntry.of(
            Material.PLAYER_HEAD,
            "<yellow>Jogadores online",
            "<gray>"
                + Bukkit.getOnlinePlayers().size()
                + " <dark_gray>/ <gray>"
                + Bukkit.getMaxPlayers()),
        InfoEntry.of(Material.NAME_TAG, "<yellow>VersÃ£o", "<gray>" + Bukkit.getVersion()),
        InfoEntry.of(Material.COMPARATOR, "<yellow>Tempo ligado", "<gray>" + formattedUptime()),
        InfoEntry.of(Material.REDSTONE, "<yellow>MemÃ³ria", "<gray>" + formattedMemory()),
        InfoEntry.of(
            Material.GRASS_BLOCK,
            "<yellow>Mundos",
            "<gray>" + Bukkit.getWorlds().size() + " carregado(s)"));
  }

  /** How long the player has been connected this session, or a fallback for a fresh join. */
  private String sessionDuration(Player player) {
    return sessions
        .sessionOf(player.getUniqueId())
        .map(
            session ->
                DurationFormatter.format(Duration.between(session.connectedAt(), Instant.now())))
        .orElse("agora mesmo");
  }

  /** Live information about a single player. */
  public List<InfoEntry> playerEntries(Player player) {
    var location = player.getLocation();
    return List.of(
        InfoEntry.head(
            player.getUniqueId(), "<yellow>" + player.getName(), "<gray>InformaÃ§Ãµes do jogador."),
        InfoEntry.of(
            Material.GOLDEN_APPLE,
            "<yellow>Vida",
            "<gray>" + (int) Math.round(player.getHealth()) + " <red>â¤"),
        InfoEntry.of(
            Material.COOKED_BEEF,
            "<yellow>Fome",
            "<gray>" + player.getFoodLevel() + " <dark_gray>/ <gray>20"),
        InfoEntry.of(Material.EXPERIENCE_BOTTLE, "<yellow>NÃ­vel", "<gray>" + player.getLevel()),
        InfoEntry.of(
            Material.GRASS_BLOCK,
            "<yellow>Modo de jogo",
            "<gray>" + gameModeName(player.getGameMode())),
        InfoEntry.of(Material.MAP, "<yellow>Mundo", "<gray>" + player.getWorld().getName()),
        InfoEntry.of(
            Material.COMPASS,
            "<yellow>LocalizaÃ§Ã£o",
            "<gray>"
                + Numbers.compact(location.getX())
                + ", "
                + Numbers.compact(location.getY())
                + ", "
                + Numbers.compact(location.getZ())),
        InfoEntry.of(Material.FEATHER, "<yellow>Ping", "<gray>" + player.getPing() + " ms"),
        InfoEntry.of(
            Material.CLOCK, "<yellow>Tempo de sessÃ£o", "<gray>" + sessionDuration(player)));
  }

  /** Information about the Essentialist plugin itself. */
  public List<InfoEntry> aboutEntries() {
    var meta = plugin.getPluginMeta();
    return List.of(
        InfoEntry.of(
            Material.NETHER_STAR,
            "<yellow>" + meta.getName(),
            "<gray>VersÃ£o <white>" + meta.getVersion()),
        InfoEntry.of(
            Material.WRITABLE_BOOK, "<yellow>Autor", "<gray>" + authors(meta.getAuthors())),
        InfoEntry.of(
            Material.GRASS_BLOCK, "<yellow>Minecraft", "<gray>" + Bukkit.getMinecraftVersion()));
  }
}
