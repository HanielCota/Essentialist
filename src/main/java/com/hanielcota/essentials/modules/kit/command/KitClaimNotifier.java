package com.hanielcota.essentials.modules.kit.command;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.kit.config.KitConfig;
import com.hanielcota.essentials.modules.kit.domain.Kit;
import com.hanielcota.essentials.modules.kit.domain.KitClaimResult;
import com.hanielcota.essentials.modules.kit.service.ClaimOutcome;
import com.hanielcota.essentials.modules.kit.service.KitCooldownService;
import com.hanielcota.essentials.modules.kit.service.KitDurations;
import com.hanielcota.essentials.shared.ComponentUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/** Sends the player the chat line matching a claim outcome. */
@RequiredArgsConstructor
public final class KitClaimNotifier {

  private final ConfigHandle<KitConfig> config;
  private final KitCooldownService cooldowns;

  public void notify(@NonNull Player player, @NonNull Kit kit, @NonNull ClaimOutcome outcome) {
    var messages = this.config.value().messages();
    var name = kit.displayName();

    var text =
        switch (outcome.result()) {
          case CLAIMED -> messages.formatClaimed(name);
          case ON_COOLDOWN -> messages.formatOnCooldown(name, remainingTime(player, kit));
          case ALREADY_CLAIMED -> messages.formatAlreadyClaimed(name);
          case NO_PERMISSION -> messages.formatNoPermission(name);
          case EMPTY -> messages.formatEmpty(name);
          case INVENTORY_FULL -> messages.inventoryNoSpace();
          case UNKNOWN_KIT -> messages.formatUnknownKit(name);
        };

    send(player, text);

    if (outcome.result() == KitClaimResult.CLAIMED && outcome.overflowDropped()) {
      send(player, messages.inventoryFull());
    }
  }

  private String remainingTime(@NonNull Player player, @NonNull Kit kit) {
    var seconds = this.cooldowns.remainingSeconds(player.getUniqueId(), kit);

    return KitDurations.format(seconds);
  }

  private static void send(@NonNull Player player, @NonNull String mini) {
    var component = ComponentUtils.mini(mini);

    player.sendMessage(component);
  }
}
