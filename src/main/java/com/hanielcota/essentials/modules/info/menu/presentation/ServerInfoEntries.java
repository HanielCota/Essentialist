package com.hanielcota.essentials.modules.info.menu.presentation;

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

  private static String formattedTps() {
    var rawTps = Bukkit.getTPS()[0];
    var tps = Math.min(20.0, rawTps);

    return String.format(Locale.US, "%.2f", tps);
  }

  private static String formattedUptime() {
    var runtimeMx = ManagementFactory.getRuntimeMXBean();
    var uptimeMs = runtimeMx.getUptime();
    var uptime = Duration.ofMillis(uptimeMs);

    return DurationFormatter.format(uptime);
  }

  private static String formattedMemory() {
    var runtime = Runtime.getRuntime();
    var totalBytes = runtime.totalMemory();
    var freeBytes = runtime.freeMemory();
    var maxBytes = runtime.maxMemory();

    var usedMb = (totalBytes - freeBytes) / BYTES_PER_MB;
    var maxMb = maxBytes / BYTES_PER_MB;

    return usedMb + " MB <dark_gray>/ <gray>" + maxMb + " MB";
  }

  public List<InfoEntry> entries() {
    var onlinePlayers = Bukkit.getOnlinePlayers();
    var onlineCount = onlinePlayers.size();
    var maxPlayers = Bukkit.getMaxPlayers();
    var playerFormat = onlineCount + " <dark_gray>/ <gray>" + maxPlayers;

    var worlds = Bukkit.getWorlds();
    var worldCount = worlds.size();

    var version = Bukkit.getVersion();
    var tps = formattedTps();
    var uptime = formattedUptime();
    var memory = formattedMemory();

    var tpsLore = GRAY + tps;
    var playersLore = GRAY + playerFormat;
    var versionLore = GRAY + version;
    var uptimeLore = GRAY + uptime;
    var memoryLore = GRAY + memory;
    var worldsLore = GRAY + worldCount + " carregado(s)";

    var tpsEntry = InfoEntry.of(Material.CLOCK, "<yellow>TPS", tpsLore);
    var playersEntry = InfoEntry.of(Material.PLAYER_HEAD, "<yellow>Jogadores online", playersLore);
    var versionEntry = InfoEntry.of(Material.NAME_TAG, "<yellow>Versão", versionLore);
    var uptimeEntry = InfoEntry.of(Material.COMPARATOR, "<yellow>Tempo ligado", uptimeLore);
    var memoryEntry = InfoEntry.of(Material.REDSTONE, "<yellow>Memória", memoryLore);
    var worldsEntry = InfoEntry.of(Material.GRASS_BLOCK, "<yellow>Mundos", worldsLore);

    return List.of(tpsEntry, playersEntry, versionEntry, uptimeEntry, memoryEntry, worldsEntry);
  }
}
