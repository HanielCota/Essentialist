package com.hanielcota.essentials.modules.ban.menu;

import com.github.hanielcota.menuframework.api.ClickContext;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.ban.command.BanApplyOrchestrator;
import com.hanielcota.essentials.modules.ban.config.BanConfig;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/** Routes clicks inside the ban-options menu: pick duration/reason, go back, or confirm the ban. */
@RequiredArgsConstructor
public final class BanOptionsClickHandler {

  private final ConfigHandle<BanConfig> config;
  private final BanMenuState state;
  private final BanApplyOrchestrator orchestrator;

  public void setDuration(@NonNull ClickContext click, @NonNull String raw, @NonNull String label) {
    var viewer = click.player().getUniqueId();

    this.state.setDuration(viewer, raw, label);
    click.refresh();
  }

  public void setReason(@NonNull ClickContext click, @NonNull String reason) {
    var viewer = click.player().getUniqueId();

    this.state.setReason(viewer, reason);
    click.refresh();
  }

  public void back(@NonNull ClickContext click) {
    click.switchTo(BanPickerMenu.ID);
  }

  public void confirm(@NonNull ClickContext click) {
    var player = click.player();
    var viewer = player.getUniqueId();
    var selection = this.state.get(viewer);

    if (selection == null) {
      click.close();
      return;
    }

    if (selection.reason() == null) {
      var snap = this.config.value();
      var selectReasonMsg = snap.selectReasonFirst();

      click.reply(selectReasonMsg);
      return;
    }

    var applied = this.orchestrator.apply(player, selection);
    if (!applied) {
      return;
    }

    this.state.clear(viewer);
    click.close();
  }
}
