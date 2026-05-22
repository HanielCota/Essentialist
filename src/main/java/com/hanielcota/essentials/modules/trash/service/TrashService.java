package com.hanielcota.essentials.modules.trash.service;

import com.hanielcota.essentials.util.ComponentUtils;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public final class TrashService {

  /**
   * Opens a standalone disposal inventory for {@code player}. The inventory has no holder, so
   * nothing persists it — whatever is placed inside is discarded when the menu is closed.
   */
  public void openTrash(Player player, int size, String title) {
    Objects.requireNonNull(player, "player");
    Objects.requireNonNull(title, "title");

    Inventory trash = Bukkit.createInventory(null, size, ComponentUtils.mini(title));
    player.openInventory(trash);
  }
}
