package com.hanielcota.essentials.modules.trash.command;

import com.hanielcota.essentials.shared.ComponentUtils;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

/**
 * Creates the throwaway inventory used by {@code /lixo}. Encapsulates the static
 * {@code Bukkit.createInventory} call so the command stays decoupled from the static API.
 */
public final class TrashInventoryFactory {

  public Inventory create(int size, @NonNull String miniTitle) {
    var titleComponent = ComponentUtils.mini(miniTitle);
    return create(size, titleComponent);
  }

  public Inventory create(int size, @NonNull Component title) {
    // No holder: nothing persists the inventory, so whatever is placed inside is discarded when
    // the menu closes.
    return Bukkit.createInventory(null, size, title);
  }
}
