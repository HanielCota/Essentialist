package com.hanielcota.essentials.modules.teleport.service;

import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class TeleportService {

  public boolean teleportTo(@NonNull Player who, @NonNull Location destination) {
    return who.teleport(destination);
  }
}
