package com.hanielcota.essentials.modules.enderchest.service;

import lombok.NonNull;
import org.bukkit.entity.Player;

public final class EnderChestService {

  /** Opens {@code target}'s live ender chest for {@code viewer} — edits affect the real one. */
  public void open(@NonNull Player viewer, @NonNull Player target) {

    viewer.openInventory(target.getEnderChest());
  }
}
