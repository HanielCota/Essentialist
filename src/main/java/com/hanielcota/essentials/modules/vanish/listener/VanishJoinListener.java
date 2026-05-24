package com.hanielcota.essentials.modules.vanish.listener;

import com.hanielcota.essentials.modules.vanish.service.VanishService;
import com.hanielcota.essentials.modules.vanish.service.VanishVisibilityApplier;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * On join, hides every currently-vanished player from the joiner. Vanish state is memory-only so
 * the joiner themselves is never vanished at this point — restart and quit both clear the entry.
 */
@RequiredArgsConstructor
public final class VanishJoinListener implements Listener {

  private final VanishService service;
  private final VanishVisibilityApplier applier;

  @EventHandler
  public void onJoin(@NonNull PlayerJoinEvent event) {
    this.applier.hideExistingFor(event.getPlayer(), this.service.vanished());
  }
}
