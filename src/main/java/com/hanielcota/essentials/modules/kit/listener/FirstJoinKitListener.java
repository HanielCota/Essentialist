package com.hanielcota.essentials.modules.kit.listener;

import com.hanielcota.essentials.modules.kit.domain.KitClaimResult;
import com.hanielcota.essentials.modules.kit.service.KitCatalog;
import com.hanielcota.essentials.modules.kit.service.KitClaimService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/** Gives the kits flagged {@code firstJoin} to a player the first time they join. */
@RequiredArgsConstructor
public final class FirstJoinKitListener implements Listener {

  private final KitCatalog catalog;
  private final KitClaimService claim;

  @EventHandler
  public void onJoin(@NonNull PlayerJoinEvent event) {
    var player = event.getPlayer();
    if (player.hasPlayedBefore()) {
      return;
    }

    var given = 0;
    for (var kit : this.catalog.firstJoinKits()) {
      var outcome = this.claim.claim(player, kit, false);
      if (outcome.result() == KitClaimResult.CLAIMED) {
        given++;
      }
    }

    if (given > 0) {
      this.claim.playClaimSound(player);
    }
  }
}
