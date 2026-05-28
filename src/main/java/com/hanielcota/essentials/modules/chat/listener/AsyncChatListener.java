package com.hanielcota.essentials.modules.chat.listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Thin Bukkit adapter for {@link AsyncChatEvent}. The route → guards → render pipeline lives in
 * {@link ChatDispatchOrchestrator}; this class only wires Bukkit's listener contract.
 *
 * <p>Priority {@link EventPriority#NORMAL} with {@code ignoreCancelled = true} so cancellers at
 * lower priorities (e.g. {@code MuteChatListener} at LOWEST) win and we do not waste a parse on a
 * silenced message.
 */
@RequiredArgsConstructor
public final class AsyncChatListener implements Listener {

  private final @NonNull ChatDispatchOrchestrator orchestrator;

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void onChat(@NonNull AsyncChatEvent event) {
    this.orchestrator.dispatch(event);
  }
}
