package com.hanielcota.essentials.modules.teleport.history;

import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import org.bukkit.Location;

public interface TeleportHistory {

  int CAPACITY = 5;

  void push(@NonNull UUID player, @NonNull Location location, @NonNull Cause cause);

  List<HistoryEntry> list(@NonNull UUID player);

  void remove(@NonNull UUID player, long entryId);

  /** Why a location was captured into history — drives the {@code /back} menu filter. */
  enum Cause {
    DEATH,
    TELEPORT
  }

  record HistoryEntry(long id, Location location, long createdAt, Cause cause) {}
}
