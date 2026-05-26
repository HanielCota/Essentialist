package com.hanielcota.essentials.modules.nick.service;

import com.hanielcota.essentials.shared.ComponentUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

/**
 * Applies a nickname to the player's display + tab name, or restores the real name on reset. Kept
 * separate from {@link NickService} because the service is responsible for state + persistence, and
 * the applier touches Bukkit / Adventure live entity state.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NickApplier {

  public static void apply(@NonNull Player player, @NonNull String nickname) {
    var component = ComponentUtils.mini(nickname);

    player.displayName(component);
    player.playerListName(component);
  }

  public static void reset(@NonNull Player player) {
    var realName = player.getName();
    var component = Component.text(realName);

    player.displayName(component);
    player.playerListName(component);
  }
}
