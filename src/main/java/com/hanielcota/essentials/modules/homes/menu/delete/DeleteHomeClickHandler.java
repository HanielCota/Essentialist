package com.hanielcota.essentials.modules.homes.menu.delete;

import com.github.hanielcota.menuframework.api.ClickContext;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import com.hanielcota.essentials.modules.homes.menu.HomesActionTarget;
import com.hanielcota.essentials.modules.homes.service.HomeService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class DeleteHomeClickHandler {

  private final ConfigHandle<HomesConfig> config;
  private final HomeService service;
  private final HomesActionTarget target;

  public void confirm(@NonNull ClickContext click) {
    var player = click.player();
    var uuid = player.getUniqueId();
    var homeName = this.target.consume(uuid);

    if (homeName == null) {
      click.switchTo(HomesMenu.ID);
      return;
    }

    var messages = this.config.value().messages();

    if (!this.service.delete(uuid, homeName)) {
      var unknownHomeMsg = messages.unknownHome();
      var unknownMsg = unknownHomeMsg.replace("{name}", homeName);
      click.reply(unknownMsg);
      click.switchTo(HomesMenu.ID);
      return;
    }

    var homeDeletedMsg = messages.homeDeleted();
    var deletedMsg = homeDeletedMsg.replace("{name}", homeName);
    click.reply(deletedMsg);
    click.switchTo(HomesMenu.ID);
  }

  public void cancel(@NonNull ClickContext click) {
    var uuid = click.player().getUniqueId();

    this.target.clear(uuid);
    click.switchTo(HomesMenu.ID);
  }
}
