package com.hanielcota.essentials.paper;

import com.hanielcota.essentials.EssentialsPlugin;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public record BukkitPlayerProvider(EssentialsPlugin plugin) implements PlayerProvider {

  public BukkitPlayerProvider(@NonNull EssentialsPlugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public Optional<Player> online(@NonNull UUID id) {
    var server = plugin.getServer();
    var player = server.getPlayer(id);

    return Optional.ofNullable(player);
  }

  @Override
  public Optional<Player> online(@NonNull String name) {
    var server = plugin.getServer();
    var player = server.getPlayerExact(name);

    return Optional.ofNullable(player);
  }

  @Override
  public OfflinePlayer offline(@NonNull UUID id) {
    var server = plugin.getServer();
    return server.getOfflinePlayer(id);
  }

  @Override
  public Optional<OfflinePlayer> offlineByName(@NonNull String name) {
    var server = plugin.getServer();
    var current = server.getPlayerExact(name);

    if (current != null) {
      return Optional.of(current);
    }

    var cachedOffline = server.getOfflinePlayerIfCached(name);
    return Optional.ofNullable(cachedOffline);
  }

  @Override
  public Collection<Player> all() {
    var server = plugin.getServer();
    var onlinePlayers = server.getOnlinePlayers();

    return List.copyOf(onlinePlayers);
  }
}
