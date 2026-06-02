package com.hanielcota.essentials.modules.back.menu;

import com.github.hanielcota.menuframework.api.ClickContext;
import com.hanielcota.essentials.modules.back.command.BackOrchestrator;
import com.hanielcota.essentials.modules.back.service.BackFilterState;
import com.hanielcota.essentials.modules.back.service.BackStaffViewState;
import com.hanielcota.essentials.modules.teleport.history.TeleportHistory.HistoryEntry;
import lombok.NonNull;

public record BackClickHandler(
    BackOrchestrator orchestrator, BackFilterState filterState, BackStaffViewState staffViewState) {

  public void onFilterClicked(@NonNull ClickContext click) {
    var player = click.player();
    var playerId = player.getUniqueId();

    this.filterState.cycleFilter(playerId);
    click.refresh();
  }

  public void handle(@NonNull ClickContext click, @NonNull HistoryEntry entry) {
    var player = click.player();
    var playerId = player.getUniqueId();

    var historyOwner = this.staffViewState.targetOf(playerId);
    if (historyOwner == null) {
      historyOwner = playerId;
    }

    click.close();
    this.orchestrator.processTeleport(click, entry, historyOwner);
  }
}
