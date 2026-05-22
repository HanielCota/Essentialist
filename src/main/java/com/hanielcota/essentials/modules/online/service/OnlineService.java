package com.hanielcota.essentials.modules.online.service;

import org.bukkit.Bukkit;

public final class OnlineService {

  public int onlineCount() {
    return Bukkit.getOnlinePlayers().size();
  }

  public int maxPlayers() {
    return Bukkit.getMaxPlayers();
  }
}
