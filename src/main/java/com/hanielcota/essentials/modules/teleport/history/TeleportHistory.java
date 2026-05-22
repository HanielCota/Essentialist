package com.hanielcota.essentials.modules.teleport.history;

import java.util.List;
import java.util.UUID;
import org.bukkit.Location;

public interface TeleportHistory {

  int CAPACITY = 5;

  void push(UUID player, Location location);

  List<HistoryEntry> list(UUID player);

  void remove(UUID player, long entryId);

  record HistoryEntry(long id, Location location, long createdAt) {}
}
