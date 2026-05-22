package com.hanielcota.essentials.modules.whitelist.menu;

import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.whitelist.config.WhitelistConfig;
import com.hanielcota.essentials.modules.whitelist.service.WhitelistService;
import java.util.Objects;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;

/** Renders a whitelisted player as a head item. */
public record WhitelistEntryRenderer(ConfigHandle<WhitelistConfig> config) {

  public WhitelistEntryRenderer {
    Objects.requireNonNull(config, "config");
  }

  public ItemTemplate render(OfflinePlayer player) {
    Objects.requireNonNull(player, "player");
    var snap = config.value();
    String name = WhitelistService.nameOf(player);
    return ItemTemplate.builder(Material.PLAYER_HEAD)
        .head(player.getUniqueId())
        .name(snap.formatItemName(name))
        .lore(snap.formatLore(name).toArray(String[]::new))
        .italic(false)
        .build();
  }
}
