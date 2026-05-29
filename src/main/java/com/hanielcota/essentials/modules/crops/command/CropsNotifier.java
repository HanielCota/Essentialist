package com.hanielcota.essentials.modules.crops.command;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.crops.config.CropsConfig;
import com.hanielcota.essentials.shared.ComponentUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class CropsNotifier {

  private final ConfigHandle<CropsConfig> config;

  private final Map<UUID, Long> lastSent = new HashMap<>();

  public void notifyBreakBlocked(@NonNull Player player) {
    var snap = config.value();
    var message = snap.messages().breakBlocked();
    send(player, message);
  }

  public void notifyTrampleBlocked(@NonNull Player player) {
    var snap = config.value();
    var message = snap.messages().trampleBlocked();
    send(player, message);
  }

  private void send(@NonNull Player player, @NonNull String message) {
    var snap = config.value();
    var messages = snap.messages();

    if (!messages.notifyPlayer() || message.isBlank()) {
      return;
    }

    var playerId = player.getUniqueId();
    if (onCooldown(playerId, messages.cooldownMs())) {
      return;
    }

    var component = ComponentUtils.mini(message);
    player.sendMessage(component);
  }

  public void forget(@NonNull UUID playerId) {
    this.lastSent.remove(playerId);
  }

  private boolean onCooldown(@NonNull UUID playerId, long cooldownMs) {
    var now = System.currentTimeMillis();
    var last = lastSent.getOrDefault(playerId, 0L);

    if (now - last < cooldownMs) {
      return true;
    }

    lastSent.put(playerId, now);
    return false;
  }
}
