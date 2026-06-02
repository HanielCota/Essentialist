package com.hanielcota.essentials.modules.ban.menu;

import com.github.hanielcota.menuframework.api.ClickContext;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.ban.config.BanConfig;
import com.hanielcota.essentials.modules.ban.service.BanNickSessions;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/** Routes picker clicks: select an online target, or arm the chat-based nick search. */
@RequiredArgsConstructor
public final class BanPickerClickHandler {

  private final ConfigHandle<BanConfig> config;
  private final BanMenuState state;
  private final BanNickSessions sessions;

  public void selectOnline(
      @NonNull ClickContext click, @NonNull UUID targetId, @NonNull String targetName) {
    var viewer = click.player().getUniqueId();
    var snap = this.config.value();
    var permanentLabel = snap.permanentLabel();

    this.state.begin(viewer, targetId, targetName, permanentLabel);

    click.switchTo(BanOptionsMenu.ID);
  }

  public void promptNick(@NonNull ClickContext click) {
    var player = click.player();
    var viewer = player.getUniqueId();
    var snap = this.config.value();
    var prompt = snap.nickPrompt();

    this.sessions.start(viewer);

    click.close();
    click.reply(prompt);
  }
}
