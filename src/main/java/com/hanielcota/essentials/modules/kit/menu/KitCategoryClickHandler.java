package com.hanielcota.essentials.modules.kit.menu;

import com.github.hanielcota.menuframework.api.ClickContext;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/** Opens the kit list for the clicked category. */
@RequiredArgsConstructor
public final class KitCategoryClickHandler {

  private final KitMenuState state;

  public void open(@NonNull ClickContext click, @NonNull String categoryId) {
    var uuid = click.player().getUniqueId();

    this.state.setCategory(uuid, categoryId);
    click.switchTo(KitListMenu.ID);
  }
}
