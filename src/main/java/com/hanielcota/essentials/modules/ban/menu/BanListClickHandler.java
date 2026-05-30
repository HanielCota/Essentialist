package com.hanielcota.essentials.modules.ban.menu;

import com.github.hanielcota.menuframework.api.ClickContext;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.ban.command.BanNotifier;
import com.hanielcota.essentials.modules.ban.config.BanConfig;
import com.hanielcota.essentials.modules.ban.domain.ActiveBan;
import com.hanielcota.essentials.modules.ban.service.BanService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/** Lifts the clicked ban and refreshes the list. */
@RequiredArgsConstructor
public final class BanListClickHandler {

  private final ConfigHandle<BanConfig> config;
  private final BanService service;
  private final BanNotifier notifier;

  public void unban(@NonNull ClickContext click, @NonNull ActiveBan entry) {
    var issuer = click.player();
    var targetId = entry.id();
    var targetName = entry.name();

    var removed = this.service.unban(targetId);
    if (!removed) {
      var snap = this.config.value();
      var notBannedMsg = snap.formatNotBanned(targetName);

      click.reply(notBannedMsg);
      click.refresh();
      return;
    }

    var issuerName = issuer.getName();
    this.notifier.announceUnban(targetName, issuerName);

    var snap = this.config.value();
    var unbannedMsg = snap.formatStaffUnbanned(targetName);

    click.reply(unbannedMsg);
    click.refresh();
  }
}
