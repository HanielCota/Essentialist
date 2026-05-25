package com.hanielcota.essentials.modules.info.menu;

import com.hanielcota.essentials.paper.PlayerProvider;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class InfoMenuState {

  private final PlayerProvider players;

  private final Map<UUID, InfoTab> openTab = new ConcurrentHashMap<>();
  private final Map<UUID, UUID> playerTarget = new ConcurrentHashMap<>();

  public void prepare(@NonNull UUID viewer, @NonNull UUID target) {
    this.playerTarget.put(viewer, target);

    var initialTab = viewer.equals(target) ? InfoTab.CATEGORIES : InfoTab.PLAYER;

    this.openTab.put(viewer, initialTab);
  }

  public InfoTab tab(@NonNull UUID viewer) {
    return this.openTab.getOrDefault(viewer, InfoTab.CATEGORIES);
  }

  public void switchTab(@NonNull UUID viewer, @NonNull InfoTab tab) {
    this.openTab.put(viewer, tab);
  }

  public Player resolveTarget(@NonNull Player viewer) {
    var viewerId = viewer.getUniqueId();
    var targetId = this.playerTarget.getOrDefault(viewerId, viewerId);

    return this.players.online(targetId).orElse(viewer);
  }

  public void clear(@NonNull UUID viewer) {
    this.openTab.remove(viewer);
    this.playerTarget.remove(viewer);
  }
}
