package com.hanielcota.essentials.modules.kick.service;

import com.hanielcota.essentials.util.ComponentUtils;
import org.bukkit.entity.Player;

public final class KickService {

  /** Disconnects {@code target}, showing {@code screenMessage} (MiniMessage) on the kick screen. */
  public void kick(Player target, String screenMessage) {

    target.kick(ComponentUtils.mini(screenMessage));
  }
}
