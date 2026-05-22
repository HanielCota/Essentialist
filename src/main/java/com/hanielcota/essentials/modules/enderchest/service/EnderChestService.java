package com.hanielcota.essentials.modules.enderchest.service;

import java.util.Objects;
import org.bukkit.entity.Player;

public final class EnderChestService {

  /** Opens {@code target}'s live ender chest for {@code viewer} — edits affect the real one. */
  public void open(Player viewer, Player target) {
    Objects.requireNonNull(viewer, "viewer");
    Objects.requireNonNull(target, "target");

    viewer.openInventory(target.getEnderChest());
  }
}
