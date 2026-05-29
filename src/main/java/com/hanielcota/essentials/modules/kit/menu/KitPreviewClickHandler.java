package com.hanielcota.essentials.modules.kit.menu;

import com.github.hanielcota.menuframework.api.ClickContext;
import com.hanielcota.essentials.modules.kit.command.KitClaimNotifier;
import com.hanielcota.essentials.modules.kit.domain.KitClaimResult;
import com.hanielcota.essentials.modules.kit.service.KitCatalog;
import com.hanielcota.essentials.modules.kit.service.KitClaimService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/** Routes the preview: claim the kit, or go back to the list. */
@RequiredArgsConstructor
public final class KitPreviewClickHandler {

  private final KitMenuState state;
  private final KitCatalog catalog;
  private final KitClaimService claimService;
  private final KitClaimNotifier notifier;

  public void claim(@NonNull ClickContext click) {
    var player = click.player();
    var uuid = player.getUniqueId();
    var kitId = this.state.kit(uuid);

    if (kitId == null) {
      click.close();
      return;
    }

    var kit = this.catalog.find(kitId);
    if (kit.isEmpty()) {
      click.close();
      return;
    }

    var outcome = this.claimService.claim(player, kit.get());
    this.notifier.notify(player, kit.get(), outcome);

    if (outcome.result() == KitClaimResult.CLAIMED) {
      click.close();
      return;
    }

    click.refresh();
  }

  public void back(@NonNull ClickContext click) {
    click.switchTo(KitListMenu.ID);
  }
}
