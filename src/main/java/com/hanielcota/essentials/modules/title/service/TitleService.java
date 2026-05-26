package com.hanielcota.essentials.modules.title.service;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.title.config.TitleConfig;
import com.hanielcota.essentials.modules.title.domain.TitleLines;
import com.hanielcota.essentials.paper.PlayerProvider;
import com.hanielcota.essentials.util.ComponentUtils;
import java.time.Duration;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class TitleService {

  private final ConfigHandle<TitleConfig> config;
  private final PlayerProvider players;

  private static Component render(@NonNull String raw) {
    try {
      return ComponentUtils.mini(raw);
    } catch (RuntimeException _) {
      return Component.text(raw);
    }
  }

  private static Duration ticksToDuration(int ticks) {
    return Duration.ofMillis(Math.max(0, ticks) * 50L);
  }

  public void send(@NonNull Player target, @NonNull String message) {
    var title = build(message);

    target.showTitle(title);
  }

  public int broadcast(@NonNull String message) {
    var title = build(message);
    var onlinePlayers = this.players.all();

    for (var player : onlinePlayers) {
      player.showTitle(title);
    }

    return onlinePlayers.size();
  }

  private Title build(@NonNull String message) {
    var snap = this.config.value();
    var lines = TitleLines.parse(message);

    var fadeIn = ticksToDuration(snap.fadeInTicks());
    var stay = ticksToDuration(snap.stayTicks());
    var fadeOut = ticksToDuration(snap.fadeOutTicks());
    var times = Title.Times.times(fadeIn, stay, fadeOut);

    var titleComponent = render(lines.title());
    var subtitleComponent = render(lines.subtitle());

    return Title.title(titleComponent, subtitleComponent, times);
  }
}
