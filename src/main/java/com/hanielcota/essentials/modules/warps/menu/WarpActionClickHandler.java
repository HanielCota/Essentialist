package com.hanielcota.essentials.modules.warps.menu;

import com.github.hanielcota.menuframework.api.ClickContext;
import com.hanielcota.essentials.modules.warps.service.WarpFavorites;
import com.hanielcota.essentials.modules.warps.service.WarpLikes;
import com.hanielcota.essentials.modules.warps.service.WarpSelectionResolver;
import com.hanielcota.essentials.modules.warps.service.WarpTeleportService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/** Click logic of the warp action submenu. No rendering here. */
@RequiredArgsConstructor
public final class WarpActionClickHandler {

  private final WarpFavorites favorites;
  private final WarpLikes likes;
  private final WarpTeleportService teleportService;
  private final WarpSelectionResolver resolver;

  public void favorite(@NonNull ClickContext click) {
    var playerId = click.player().getUniqueId();
    this.resolver.resolve(playerId).ifPresent(warp -> this.favorites.toggle(playerId, warp.name()));
    click.refresh();
  }

  public void like(@NonNull ClickContext click) {
    var playerId = click.player().getUniqueId();
    this.resolver.resolve(playerId).ifPresent(warp -> this.likes.toggle(playerId, warp.name()));
    click.refresh();
  }

  public void occupants(@NonNull ClickContext click) {
    click.open(WarpOccupantsMenu.ID);
  }

  public void teleport(@NonNull ClickContext click) {
    var player = click.player();
    var warpOpt = this.resolver.resolve(player.getUniqueId());

    click.close();
    warpOpt.ifPresent(warp -> this.teleportService.teleport(player, warp));
  }

  public void back(@NonNull ClickContext click) {
    click.open(WarpsMenu.ID);
  }
}
