package com.hanielcota.essentials.modules.homes.config.menu;

import com.hanielcota.essentials.menu.MenuLayouts;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DeleteDialogSection {

  public static int rows(@NonNull HomesDeleteDialogConfig snap) {
    return MenuLayouts.clampRows(snap.rows());
  }

  public static int promptSlot(@NonNull HomesDeleteDialogConfig snap) {
    return MenuLayouts.sanitizeSlot(snap.promptSlot(), rows(snap), 13);
  }

  public static int yesSlot(@NonNull HomesDeleteDialogConfig snap) {
    return MenuLayouts.sanitizeSlot(snap.yesSlot(), rows(snap), 11);
  }

  public static int noSlot(@NonNull HomesDeleteDialogConfig snap) {
    return MenuLayouts.sanitizeSlot(snap.noSlot(), rows(snap), 15);
  }
}
