package com.hanielcota.essentials.modules.ban.menu;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.ban.config.BanConfig;
import com.hanielcota.essentials.paper.PlayerProvider;
import com.hanielcota.essentials.scheduler.Scheduler;
import com.hanielcota.essentials.shared.ComponentUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/**
 * Turns a name typed into chat (the one permitted text input) into a target for the options menu:
 * resolves the name to a known account and opens {@link BanOptionsMenu}. Hops to the player's
 * region thread first, since it is invoked from the async chat event and touches Bukkit + menu
 * state.
 */
@RequiredArgsConstructor
public final class BanNickOrchestrator {

  private static final String CANCEL_WORD = "cancel";

  private final ConfigHandle<BanConfig> config;
  private final PlayerProvider players;
  private final BanMenuState state;
  private final MenuService menus;
  private final Scheduler scheduler;

  public void handleInput(@NonNull Player issuer, @NonNull String input) {
    Runnable task = () -> resolveAndOpen(issuer, input);

    this.scheduler.runOnEntity(issuer, task);
  }

  private void resolveAndOpen(@NonNull Player issuer, @NonNull String input) {
    var snap = this.config.value();
    var trimmed = input.strip();

    if (trimmed.equalsIgnoreCase(CANCEL_WORD)) {
      reply(issuer, snap.nickCancelled());
      return;
    }

    var found = this.players.offlineByName(trimmed);
    if (found.isEmpty()) {
      reply(issuer, snap.formatNickUnknown(trimmed));
      return;
    }

    var offline = found.get();
    var name = offline.getName();
    if (name == null) {
      reply(issuer, snap.formatNickUnknown(trimmed));
      return;
    }

    var viewer = issuer.getUniqueId();
    var targetId = offline.getUniqueId();
    var permanentLabel = snap.permanentLabel();

    this.state.begin(viewer, targetId, name, permanentLabel);
    this.menus.open(issuer, BanOptionsMenu.ID);
  }

  private static void reply(@NonNull Player issuer, @NonNull String message) {
    var component = ComponentUtils.mini(message);

    issuer.sendMessage(component);
  }
}
