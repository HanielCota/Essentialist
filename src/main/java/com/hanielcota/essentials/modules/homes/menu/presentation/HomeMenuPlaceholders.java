package com.hanielcota.essentials.modules.homes.menu.presentation;

import com.hanielcota.essentials.modules.homes.config.menu.HomesMenuConfig;
import com.hanielcota.essentials.shared.Numbers;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.NonNull;

public record HomeMenuPlaceholders(
    String world,
    String x,
    String y,
    String z,
    String direction,
    String createdDate,
    String createdTime,
    String createdAt) {

  private static final String[] DIRECTIONS = {
    "Sul", "Sudoeste", "Oeste", "Noroeste", "Norte", "Nordeste", "Leste", "Sudeste"
  };

  public static HomeMenuPlaceholders of(
      @NonNull String world,
      double x,
      double y,
      double z,
      float yaw,
      long createdAt,
      @NonNull HomesMenuConfig settings) {
    var instant = Instant.ofEpochMilli(createdAt);
    var zone = ZoneId.systemDefault();
    var moment = LocalDateTime.ofInstant(instant, zone);

    var dateFormatter = settings.createdDateFormatter();
    var timeFormatter = settings.createdTimeFormatter();
    var date = dateFormatter.format(moment);
    var time = timeFormatter.format(moment);
    var timestamp = date + " " + time;
    var displayWorld = displayWorld(world, settings);

    return new HomeMenuPlaceholders(
        displayWorld,
        Numbers.display(x),
        Numbers.display(y),
        Numbers.display(z),
        directionOf(yaw),
        date,
        time,
        timestamp);
  }

  private static String directionOf(float yaw) {
    var normalized = ((yaw % 360) + 360) % 360;
    var shifted = (normalized + 22.5) % 360;
    var index = (int) (shifted / 45);

    return DIRECTIONS[index];
  }

  private static String displayWorld(@NonNull String world, @NonNull HomesMenuConfig settings) {
    var configured = settings.worldNames();
    var direct = configured.get(world);
    if (direct != null) {
      return direct;
    }

    for (var entry : configured.entrySet()) {
      if (entry.getKey().equalsIgnoreCase(world)) {
        return entry.getValue();
      }
    }

    return world;
  }
}
