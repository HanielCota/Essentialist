package com.hanielcota.essentials.modules.tpa.service;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.model.TeleportRequest;
import com.hanielcota.essentials.util.ClickableMessage;
import com.hanielcota.essentials.util.ComponentUtils;
import java.util.UUID;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Sends the teleport-request messages that fall outside a command's own reply: the clickable prompt
 * to the target, the expiry notice and the disconnect notice.
 *
 * <p>Sole responsibility: present these out-of-band TPA events to players. Direct command replies
 * stay in the command classes.
 */
public record TpaNotifier(ConfigHandle<TpaConfig> config) {

  public void sendPrompt(@NonNull Player target, @NonNull TeleportRequest request) {
    var snap = this.config.value();
    var messages = snap.messages();
    var requester = request.requester().name();
    var seconds = snap.requestExpiry().toSeconds();

    var requestType = request.type();
    var requestLine = messages.formatRequestReceived(requestType, requester, seconds);
    var acceptHover = messages.buttonHoverAccept().replace("{player}", requester);
    var denyHover = messages.buttonHoverDeny().replace("{player}", requester);

    ClickableMessage.create()
        .append(requestLine)
        .newline()
        .append(
            messages.buttonAccept(),
            slot -> slot.runCommand("/tpaccept " + requester).hover(acceptHover))
        .append("  ")
        .append(
            messages.buttonDeny(), slot -> slot.runCommand("/tpdeny " + requester).hover(denyHover))
        .send(target);
  }

  public void notifyExpired(@NonNull TeleportRequest request) {
    var requesterId = request.requester().id();
    var requester = Bukkit.getPlayer(requesterId);
    if (requester == null) {
      return;
    }

    var messages = this.config.value().messages();
    var targetName = request.target().name();
    var expiredTemplate = messages.expired();
    var expiredMsg = expiredTemplate.replace("{player}", targetName);
    var expiredComponent = ComponentUtils.mini(expiredMsg);
    requester.sendMessage(expiredComponent);
  }

  // Tells the party other than `quitter`, if online, that the request died because they
  // disconnected.
  public void notifyPartnerLeft(
      @NonNull TeleportRequest request, @NonNull UUID quitter, @NonNull String quitterName) {
    var requesterId = request.requester().id();

    var targetId = request.target().id();
    var recipient = requesterId.equals(quitter) ? targetId : requesterId;
    var online = Bukkit.getPlayer(recipient);
    if (online == null) {
      return;
    }

    var messages = this.config.value().messages();
    var partnerLeftMsg = messages.partnerLeft().replace("{player}", quitterName);
    var partnerLeftComponent = ComponentUtils.mini(partnerLeftMsg);
    online.sendMessage(partnerLeftComponent);
  }
}
