package com.hanielcota.essentials.modules.ban.command;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.ban.config.BanConfig;
import com.hanielcota.essentials.modules.ban.domain.Ban;
import com.hanielcota.essentials.shared.ComponentUtils;
import com.hanielcota.essentials.shared.DurationFormatter;
import java.time.Duration;
import java.time.Instant;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

/**
 * Owns every user-visible artefact the ban flow emits: the broadcast lines, the staff confirmation
 * and the login-deny / kick screen component. Keeps the menus and the service free of presentation.
 */
@RequiredArgsConstructor
public final class BanNotifier {

  private final ConfigHandle<BanConfig> config;

  private static Duration remainingFrom(@NonNull Ban ban) {
    var expiresAt = ban.expiresAt();
    if (expiresAt == null) {
      return Duration.ZERO;
    }

    var now = Instant.now();

    return Duration.between(now, expiresAt);
  }

  /** The screen shown to a banned player on login (and when kicked the moment the ban lands). */
  public Component kickComponent(@NonNull Ban ban) {
    var snap = this.config.value();
    var reason = ban.reason();
    var issuer = ban.issuer();

    if (ban.isPermanent()) {
      var permanentMsg = snap.formatKickPermanent(reason, issuer);

      return ComponentUtils.mini(permanentMsg);
    }

    var remaining = remainingFrom(ban);
    var timeStr = DurationFormatter.format(remaining);
    var timedMsg = snap.formatKickTimed(reason, issuer, timeStr);

    return ComponentUtils.mini(timedMsg);
  }

  public void announceBan(@NonNull String targetName, @NonNull Ban ban) {
    var snap = this.config.value();
    var issuer = ban.issuer();
    var reason = ban.reason();

    if (ban.isPermanent()) {
      var broadcast = snap.formatBanBroadcast(targetName, issuer, reason);
      var component = ComponentUtils.mini(broadcast);

      Bukkit.broadcast(component);
      return;
    }

    var remaining = remainingFrom(ban);
    var timeStr = DurationFormatter.format(remaining);
    var broadcast = snap.formatBanBroadcastTimed(targetName, issuer, reason, timeStr);
    var component = ComponentUtils.mini(broadcast);

    Bukkit.broadcast(component);
  }

  public void announceUnban(@NonNull String targetName, @NonNull String issuer) {
    var snap = this.config.value();
    var broadcast = snap.formatUnbanBroadcast(targetName, issuer);
    var component = ComponentUtils.mini(broadcast);

    Bukkit.broadcast(component);
  }
}
