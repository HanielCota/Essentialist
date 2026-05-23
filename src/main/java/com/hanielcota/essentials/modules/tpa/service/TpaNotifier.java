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

    ClickableMessage.create()
        .append(messages.formatRequestReceived(request.type(), requester, seconds))
        .newline()
        .append(
            messages.buttonAccept(),
            slot ->
                slot.runCommand("/tpaccept " + requester)
                    .hover(messages.buttonHoverAccept().replace("{player}", requester)))
        .append("  ")
        .append(
            messages.buttonDeny(),
            slot ->
                slot.runCommand("/tpdeny " + requester)
                    .hover(messages.buttonHoverDeny().replace("{player}", requester)))
        .send(target);
  }

  public void notifyExpired(@NonNull TeleportRequest request) {
    var requester = Bukkit.getPlayer(request.requester().id());
    if (requester == null) return;

    var line =
        this.config.value().messages().expired().replace("{player}", request.target().name());
    requester.sendMessage(ComponentUtils.mini(line));
  }

  // Tells the party other than `quitter`, if online, that the request died because they
  // disconnected.
  public void notifyPartnerLeft(
      @NonNull TeleportRequest request, @NonNull UUID quitter, @NonNull String quitterName) {
    var requesterId = request.requester().id();
    var recipient = requesterId.equals(quitter) ? request.target().id() : requesterId;
    var online = Bukkit.getPlayer(recipient);
    if (online == null) return;

    var line = this.config.value().messages().partnerLeft().replace("{player}", quitterName);
    online.sendMessage(ComponentUtils.mini(line));
  }
}
