package com.hanielcota.essentials.module.lifecycle;

import com.hanielcota.essentials.EssentialsPlugin;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public final class ModuleListeners {

  private final List<Listener> listeners = new ArrayList<>();

  public void register(@NonNull EssentialsPlugin plugin, @NonNull Listener listener) {
    var server = plugin.getServer();
    var pluginManager = server.getPluginManager();

    pluginManager.registerEvents(listener, plugin);
    this.listeners.add(listener);
  }

  public void unregisterAll() {
    for (var listener : this.listeners) {
      HandlerList.unregisterAll(listener);
    }
    this.listeners.clear();
  }
}
