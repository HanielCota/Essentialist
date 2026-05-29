package com.hanielcota.essentials.modules.kit.menu;

import com.github.hanielcota.menuframework.api.ClickContext;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/** Routes the kit list: open a kit's preview, or go back to the categories. */
@RequiredArgsConstructor
public final class KitListClickHandler {

  private final KitMenuState state;

  public void select(@NonNull ClickContext click, @NonNull String kitId) {
    var uuid = click.player().getUniqueId();

    this.state.setKit(uuid, kitId);
    click.switchTo(KitPreviewMenu.ID);
  }

  public void back(@NonNull ClickContext click) {
    click.switchTo(KitCategoryMenu.ID);
  }
}
