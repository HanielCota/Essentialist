package com.hanielcota.essentials.modules.kit.menu;

import com.github.hanielcota.menuframework.api.ClickContext;
import com.hanielcota.essentials.modules.kit.service.KitSortPreferences;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/** Routes the kit list: open a kit's preview, cycle the sort, or go back to the categories. */
@RequiredArgsConstructor
public final class KitListClickHandler {

  private final KitMenuState state;
  private final KitSortPreferences sortPreferences;

  public void select(@NonNull ClickContext click, @NonNull String kitId) {
    var uuid = click.player().getUniqueId();

    this.state.setKit(uuid, kitId);
    click.switchTo(KitPreviewMenu.ID);
  }

  public void cycleSort(@NonNull ClickContext click) {
    var uuid = click.player().getUniqueId();

    this.sortPreferences.cycle(uuid);
    click.refresh();
  }

  public void back(@NonNull ClickContext click) {
    click.switchTo(KitCategoryMenu.ID);
  }
}
