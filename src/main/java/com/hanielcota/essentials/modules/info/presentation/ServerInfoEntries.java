package com.hanielcota.essentials.modules.info.presentation;

import com.hanielcota.essentials.util.DurationFormatter;
import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import org.bukkit.Bukkit;
import org.bukkit.Material;

public final class ServerInfoEntries {

  private static final long BYTES_PER_MB = 1024L * 1024L;
  private static final String GRAY = "<gray>";

  public List<InfoEntry> entries() {
    var onlineCount = Bukkit.getOnlinePlayers().size();
    var maxPlayers = Bukkit.getMaxPlayers();
    var playerFormat = onlineCount + " <dark_gray>/ <gray>" + maxPlayers;

    var worldCount = Bukkit.getWorlds().size();

    return List.of(
        InfoEntry.of(Material.CLOCK, "<yellow>TPS", GRAY + formattedTps()),
        InfoEntry.of(Material.PLAYER_HEAD, "<yellow>Jogadores online", GRAY + playerFormat),
        InfoEntry.of(Material.NAME_TAG, "<yellow>Versão", GRAY + Bukkit.getVersion()),
        InfoEntry.of(Material.COMPARATOR, "<yellow>Tempo ligado", GRAY + formattedUptime()),
        InfoEntry.of(Material.REDSTONE, "<yellow>Memória", GRAY + formattedMemory()),
        InfoEntry.of(Material.GRASS_BLOCK, "<yellow>Mundos", GRAY + worldCount + " carregado(s)"));
  }

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
}
