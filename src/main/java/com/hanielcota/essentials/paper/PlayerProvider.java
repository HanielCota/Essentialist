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

  Collection<? extends Player> all();
}
