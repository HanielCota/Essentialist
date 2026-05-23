package com.hanielcota.essentials.modules.teleport.history;

import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import org.bukkit.Location;

public interface TeleportHistory {

  int CAPACITY = 5;

  void push(@NonNull UUID player, @NonNull Location location);

  List<HistoryEntry> list(@NonNull UUID player);

  void remove(@NonNull UUID player, long entryId);

  record HistoryEntry(long id, Location location, long createdAt) {}
}
