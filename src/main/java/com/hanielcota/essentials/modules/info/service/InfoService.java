package com.hanielcota.essentials.modules.info.service;

import com.hanielcota.essentials.modules.info.presentation.InfoEntry;
import com.hanielcota.essentials.modules.info.presentation.PlayerInfoEntries;
import com.hanielcota.essentials.modules.info.presentation.PluginInfoEntries;
import com.hanielcota.essentials.modules.info.presentation.ServerInfoEntries;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class InfoService {

  private final ServerInfoEntries server;
  private final PlayerInfoEntries player;
  private final PluginInfoEntries plugin;

  /** Server status: TPS, players, version, uptime, memory and worlds. */
  public List<InfoEntry> serverEntries() {
    return this.server.entries();
  }

  /** Live information about a single player. */
  public List<InfoEntry> playerEntries(@NonNull Player player) {
    return this.player.entries(player);
  }

  /** Information about the Essentialist plugin itself. */
  public List<InfoEntry> aboutEntries() {
    return this.plugin.entries();
  }
}
