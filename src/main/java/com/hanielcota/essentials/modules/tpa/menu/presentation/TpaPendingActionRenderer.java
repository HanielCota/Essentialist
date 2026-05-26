package com.hanielcota.essentials.modules.tpa.menu.presentation;

import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.hanielcota.essentials.modules.tpa.config.menu.TpaPendingActionMenuConfig;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequest;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequestType;
import com.hanielcota.essentials.util.Placeholders;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
import org.bukkit.Material;

public final class TpaPendingActionRenderer {

  public ItemTemplate targetTemplate(
      @NonNull TpaPendingActionMenuConfig settings, @NonNull TeleportRequest request) {
    var requesterName = request.requester().name();
    var typeLabel = typeLabel(settings, request.type());
    var seconds = Long.toString(secondsLeft(request));
    var name = targetName(settings, requesterName, typeLabel, seconds);
    var lore = applyTargetPlaceholders(settings.targetLore(), requesterName, typeLabel, seconds);

    var builder = ItemTemplate.builder(settings.targetIcon());
    applyTargetHead(builder, settings, request);
    builder.name(name);
    builder.lore(lore.toArray(String[]::new));
    builder.italic(false);

    return builder.build();
  }

  public ItemTemplate acceptTemplate(
      @NonNull TpaPendingActionMenuConfig settings, @NonNull TeleportRequest request) {
    var requesterName = request.requester().name();
    var name = settings.acceptName().replace("{player}", requesterName);
    var lore = replacePlayer(settings.acceptLore(), requesterName);

    return simpleTemplate(settings.acceptIcon(), name, lore);
  }

  public ItemTemplate denyTemplate(
      @NonNull TpaPendingActionMenuConfig settings, @NonNull TeleportRequest request) {
    var requesterName = request.requester().name();
    var name = settings.denyName().replace("{player}", requesterName);
    var lore = replacePlayer(settings.denyLore(), requesterName);

    return simpleTemplate(settings.denyIcon(), name, lore);
  }

  public ItemTemplate blockTemplate(
      @NonNull TpaPendingActionMenuConfig settings, @NonNull TeleportRequest request) {
    var requesterName = request.requester().name();
    var name = settings.blockName().replace("{player}", requesterName);
    var lore = replacePlayer(settings.blockLore(), requesterName);

    return simpleTemplate(settings.blockIcon(), name, lore);
  }

  public ItemTemplate backTemplate(@NonNull TpaPendingActionMenuConfig settings) {
    return simpleTemplate(settings.backIcon(), settings.backName(), settings.backLore());
  }

  private static String targetName(
      @NonNull TpaPendingActionMenuConfig settings,
      @NonNull String requesterName,
      @NonNull String typeLabel,
      @NonNull String seconds) {
    return Placeholders.format(
        settings.targetName(), "player", requesterName, "type", typeLabel, "seconds", seconds);
  }

  private static void applyTargetHead(
      @NonNull ItemTemplate.Builder builder,
      @NonNull TpaPendingActionMenuConfig settings,
      @NonNull TeleportRequest request) {
    if (settings.targetIcon() != Material.PLAYER_HEAD) {
      return;
    }
    if (settings.targetUsePlayerHead()) {
      builder.head(request.requester().id());
      return;
    }
    if (!settings.targetHeadTexture().isBlank()) {
      builder.head(settings.targetHeadTexture());
    }
  }

  private static String typeLabel(
      @NonNull TpaPendingActionMenuConfig settings, @NonNull TeleportRequestType type) {
    return switch (type) {
      case TPA -> settings.typeTpa();
      case TPAHERE -> settings.typeTpaHere();
    };
  }

  private static long secondsLeft(@NonNull TeleportRequest request) {
    var now = Instant.now();
    var remaining = Duration.between(now, request.window().expiresAt()).toSeconds();

    return Math.max(0, remaining);
  }

  private static List<String> applyTargetPlaceholders(
      @NonNull List<String> lines,
      @NonNull String player,
      @NonNull String type,
      @NonNull String seconds) {
    var values = Map.of("player", player, "type", type, "seconds", seconds);
    var replaced = new ArrayList<String>(lines.size());
    for (var line : lines) {
      replaced.add(Placeholders.format(line, values));
    }
    return replaced;
  }

  private static List<String> replacePlayer(@NonNull List<String> lines, @NonNull String player) {
    var replaced = new ArrayList<String>(lines.size());
    for (var line : lines) {
      replaced.add(line.replace("{player}", player));
    }
    return replaced;
  }

  private static ItemTemplate simpleTemplate(
      @NonNull Material material, @NonNull String name, @NonNull List<String> lore) {
    var builder = ItemTemplate.builder(material);
    builder.name(name);
    builder.lore(lore.toArray(String[]::new));
    builder.italic(false);

    return builder.build();
  }
}
