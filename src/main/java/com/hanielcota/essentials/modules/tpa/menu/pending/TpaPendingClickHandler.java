package com.hanielcota.essentials.modules.tpa.menu.pending;

import com.github.hanielcota.menuframework.api.ClickContext;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequest;
import com.hanielcota.essentials.modules.tpa.service.selection.TpaPendingSelections;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Routes clicks on the pending menu: clicking a request selects it and switches to {@link
 * TpaPendingActionMenu}; the bulk accept-all / deny-all shortcuts delegate to {@link
 * TpaPendingBulkActions}.
 */
@RequiredArgsConstructor
public final class TpaPendingClickHandler {

  private final TpaPendingSelections selections;
  private final TpaPendingBulkActions bulkActions;

  public void handle(@NonNull ClickContext click, @NonNull TeleportRequest request) {
    var viewerId = click.player().getUniqueId();
    this.selections.select(viewerId, request);

    click.switchTo(TpaPendingActionMenu.ID);
  }

  public void acceptAll(@NonNull ClickContext click) {
    this.bulkActions.acceptAll(click);
  }

  public void denyAll(@NonNull ClickContext click) {
    this.bulkActions.denyAll(click);
  }
}
