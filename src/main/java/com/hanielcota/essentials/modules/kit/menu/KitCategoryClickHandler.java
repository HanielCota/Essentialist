package com.hanielcota.essentials.modules.kit.menu;

import com.github.hanielcota.menuframework.api.ClickContext;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.kit.config.KitConfig;
import com.hanielcota.essentials.modules.kit.domain.KitClaimResult;
import com.hanielcota.essentials.modules.kit.service.KitCatalog;
import com.hanielcota.essentials.modules.kit.service.KitClaimService;
import com.hanielcota.essentials.shared.ComponentUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/** Opens the kit list for a clicked category, or claims every currently-available kit. */
@RequiredArgsConstructor
public final class KitCategoryClickHandler {

  private final ConfigHandle<KitConfig> config;
  private final KitMenuState state;
  private final KitCatalog catalog;
  private final KitClaimService claimService;

  public void open(@NonNull ClickContext click, @NonNull String categoryId) {
    var uuid = click.player().getUniqueId();

    this.state.setCategory(uuid, categoryId);
    click.switchTo(KitListMenu.ID);
  }

  public void claimAll(@NonNull ClickContext click) {
    var player = click.player();

    var claimed = 0;
    for (var kit : this.catalog.all()) {
      var outcome = this.claimService.claim(player, kit, false);
      if (outcome.result() == KitClaimResult.CLAIMED) {
        claimed++;
      }
    }

    if (claimed > 0) {
      this.claimService.playClaimSound(player);
    }

    var messages = this.config.value().messages();
    var text = claimed == 0 ? messages.claimedNone() : messages.formatClaimedAll(claimed);
    var component = ComponentUtils.mini(text);

    player.sendMessage(component);
    click.refresh();
  }
}
