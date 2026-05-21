package com.hanielcota.essentials.modules.teleport.service;

import com.hanielcota.essentials.modules.teleport.history.TeleportHistory;
import java.util.Objects;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public record TeleportService(TeleportHistory history) {

  public TeleportService {
    Objects.requireNonNull(history, "history");
  }

  public boolean teleportTo(Player who, Location destination) {
    Objects.requireNonNull(who, "who");
    Objects.requireNonNull(destination, "destination");
    Location from = who.getLocation();
    if (!who.teleport(destination)) {
      return false;
    }
    history.push(who.getUniqueId(), from);
    return true;
  }
}
