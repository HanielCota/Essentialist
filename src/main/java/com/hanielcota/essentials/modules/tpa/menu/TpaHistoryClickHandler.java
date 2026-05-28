package com.hanielcota.essentials.modules.tpa.menu;

import com.github.hanielcota.menuframework.api.ClickContext;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.domain.Destination;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequestStatus;
import com.hanielcota.essentials.modules.tpa.history.TpaHistoryEntry;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class TpaHistoryClickHandler {

  private final @NonNull ConfigHandle<TpaConfig> config;
  private final @NonNull TpaHistoryMenuState state;

  void onEntryClicked(@NonNull ClickContext click, @NonNull TpaHistoryEntry entry) {
    if (entry.status() != TeleportRequestStatus.ACCEPTED) {
      return;
    }
    var destination = entry.destination();
    if (destination == null) {
      return;
    }

    var settings = this.config.value().menu();
    var copyMsg = formatDestinationCopy(settings.destinationCopyMessage(), destination);
    click.reply(copyMsg);
  }

  void onFilterClicked(@NonNull ClickContext click) {
    var viewerId = click.player().getUniqueId();
    this.state.cycleFilter(viewerId);
    click.refresh();
  }

  private static String formatDestinationCopy(
      @NonNull String template, @NonNull Destination destination) {
    return template
        .replace("{world}", destination.world())
        .replace("{x}", Long.toString(Math.round(destination.x())))
        .replace("{y}", Long.toString(Math.round(destination.y())))
        .replace("{z}", Long.toString(Math.round(destination.z())));
  }
}
