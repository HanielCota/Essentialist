package com.hanielcota.essentials.modules.warps.menu;

import com.github.hanielcota.menuframework.api.ClickContext;
import com.hanielcota.essentials.modules.warps.domain.Warp;
import com.hanielcota.essentials.modules.warps.service.WarpSelection;
import com.hanielcota.essentials.modules.warps.service.WarpTeleportService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class WarpClickHandler {

  private final WarpTeleportService teleportService;
  private final WarpSelection selection;

  public void handle(@NonNull ClickContext click, @NonNull Warp warp) {
    var player = click.player();

    if (click.clickType().isRightClick()) {
      this.selection.select(player.getUniqueId(), warp.name());
      click.open(WarpActionsMenu.ID);
      return;
    }

    click.close();
    this.teleportService.teleport(player, warp);
  }
}
