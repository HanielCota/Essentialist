package com.hanielcota.essentials.modules.tpa.command;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.MenuOpenings;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.history.TpaHistory;
import com.hanielcota.essentials.modules.tpa.menu.TpaHistoryMenu;
import com.hanielcota.essentials.modules.tpa.menu.TpaHistoryMenuState;
import com.hanielcota.essentials.util.ComponentUtils;
import io.github.hanielcota.commandframework.core.CommandActor;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/**
 * Opens {@code /tpahistory} for a given subject — paints the empty / viewing-other line, prefetches
 * the entries into the menu state and then opens the menu via {@link MenuOpenings}. Keeps the
 * command class free of the second flow needed by the "other player" case.
 */
@RequiredArgsConstructor
public final class TpaHistoryPresenter {

  private final ConfigHandle<TpaConfig> config;
  private final TpaHistory history;
  private final MenuService menus;
  private final TpaHistoryMenuState state;

  public void open(
      @NonNull CommandActor actor,
      @NonNull Player viewer,
      @NonNull UUID subject,
      boolean self,
      @NonNull String subjectName) {
    var snap = this.config.value();
    var messages = snap.messages();

    var entries = this.history.list(subject);
    if (entries.isEmpty()) {
      var selfTemplate = messages.noHistory();
      var otherTemplate = messages.noHistoryOther();
      var emptyMsg = self ? selfTemplate : otherTemplate.replace("{player}", subjectName);

      actor.sendError(emptyMsg);
      return;
    }

    if (!self) {
      var viewingTemplate = messages.viewingOther();
      var viewingMsg = viewingTemplate.replace("{player}", subjectName);
      var viewingComponent = ComponentUtils.mini(viewingMsg);
      actor.sendMessage(viewingComponent);
    }

    var viewerId = viewer.getUniqueId();
    this.state.prefetch(viewerId, entries);

    MenuOpenings.open(this.menus, viewer, TpaHistoryMenu.ID, actor);
  }
}
