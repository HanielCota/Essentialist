package com.hanielcota.essentials.modules.warps.menu;

import com.github.hanielcota.menuframework.MenuFramework;
import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.menu.EssentialsMenu;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Per-warp action submenu opened by right-clicking a warp. Pure wiring: slots map to {@link
 * WarpActionRenderer} for items and {@link WarpActionClickHandler} for clicks. The warp it acts on
 * comes from the selection resolver both collaborators share.
 */
@RequiredArgsConstructor
public final class WarpActionsMenu implements EssentialsMenu {

  public static final String ID = "essentials.warps.actions";

  private static final String TITLE = "Gerenciar Warp";
  private static final int ROWS = 3;

  private static final int INFO_SLOT = 4;
  private static final int FAVORITE_SLOT = 11;
  private static final int LIKE_SLOT = 13;
  private static final int OCCUPANTS_SLOT = 15;
  private static final int TELEPORT_SLOT = 21;
  private static final int BACK_SLOT = 23;

  private final WarpActionRenderer renderer;
  private final WarpActionClickHandler clicks;

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void register(@NonNull MenuService menus) {
    var builder = MenuFramework.builder(ID, menus);
    builder.rows(ROWS);
    builder.title(TITLE);
    builder.slot(INFO_SLOT, this.renderer::info);
    builder.slot(FAVORITE_SLOT, this.renderer::favorite, this.clicks::favorite);
    builder.slot(LIKE_SLOT, this.renderer::like, this.clicks::like);
    builder.slot(OCCUPANTS_SLOT, this.renderer::occupants, this.clicks::occupants);
    builder.slot(TELEPORT_SLOT, this.renderer::teleport, this.clicks::teleport);
    builder.slot(BACK_SLOT, this.renderer::back, this.clicks::back);
    builder.buildAndRegister();
  }
}
