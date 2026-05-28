package com.hanielcota.essentials.modules.info.menu.presentation;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.info.config.InfoConfig;
import com.hanielcota.essentials.paper.ServerMetricsProvider;
import com.hanielcota.essentials.shared.DurationFormatter;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ServerInfoEntries {

  private final @NonNull ConfigHandle<InfoConfig> config;
  private final @NonNull ServerMetricsProvider metrics;

  public List<InfoEntry> entries() {
    var snap = this.config.value();
    var section = snap.server();

    var tps = String.format(Locale.US, "%.2f", this.metrics.tps());
    var uptime = DurationFormatter.format(Duration.ofMillis(this.metrics.uptimeMillis()));

    var tpsEntry = InfoEntry.from(section.tps(), Map.of("tps", tps));
    var playersEntry =
        InfoEntry.from(
            section.players(),
            Map.of(
                "online", this.metrics.onlinePlayerCount(),
                "max", this.metrics.maxPlayers()));
    var versionEntry = InfoEntry.from(section.version(), Map.of("version", this.metrics.version()));
    var uptimeEntry = InfoEntry.from(section.uptime(), Map.of("uptime", uptime));
    var memoryEntry =
        InfoEntry.from(
            section.memory(),
            Map.of(
                "usedMb", this.metrics.memoryUsedMb(),
                "maxMb", this.metrics.memoryMaxMb()));
    var worldsEntry = InfoEntry.from(section.worlds(), Map.of("count", this.metrics.worldCount()));

    return List.of(tpsEntry, playersEntry, versionEntry, uptimeEntry, memoryEntry, worldsEntry);
  }
}
