package com.hanielcota.essentials.modules.vanish.menu;

import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.vanish.config.VanishConfig;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public record VanishEntryRenderer(ConfigHandle<VanishConfig> config) {

  public ItemTemplate render(@NonNull Player player) {
    var snap = this.config.value();
    var name = player.getName();
    var location = player.getLocation();
    var world = location.getWorld();
    var worldName = world != null ? world.getName() : "?";

    return ItemTemplate.builder(Material.PLAYER_HEAD)
        .head(player.getUniqueId())
        .name(snap.formatItemName(name))
        .lore(
            snap.formatItemLore(name, worldName, location.getX(), location.getY(), location.getZ())
                .toArray(String[]::new))
        .italic(false)
        .build();
  }

  public ItemTemplate renderEmpty() {
    var snap = this.config.value();
    return ItemTemplate.builder(snap.emptyMaterial())
        .name(snap.emptyName())
        .lore(snap.emptyLore().toArray(String[]::new))
        .italic(false)
        .build();
  }
}
