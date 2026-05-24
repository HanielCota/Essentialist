package com.hanielcota.essentials.modules.info.service;

import com.hanielcota.essentials.modules.info.presentation.InfoEntry;
import com.hanielcota.essentials.modules.info.presentation.PlayerInfoEntries;
import com.hanielcota.essentials.modules.info.presentation.PluginInfoEntries;
import com.hanielcota.essentials.modules.info.presentation.ServerInfoEntries;
import com.hanielcota.essentials.user.UserSessionService;
import java.util.List;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class InfoService {

  private final ServerInfoEntries server;
  private final PlayerInfoEntries player;
  private final PluginInfoEntries plugin;

  public InfoService(@NonNull Plugin plugin, @NonNull UserSessionService sessions) {
    this.server = new ServerInfoEntries();
    this.player = new PlayerInfoEntries(sessions);
    this.plugin = new PluginInfoEntries(plugin);
  }

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
