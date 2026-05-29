package com.hanielcota.essentials.modules.skull.service;

import com.hanielcota.essentials.modules.skull.domain.SkullDelivery;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public final class SkullService {

  public SkullDelivery deliver(@NonNull Player recipient, @NonNull OfflinePlayer owner) {
    var skull = createSkull(owner);
    var inventory = recipient.getInventory();
    var leftover = inventory.addItem(skull);
    var full = !leftover.isEmpty();

    return new SkullDelivery(full);
  }

  private ItemStack createSkull(@NonNull OfflinePlayer owner) {
    var skull = new ItemStack(Material.PLAYER_HEAD);
    var meta = (SkullMeta) skull.getItemMeta();

    meta.setOwningPlayer(owner);
    skull.setItemMeta(meta);

    return skull;
  }
}
