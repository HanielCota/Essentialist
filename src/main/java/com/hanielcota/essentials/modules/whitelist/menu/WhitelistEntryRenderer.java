package com.hanielcota.essentials.modules.whitelist.menu;

import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.whitelist.config.WhitelistConfig;
import com.hanielcota.essentials.modules.whitelist.service.WhitelistService;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;

/** Renders the items shown in the whitelist menu. */
public record WhitelistEntryRenderer(ConfigHandle<WhitelistConfig> config) {

  /** A whitelisted player as a head item. */
  public ItemTemplate render(OfflinePlayer player) {
    var snap = config.value();
    String name = WhitelistService.nameOf(player);
    return ItemTemplate.builder(Material.PLAYER_HEAD)
        .head(player.getUniqueId())
        .name(snap.formatItemName(name))
        .lore(snap.formatLore(name).toArray(String[]::new))
        .italic(false)
        .build();
  }

  /** The placeholder item shown when the whitelist has no players. */
  public ItemTemplate renderEmpty() {
    var snap = config.value();
    return ItemTemplate.builder(snap.emptyMaterial())
        .name(snap.emptyName())
        .lore(snap.emptyLore().toArray(String[]::new))
        .italic(false)
        .build();
  }
}
