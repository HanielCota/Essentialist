package com.hanielcota.essentials.modules.teleport.service;

import java.util.Objects;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class TeleportService {

  public boolean teleportTo(Player who, Location destination) {
    Objects.requireNonNull(who, "who");
    Objects.requireNonNull(destination, "destination");
    return who.teleport(destination);
  }
}
