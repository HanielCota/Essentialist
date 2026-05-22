package com.hanielcota.essentials.modules.title.service;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.title.config.TitleConfig;
import com.hanielcota.essentials.util.ComponentUtils;
import java.time.Duration;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class TitleService {

  private final ConfigHandle<TitleConfig> config;

  public TitleService(ConfigHandle<TitleConfig> config) {
    this.config = Objects.requireNonNull(config, "config");
  }

  /** Renders MiniMessage, falling back to literal text when the player typed an invalid tag. */
  private static Component render(String raw) {
    try {
      return ComponentUtils.mini(raw);
    } catch (RuntimeException _) {
      return Component.text(raw);
    }
  }

  private static Duration ticksToDuration(int ticks) {
    return Duration.ofMillis(Math.max(0, ticks) * 50L);
  }

  public void send(Player target, String message) {
    Objects.requireNonNull(target, "target");
    Objects.requireNonNull(message, "message");

    target.showTitle(build(message));
  }

  public int broadcast(String message) {
    Objects.requireNonNull(message, "message");

    var title = build(message);
    var onlinePlayers = Bukkit.getOnlinePlayers();

    for (var player : onlinePlayers) {
      player.showTitle(title);
    }

    return onlinePlayers.size();
  }

  private Title build(String message) {
    var snap = config.value();
    var lines = TitleLines.parse(message);

    var times =
        Title.Times.times(
            ticksToDuration(snap.fadeInTicks()),
            ticksToDuration(snap.stayTicks()),
            ticksToDuration(snap.fadeOutTicks()));

    return Title.title(render(lines.title()), render(lines.subtitle()), times);
  }

  /** A title message split into its title and subtitle halves at the first {@code |}. */
  private record TitleLines(String title, String subtitle) {

    static TitleLines parse(String message) {
      int separator = message.indexOf('|');
      if (separator < 0) {
        return new TitleLines(message.strip(), "");
      }
      return new TitleLines(
          message.substring(0, separator).strip(), message.substring(separator + 1).strip());
    }
  }
}
