package com.hanielcota.essentials.modules.title.service;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.title.config.TitleConfig;
import com.hanielcota.essentials.util.ComponentUtils;
import java.time.Duration;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class TitleService {

  private final ConfigHandle<TitleConfig> config;

  private static Component render(@NonNull String raw) {
    try {
      var component = ComponentUtils.mini(raw);
      return component;
    } catch (RuntimeException _) {
      return Component.text(raw);
    }
  }

  private static Duration ticksToDuration(int ticks) {
    return Duration.ofMillis(Math.max(0, ticks) * 50L);
  }

  public void send(@NonNull Player target, @NonNull String message) {

    target.showTitle(build(message));
  }

  public int broadcast(@NonNull String message) {

    var title = build(message);
    var onlinePlayers = Bukkit.getOnlinePlayers();

    for (var player : onlinePlayers) {
      player.showTitle(title);
    }

    return onlinePlayers.size();
  }

  private Title build(@NonNull String message) {
    var snap = this.config.value();
    var lines = TitleLines.parse(message);

    var times =
        Title.Times.times(
            ticksToDuration(snap.fadeInTicks()),
            ticksToDuration(snap.stayTicks()),
            ticksToDuration(snap.fadeOutTicks()));

    return Title.title(render(lines.title()), render(lines.subtitle()), times);
  }
}
