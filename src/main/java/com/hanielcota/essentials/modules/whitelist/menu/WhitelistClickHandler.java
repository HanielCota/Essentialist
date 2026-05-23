package com.hanielcota.essentials.modules.whitelist.menu;

import com.github.hanielcota.menuframework.api.ClickContext;
import com.github.hanielcota.menuframework.api.ItemClickHandler;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.whitelist.config.WhitelistConfig;
import com.hanielcota.essentials.modules.whitelist.service.WhitelistService;
import lombok.NonNull;
import org.bukkit.OfflinePlayer;

/** Removes a player from the whitelist when their head is clicked, then refreshes the menu. */
public record WhitelistClickHandler(ConfigHandle<WhitelistConfig> config, WhitelistService service)
    implements ItemClickHandler<OfflinePlayer> {

  @Override
  public void handle(@NonNull ClickContext click, @NonNull OfflinePlayer player) {
    this.service.remove(player);

    var playerName = WhitelistService.nameOf(player);
    var removedMsg = this.config.value().formatRemoved(playerName);
    click.reply(removedMsg);
    click.refresh();
  }
}
