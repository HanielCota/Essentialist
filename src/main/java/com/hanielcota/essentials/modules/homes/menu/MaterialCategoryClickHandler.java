package com.hanielcota.essentials.modules.homes.menu;

import com.github.hanielcota.menuframework.api.ClickContext;
import com.hanielcota.essentials.modules.homes.menu.presentation.MaterialCategory;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class MaterialCategoryClickHandler {

  private final HomesActionTarget target;

  public void handle(@NonNull ClickContext click, @NonNull MaterialCategory category) {
    var player = click.player();
    this.target.setCategory(player.getUniqueId(), category);
    click.switchTo(MaterialPickerMenu.ID);
  }
}
