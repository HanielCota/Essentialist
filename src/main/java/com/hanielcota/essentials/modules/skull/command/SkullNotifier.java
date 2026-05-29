package com.hanielcota.essentials.modules.skull.command;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.skull.config.SkullConfig;
import io.github.hanielcota.commandframework.core.CommandActor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.OfflinePlayer;

@RequiredArgsConstructor
public final class SkullNotifier {

  private final ConfigHandle<SkullConfig> config;

  public void sendReceived(@NonNull CommandActor actor, @NonNull OfflinePlayer owner, boolean self) {
    var snap = config.value();
    var messages = snap.whenReceived();
    var ownerName = owner.getName();
    var receivedMsg = messages.forSender(self, ownerName);

    actor.sendSuccess(receivedMsg);
  }

  public void sendPlayerNotFound(@NonNull CommandActor actor) {
    var snap = config.value();
    var notFoundMsg = snap.playerNotFound();

    actor.sendError(notFoundMsg);
  }

  public void sendInventoryFull(@NonNull CommandActor actor) {
    var snap = config.value();
    var fullMsg = snap.inventoryFull();

    actor.sendError(fullMsg);
  }
}
