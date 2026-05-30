package com.hanielcota.essentials.modules.ban.command;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.ban.config.BanConfig;
import com.hanielcota.essentials.modules.ban.domain.Ban;
import com.hanielcota.essentials.modules.ban.menu.BanSelection;
import com.hanielcota.essentials.modules.ban.service.BanDurationParser;
import com.hanielcota.essentials.modules.ban.service.BanService;
import com.hanielcota.essentials.scheduler.Scheduler;
import com.hanielcota.essentials.shared.ComponentUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Sequences a confirmed ban: validation (self / exempt / already banned), persistence via {@link
 * BanService}, kicking the target if online, and the broadcast plus staff confirmation. Runs on the
 * main thread (invoked from a menu click) and returns whether the ban was applied so the menu can
 * decide to close.
 */
@RequiredArgsConstructor
public final class BanApplyOrchestrator {

  private static final String EXEMPT_PERMISSION = "essentials.ban.exempt";

  private final ConfigHandle<BanConfig> config;
  private final BanService service;
  private final BanNotifier notifier;
  private final Scheduler scheduler;

  public boolean apply(@NonNull Player issuer, @NonNull BanSelection selection) {
    var snap = this.config.value();
    var targetId = selection.targetId();
    var targetName = selection.targetName();

    if (issuer.getUniqueId().equals(targetId)) {
      reply(issuer, snap.cannotBanSelf());
      return false;
    }

    var online = Bukkit.getPlayer(targetId);
    if (online != null && online.hasPermission(EXEMPT_PERMISSION)) {
      reply(issuer, snap.formatExempt(targetName));
      return false;
    }

    if (this.service.isBanned(targetId)) {
      reply(issuer, snap.formatAlreadyBanned(targetName));
      return false;
    }

    var reason = selection.reason();
    if (reason == null) {
      reply(issuer, snap.selectReasonFirst());
      return false;
    }

    var duration = BanDurationParser.tryParse(selection.durationRaw());
    var issuerName = issuer.getName();

    var ban = this.service.ban(targetId, targetName, duration, reason, issuerName);

    kickIfOnline(online, ban);
    this.notifier.announceBan(targetName, ban);
    reply(issuer, snap.formatStaffBanned(targetName));

    return true;
  }

  private void kickIfOnline(Player online, @NonNull Ban ban) {
    if (online == null) {
      return;
    }

    var screen = this.notifier.kickComponent(ban);
    Runnable kick = () -> online.kick(screen);

    this.scheduler.runOnEntity(online, kick);
  }

  private static void reply(@NonNull Player issuer, @NonNull String message) {
    var component = ComponentUtils.mini(message);

    issuer.sendMessage(component);
  }
}
