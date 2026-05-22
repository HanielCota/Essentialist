package com.hanielcota.essentials.modules.whitelist.menu;

import com.github.hanielcota.menuframework.api.ClickContext;
import com.github.hanielcota.menuframework.api.ItemClickHandler;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.whitelist.config.WhitelistConfig;
import com.hanielcota.essentials.modules.whitelist.service.WhitelistService;
import java.util.Objects;
import org.bukkit.OfflinePlayer;
import org.jspecify.annotations.NonNull;

/** Removes a player from the whitelist when their head is clicked, then refreshes the menu. */
public record WhitelistClickHandler(ConfigHandle<WhitelistConfig> config, WhitelistService service)
    implements ItemClickHandler<OfflinePlayer> {

  public WhitelistClickHandler {
    Objects.requireNonNull(config, "config");
    Objects.requireNonNull(service, "service");
  }

  @Override
  public void handle(@NonNull ClickContext click, @NonNull OfflinePlayer player) {
    service.remove(player);
    click.reply(config.value().formatRemoved(WhitelistService.nameOf(player)));
    click.refresh();
  }
}
