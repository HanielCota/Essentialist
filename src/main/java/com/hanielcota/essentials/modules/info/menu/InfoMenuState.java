package com.hanielcota.essentials.modules.info.menu;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;
import org.bukkit.entity.Player;

public final class InfoMenuState {

  private final Map<UUID, InfoTab> openTab = new ConcurrentHashMap<>();
  private final Map<UUID, UUID> playerTarget = new ConcurrentHashMap<>();

  public void prepare(@NonNull UUID viewer, @NonNull UUID target) {
    this.playerTarget.put(viewer, target);
    this.openTab.put(viewer, viewer.equals(target) ? InfoTab.CATEGORIES : InfoTab.PLAYER);
  }

  public InfoTab tab(@NonNull UUID viewer) {
    return this.openTab.getOrDefault(viewer, InfoTab.CATEGORIES);
  }

  public void switchTab(@NonNull UUID viewer, @NonNull InfoTab tab) {
    this.openTab.put(viewer, tab);
  }

  public Player resolveTarget(@NonNull Player viewer) {
    UUID targetId = this.playerTarget.getOrDefault(viewer.getUniqueId(), viewer.getUniqueId());
    Player target = org.bukkit.Bukkit.getPlayer(targetId);
    return target != null ? target : viewer;
  }

  public void clear(@NonNull UUID viewer) {
    this.openTab.remove(viewer);
    this.playerTarget.remove(viewer);
  }
}
