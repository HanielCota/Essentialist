package com.hanielcota.essentials.modules.kick.service;

import com.hanielcota.essentials.util.ComponentUtils;
import lombok.NonNull;
import org.bukkit.entity.Player;

public final class KickService {

  /** Disconnects {@code target}, showing {@code screenMessage} (MiniMessage) on the kick screen. */
  public void kick(@NonNull Player target, @NonNull String screenMessage) {
    var screenComponent = ComponentUtils.mini(screenMessage);
    target.kick(screenComponent);
  }
}
