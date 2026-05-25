package com.hanielcota.essentials.modules.homes.config.menu;

import com.hanielcota.essentials.menu.MenuLayouts;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Layout helpers for the delete-confirmation submenu (rows / prompt / yes / no slots). Stateless —
 * every call takes a config snapshot.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DeleteDialogSection {

  public static int rows(@NonNull HomesMenuConfig snap) {
    return MenuLayouts.clampRows(snap.deleteRows());
  }

  public static int promptSlot(@NonNull HomesMenuConfig snap) {
    return MenuLayouts.sanitizeSlot(snap.deletePromptSlot(), rows(snap), 13);
  }

  public static int yesSlot(@NonNull HomesMenuConfig snap) {
    return MenuLayouts.sanitizeSlot(snap.deleteYesSlot(), rows(snap), 11);
  }

  public static int noSlot(@NonNull HomesMenuConfig snap) {
    return MenuLayouts.sanitizeSlot(snap.deleteNoSlot(), rows(snap), 15);
  }
}
