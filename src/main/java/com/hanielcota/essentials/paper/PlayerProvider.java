package com.hanielcota.essentials.paper;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public interface PlayerProvider {

  Optional<Player> online(@NonNull UUID id);

  Optional<Player> online(@NonNull String name);

  OfflinePlayer offline(@NonNull UUID id);

  /** Resolves {@code name} to an online or previously-seen offline player; empty if unknown. */
  Optional<OfflinePlayer> offlineByName(@NonNull String name);

  Collection<Player> all();
}
