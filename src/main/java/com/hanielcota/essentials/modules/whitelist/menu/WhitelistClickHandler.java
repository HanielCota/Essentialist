package com.hanielcota.essentials.modules.whitelist.menu;

import com.github.hanielcota.menuframework.api.ClickContext;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.whitelist.config.WhitelistConfig;
import com.hanielcota.essentials.modules.whitelist.service.WhitelistService;
import lombok.NonNull;
import org.bukkit.OfflinePlayer;

/** Removes a player from the whitelist when their head is clicked, then refreshes the menu. */
public record WhitelistClickHandler(
    ConfigHandle<WhitelistConfig> config, WhitelistService service) {

  public void handle(@NonNull ClickContext click, @NonNull OfflinePlayer player) {
    this.service.remove(player);

    var snap = this.config.value();
    var playerName = WhitelistService.nameOf(player);
    var removedMsg = snap.messages().formatRemoved(playerName);

    click.reply(removedMsg);
    click.refresh();
  }
}
