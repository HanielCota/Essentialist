package com.hanielcota.essentials.modules.tpa.command;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequest;
import com.hanielcota.essentials.paper.PlayerProvider;
import com.hanielcota.essentials.util.ClickableMessage;
import com.hanielcota.essentials.util.ComponentUtils;
import java.util.UUID;
import lombok.NonNull;
import org.bukkit.entity.Player;

/**
 * Sends the teleport-request messages that fall outside a command's own reply: the clickable prompt
 * to the target, the expiry notice and the disconnect notice.
 *
 * <p>Sole responsibility: present these out-of-band TPA events to players. Direct command replies
 * stay in the command classes.
 */
public record TpaNotifier(ConfigHandle<TpaConfig> config, PlayerProvider players) {

  public void sendPrompt(@NonNull Player target, @NonNull TeleportRequest request) {
    var snap = this.config.value();
    var messages = snap.messages();

    var requesterName = request.requester().name();
    var seconds = snap.requestExpiry().toSeconds();

    var requestType = request.type();
    var requestLine = messages.formatRequestReceived(requestType, requesterName, seconds);

    var acceptHoverTemplate = messages.buttonHoverAccept();
    var acceptHover = acceptHoverTemplate.replace("{player}", requesterName);

    var denyHoverTemplate = messages.buttonHoverDeny();
    var denyHover = denyHoverTemplate.replace("{player}", requesterName);

    var acceptCommand = "/tpaccept " + requesterName;
    var denyCommand = "/tpdeny " + requesterName;

    var acceptLabel = messages.buttonAccept();
    var denyLabel = messages.buttonDeny();

    var builder = ClickableMessage.create();
    builder.append(requestLine);
    builder.newline();
    builder.append(acceptLabel, slot -> slot.runCommand(acceptCommand).hover(acceptHover));
    builder.append("  ");
    builder.append(denyLabel, slot -> slot.runCommand(denyCommand).hover(denyHover));

    builder.send(target);
  }

  public void notifyExpired(@NonNull TeleportRequest request) {
    var requesterId = request.requester().id();
    var requester = this.players.online(requesterId).orElse(null);
    if (requester == null) {
      return;
    }

    var snap = this.config.value();
    var messages = snap.messages();

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

    var online = this.players.online(recipient).orElse(null);
    if (online == null) {
      return;
    }

    var snap = this.config.value();
    var messages = snap.messages();

    var partnerLeftTemplate = messages.partnerLeft();
    var partnerLeftMsg = partnerLeftTemplate.replace("{player}", quitterName);
    var partnerLeftComponent = ComponentUtils.mini(partnerLeftMsg);

    online.sendMessage(partnerLeftComponent);
  }
}
