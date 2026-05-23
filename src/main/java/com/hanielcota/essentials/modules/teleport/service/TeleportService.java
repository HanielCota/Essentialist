package com.hanielcota.essentials.modules.teleport.service;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class TeleportService {

  public boolean teleportTo(Player who, Location destination) {
    return who.teleport(destination);
  }
}
