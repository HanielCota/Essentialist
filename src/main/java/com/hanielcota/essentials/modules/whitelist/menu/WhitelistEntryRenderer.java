package com.hanielcota.essentials.modules.whitelist.menu;

import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.whitelist.config.WhitelistConfig;
import com.hanielcota.essentials.modules.whitelist.service.WhitelistService;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;

/** Renders the items shown in the whitelist menu. */
public record WhitelistEntryRenderer(ConfigHandle<WhitelistConfig> config) {

  /** A whitelisted player as a head item. */
  public ItemTemplate render(@NonNull OfflinePlayer player) {
    var snap = this.config.value();
    var name = WhitelistService.nameOf(player);
    var ownerId = player.getUniqueId();

    var itemName = snap.formatItemName(name);
    var lore = snap.formatLore(name);
    var loreArray = lore.toArray(String[]::new);

    var builder = ItemTemplate.builder(Material.PLAYER_HEAD);
    builder = builder.head(ownerId);
    builder = builder.name(itemName);
    builder = builder.lore(loreArray);
    builder = builder.italic(false);

    return builder.build();
  }

  /** The placeholder item shown when the whitelist has no players. */
  public ItemTemplate renderEmpty() {
    var snap = this.config.value();
    var material = snap.emptyMaterial();
    var name = snap.emptyName();
    var lore = snap.emptyLore();
    var loreArray = lore.toArray(String[]::new);

    var builder = ItemTemplate.builder(material);
    builder = builder.name(name);
    builder = builder.lore(loreArray);
    builder = builder.italic(false);

    return builder.build();
  }
}
