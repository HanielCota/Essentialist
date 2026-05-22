package com.hanielcota.essentials.paper;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public interface PlayerProvider {

  Optional<Player> online(UUID id);

  Optional<Player> online(String name);

  OfflinePlayer offline(UUID id);

  /** Resolves {@code name} to an online or previously-seen offline player; empty if unknown. */
  Optional<OfflinePlayer> offlineByName(String name);

  Collection<Player> all();
}
