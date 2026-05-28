package com.hanielcota.essentials.modules.homes.menu.material;

import com.github.hanielcota.menuframework.api.ClickContext;
import com.hanielcota.essentials.modules.homes.menu.presentation.MaterialCategory;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class MaterialCategoryClickHandler {

  private final HomesActionTarget target;

  public void handle(@NonNull ClickContext click, @NonNull MaterialCategory category) {
    var player = click.player();
    var uuid = player.getUniqueId();

    this.target.setCategory(uuid, category);
    click.switchTo(MaterialPickerMenu.ID);
  }

  public void back(@NonNull ClickContext click) {
    var player = click.player();
    var uuid = player.getUniqueId();

    this.target.clear(uuid);
    click.switchTo(HomesMenu.ID);
  }
}
