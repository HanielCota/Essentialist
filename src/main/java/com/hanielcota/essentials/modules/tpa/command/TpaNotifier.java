package com.hanielcota.essentials.modules.tpa.command;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequest;
import com.hanielcota.essentials.modules.tpa.service.TpaProfileService;
import com.hanielcota.essentials.modules.tpa.service.favorites.TpaFavoriteService;
import com.hanielcota.essentials.paper.PlayerProvider;
import com.hanielcota.essentials.shared.ClickableMessage;
import com.hanielcota.essentials.shared.ComponentUtils;
import java.util.UUID;
import lombok.NonNull;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

/**
 * Sends the teleport-request messages that fall outside a command's own reply: the clickable prompt
 * to the target, the expiry notice and the disconnect notice. Plays a notification sound on the
 * prompt when the target has it enabled in their profile.
 *
 * <p>Sole responsibility: present these out-of-band TPA events to players. Direct command replies
 * stay in the command classes.
 */
public record TpaNotifier(
    ConfigHandle<TpaConfig> config,
    PlayerProvider players,
    TpaProfileService profiles,
    TpaFavoriteService favorites) {

  private static final String PROMPT_SOUND_KEY = "entity.experience_orb.pickup";
  private static final float PROMPT_SOUND_VOLUME = 1.0f;
  private static final float PROMPT_SOUND_PITCH = 1.2f;

  public void sendPrompt(@NonNull Player target, @NonNull TeleportRequest request) {
    var snap = this.config.value();
    var messages = snap.messages();

    var requesterName = request.requester().name();
    var seconds = snap.requestExpiry().toSeconds();

    var requestType = request.type();
    var targetId = target.getUniqueId();
    var requesterId = request.requester().id();
    var requesterIsFavorite = this.favorites.isFavorite(targetId, requesterId);
    var requestLine =
        messages.formatRequestReceived(requestType, requesterName, seconds, requesterIsFavorite);

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
    playPromptSound(target);
  }

  private void playPromptSound(@NonNull Player target) {
    var profile = this.profiles.profile(target.getUniqueId());
    if (!profile.soundsEnabled()) {
      return;
    }

    target.playSound(
        target, PROMPT_SOUND_KEY, SoundCategory.MASTER, PROMPT_SOUND_VOLUME, PROMPT_SOUND_PITCH);
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

  /**
   * Tells the previous target, if online, that the requester switched to a different target —
   * distinct from {@link #notifyPartnerLeft} which fires on disconnects.
   */
  public void notifyRequesterSwitched(
      @NonNull TeleportRequest previous, @NonNull String requesterName) {
    var targetId = previous.target().id();
    var online = this.players.online(targetId).orElse(null);
    if (online == null) {
      return;
    }

    var snap = this.config.value();
    var messages = snap.messages();

    var template = messages.requesterSwitchedTarget();
    var msg = template.replace("{player}", requesterName);
    var component = ComponentUtils.mini(msg);

    online.sendMessage(component);
  }

  /**
   * Tells the target, if online, that the requester cancelled the outgoing request from the hub
   * menu — so the clickable prompt they may still see in chat is now stale.
   */
  public void notifyCancelledByRequester(@NonNull TeleportRequest request) {
    var targetId = request.target().id();
    var online = this.players.online(targetId).orElse(null);
    if (online == null) {
      return;
    }

    var snap = this.config.value();
    var messages = snap.messages();

    var requesterName = request.requester().name();
    var template = messages.cancelledByRequester();
    var msg = template.replace("{player}", requesterName);
    var component = ComponentUtils.mini(msg);

    online.sendMessage(component);
  }

  /**
   * Tells the target that an incoming request from a favorite was auto-accepted on their behalf —
   * sent in place of the manual {@code acceptedSelf} line.
   */
  public void notifyAutoAccepted(@NonNull Player target, @NonNull String requesterName) {
    var snap = this.config.value();
    var messages = snap.messages();

    var template = messages.autoAcceptedNotice();
    var msg = template.replace("{player}", requesterName);
    var component = ComponentUtils.mini(msg);

    target.sendMessage(component);
  }
}
