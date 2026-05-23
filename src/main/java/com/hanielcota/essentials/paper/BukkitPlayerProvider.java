package com.hanielcota.essentials.paper;

import com.hanielcota.essentials.EssentialsPlugin;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public record BukkitPlayerProvider(EssentialsPlugin plugin) implements PlayerProvider {

  @Override
  public Optional<Player> online(UUID id) {
    return Optional.ofNullable(plugin.getServer().getPlayer(id));
  }

  @Override
  public Optional<Player> online(String name) {
    return Optional.ofNullable(plugin.getServer().getPlayerExact(name));
  }

  @Override
  public OfflinePlayer offline(UUID id) {
    return plugin.getServer().getOfflinePlayer(id);
  }

  @Override
  public Optional<OfflinePlayer> offlineByName(String name) {
    Player current = plugin.getServer().getPlayerExact(name);
    if (current != null) {
      return Optional.of(current);
    }
    return Optional.ofNullable(plugin.getServer().getOfflinePlayerIfCached(name));
  }

  @Override
  public Collection<Player> all() {
    return List.copyOf(plugin.getServer().getOnlinePlayers());
  }
}
