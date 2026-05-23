package com.hanielcota.essentials.modules.tpa.service;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.model.TeleportRequest;
import com.hanielcota.essentials.util.ClickableMessage;
import com.hanielcota.essentials.util.ComponentUtils;
import com.hanielcota.essentials.util.Placeholders;
import java.util.Objects;
import java.util.UUID;
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

  public TpaNotifier {
    Objects.requireNonNull(config, "config");
  }

  /** Sends the target the clickable accept / deny prompt for an incoming request. */
  public void sendPrompt(Player target, TeleportRequest request) {
    Objects.requireNonNull(target, "target");
    Objects.requireNonNull(request, "request");

    var snap = config.value();
    var messages = snap.messages();
    String requester = request.requester().name();
    long seconds = snap.requestExpiry().toSeconds();

    ClickableMessage.create()
        .append(messages.formatRequestReceived(request.type(), requester, seconds))
        .newline()
        .append(
            messages.buttonAccept(),
            slot ->
                slot.runCommand("/tpaccept " + requester)
                    .hover(Placeholders.format(messages.buttonHoverAccept(), "player", requester)))
        .append("  ")
        .append(
            messages.buttonDeny(),
            slot ->
                slot.runCommand("/tpdeny " + requester)
                    .hover(Placeholders.format(messages.buttonHoverDeny(), "player", requester)))
        .send(target);
  }

  /** Tells the requester, if still online, that their request expired. */
  public void notifyExpired(TeleportRequest request) {
    Objects.requireNonNull(request, "request");
    var requester = Bukkit.getPlayer(request.requester().id());
    if (requester != null) {
      requester.sendMessage(
          ComponentUtils.mini(
              Placeholders.format(
                  config.value().messages().expired(), "player", request.target().name())));
    }
  }

  /**
   * Tells the party other than {@code quitter}, if still online, that {@code request} died because
   * {@code quitterName} disconnected.
   */
  public void notifyPartnerLeft(TeleportRequest request, UUID quitter, String quitterName) {
    Objects.requireNonNull(request, "request");
    UUID recipient =
        request.requester().id().equals(quitter) ? request.target().id() : request.requester().id();
    var online = Bukkit.getPlayer(recipient);
    if (online != null) {
      online.sendMessage(
          ComponentUtils.mini(
              Placeholders.format(config.value().messages().partnerLeft(), "player", quitterName)));
    }
  }
}
