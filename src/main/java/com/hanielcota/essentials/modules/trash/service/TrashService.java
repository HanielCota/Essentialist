package com.hanielcota.essentials.modules.trash.service;

import com.hanielcota.essentials.util.ComponentUtils;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public final class TrashService {

  /**
   * Opens a standalone disposal inventory for {@code player}. The inventory has no holder, so
   * nothing persists it — whatever is placed inside is discarded when the menu is closed.
   */
  public void openTrash(@NonNull Player player, int size, @NonNull String title) {

    var titleComponent = ComponentUtils.mini(title);
    Inventory trash = Bukkit.createInventory(null, size, titleComponent);
    player.openInventory(trash);
  }
}
