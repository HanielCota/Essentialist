package com.hanielcota.essentials.modules.homes.menu.material;

import com.github.hanielcota.menuframework.api.ClickContext;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import com.hanielcota.essentials.modules.homes.menu.presentation.MaterialPickerPresentation;
import com.hanielcota.essentials.modules.homes.service.HomeService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;

@RequiredArgsConstructor
public final class MaterialPickerClickHandler {

  private final ConfigHandle<HomesConfig> config;
  private final HomeService service;
  private final HomesActionTarget target;
  private final MaterialPickerPresentation presentation;

  public void pick(@NonNull ClickContext click, @NonNull Material pickedMaterial) {
    var player = click.player();
    var uuid = player.getUniqueId();
    var homeName = this.target.consume(uuid);
    this.target.consumeCategory(uuid);

    if (homeName == null) {
      click.switchTo(HomesMenu.ID);
      return;
    }

    var messages = this.config.value().messages();
    var applied = this.service.setMaterial(uuid, homeName, pickedMaterial);
    var replyText = this.presentation.reply(messages, homeName, pickedMaterial, applied);
    click.reply(replyText);
    click.switchTo(HomesMenu.ID);
  }

  public void back(@NonNull ClickContext click) {
    this.target.consumeCategory(click.player().getUniqueId());
    click.switchTo(MaterialCategoryMenu.ID);
  }
}
