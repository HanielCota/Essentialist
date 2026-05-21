package com.hanielcota.essentials.paper;

import com.hanielcota.essentials.EssentialsPlugin;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public record BukkitPlayerProvider(EssentialsPlugin plugin) implements PlayerProvider {

  public BukkitPlayerProvider {
    Objects.requireNonNull(plugin, "plugin");
  }

  @Override
  public Optional<Player> online(UUID id) {
    return Optional.ofNullable(plugin.getServer().getPlayer(Objects.requireNonNull(id, "id")));
  }

  @Override
  public Optional<Player> online(String name) {
    return Optional.ofNullable(
        plugin.getServer().getPlayerExact(Objects.requireNonNull(name, "name")));
  }

  @Override
  public OfflinePlayer offline(UUID id) {
    return plugin.getServer().getOfflinePlayer(Objects.requireNonNull(id, "id"));
  }

  @Override
  public Collection<? extends Player> all() {
    return plugin.getServer().getOnlinePlayers();
  }
}
