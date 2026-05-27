package com.hanielcota.essentials.modules.vanish.menu;

import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.vanish.config.VanishConfig;
import com.hanielcota.essentials.modules.vanish.config.VanishMessages;
import com.hanielcota.essentials.shared.PlayerHeadTextures;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public record VanishEntryRenderer(ConfigHandle<VanishConfig> config) {

  private static final String UNKNOWN_WORLD = "?";

  public ItemTemplate render(@NonNull Player player) {
    var snap = this.config.value();

    var name = player.getName();
    var location = player.getLocation();
    var world = location.getWorld();
    var worldName = world != null ? world.getName() : UNKNOWN_WORLD;
    var x = location.getX();
    var y = location.getY();
    var z = location.getZ();

    var displayName = VanishMessages.itemName(snap, name);
    var loreList = VanishMessages.itemLore(snap, name, worldName, x, y, z);
    var loreArray = loreList.toArray(String[]::new);

    var builder = ItemTemplate.builder(Material.PLAYER_HEAD);
    PlayerHeadTextures.applyTo(builder, player);
    builder.name(displayName);
    builder.lore(loreArray);
    builder.italic(false);

    return builder.build();
  }

  public ItemTemplate renderEmpty() {
    var snap = this.config.value();

    var material = snap.emptyMaterial();
    var name = snap.emptyName();
    var loreList = snap.emptyLore();
    var loreArray = loreList.toArray(String[]::new);

    var builder = ItemTemplate.builder(material);
    builder.name(name);
    builder.lore(loreArray);
    builder.italic(false);

    return builder.build();
  }
}
