package com.hanielcota.essentials.modules.info.menu.presentation;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.info.config.InfoConfig;
import com.hanielcota.essentials.shared.DurationFormatter;
import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;

@RequiredArgsConstructor
public final class ServerInfoEntries {

  private static final long BYTES_PER_MB = 1024L * 1024L;

  private final ConfigHandle<InfoConfig> config;

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

  public List<InfoEntry> entries() {
    var snap = this.config.value();
    var section = snap.server();

    var tps = formattedTps();
    var uptime = formattedUptime();

    var onlinePlayers = Bukkit.getOnlinePlayers();
    var onlineCount = onlinePlayers.size();
    var maxPlayers = Bukkit.getMaxPlayers();

    var version = Bukkit.getVersion();
    var worldCount = Bukkit.getWorlds().size();

    var runtime = Runtime.getRuntime();
    var totalBytes = runtime.totalMemory();
    var freeBytes = runtime.freeMemory();
    var maxBytes = runtime.maxMemory();
    var usedMb = (totalBytes - freeBytes) / BYTES_PER_MB;
    var maxMb = maxBytes / BYTES_PER_MB;

    var tpsEntry = InfoEntry.from(section.tps(), Map.of("tps", tps));
    var playersEntry =
        InfoEntry.from(section.players(), Map.of("online", onlineCount, "max", maxPlayers));
    var versionEntry = InfoEntry.from(section.version(), Map.of("version", version));
    var uptimeEntry = InfoEntry.from(section.uptime(), Map.of("uptime", uptime));
    var memoryEntry = InfoEntry.from(section.memory(), Map.of("usedMb", usedMb, "maxMb", maxMb));
    var worldsEntry = InfoEntry.from(section.worlds(), Map.of("count", worldCount));

    return List.of(tpsEntry, playersEntry, versionEntry, uptimeEntry, memoryEntry, worldsEntry);
  }
}
