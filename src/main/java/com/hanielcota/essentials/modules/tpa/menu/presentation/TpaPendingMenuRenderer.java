package com.hanielcota.essentials.modules.tpa.menu.presentation;

import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.hanielcota.essentials.menu.MenuTemplates;
import com.hanielcota.essentials.modules.tpa.config.menu.TpaPendingMenuConfig;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequest;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequestType;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

public final class TpaPendingMenuRenderer {

  public ItemTemplate backTemplate(@NonNull TpaPendingMenuConfig settings) {
    return MenuTemplates.simple(settings.backIcon(), settings.backName(), settings.backLore());
  }

  public ItemTemplate emptyTemplate(@NonNull TpaPendingMenuConfig settings) {
    return MenuTemplates.simple(settings.emptyIcon(), settings.emptyName(), settings.emptyLore());
  }

  public ItemTemplate bulkTemplate(
      @NonNull Material icon,
      @NonNull String nameTemplate,
      @NonNull List<String> loreTemplate,
      int pending) {
    var pendingText = Integer.toString(pending);
    var name = nameTemplate.replace("{pending}", pendingText);
    var lore = replacePending(loreTemplate, pendingText);

    return MenuTemplates.simple(icon, name, lore);
  }

  public ItemTemplate requestTemplate(
      @NonNull TpaPendingMenuConfig settings,
      @NonNull TeleportRequest request,
      @Nullable Player requester,
      @NonNull Player viewer) {
    var name =
        applyRequestPlaceholders(settings.requestName(), settings, request, requester, viewer);
    var lore =
        applyRequestPlaceholders(settings.requestLore(), settings, request, requester, viewer);

    var builder = ItemTemplate.builder(settings.requestIcon());
    applyHead(builder, settings, request);
    builder.name(name);
    builder.lore(lore.toArray(String[]::new));
    builder.italic(false);

    return builder.build();
  }

  private static void applyHead(
      @NonNull ItemTemplate.Builder builder,
      @NonNull TpaPendingMenuConfig settings,
      @NonNull TeleportRequest request) {
    if (settings.requestIcon() != Material.PLAYER_HEAD) {
      return;
    }
    if (settings.requestUsePlayerHead()) {
      builder.head(request.requester().id());
      return;
    }
    if (!settings.requestHeadTexture().isBlank()) {
      builder.head(settings.requestHeadTexture());
    }
  }

  private static List<String> applyRequestPlaceholders(
      @NonNull List<String> lines,
      @NonNull TpaPendingMenuConfig settings,
      @NonNull TeleportRequest request,
      @Nullable Player requester,
      @NonNull Player viewer) {
    var replaced = new ArrayList<String>(lines.size());
    for (var line : lines) {
      var rendered = applyRequestPlaceholders(line, settings, request, requester, viewer);
      replaced.add(rendered);
    }
    return replaced;
  }

  private static String applyRequestPlaceholders(
      @NonNull String raw,
      @NonNull TpaPendingMenuConfig settings,
      @NonNull TeleportRequest request,
      @Nullable Player requester,
      @NonNull Player viewer) {
    var requesterName = request.requester().name();
    var type = requestTypeLabel(settings, request.type());
    var seconds = Long.toString(secondsLeft(request));
    var originWorld = originWorld(requester, settings);
    var distance = distance(requester, viewer, settings);

    return raw.replace("{player}", requesterName)
        .replace("{type}", type)
        .replace("{seconds}", seconds)
        .replace("{origin_world}", originWorld)
        .replace("{distance}", distance);
  }

  private static String requestTypeLabel(
      @NonNull TpaPendingMenuConfig settings, @NonNull TeleportRequestType type) {
    return switch (type) {
      case TPA -> settings.typeTpa();
      case TPAHERE -> settings.typeTpaHere();
    };
  }

  private static String originWorld(
      @Nullable Player requester, @NonNull TpaPendingMenuConfig settings) {
    if (requester == null) {
      return settings.unknownPlaceholder();
    }
    return requester.getWorld().getName();
  }

  private static String distance(
      @Nullable Player requester, @NonNull Player viewer, @NonNull TpaPendingMenuConfig settings) {
    if (requester == null) {
      return settings.unknownPlaceholder();
    }
    if (!requester.getWorld().getUID().equals(viewer.getWorld().getUID())) {
      return settings.unknownPlaceholder();
    }
    var meters = requester.getLocation().distance(viewer.getLocation());
    var metersText = Long.toString(Math.round(meters));
    return settings.distanceFormat().replace("{meters}", metersText);
  }

  private static long secondsLeft(@NonNull TeleportRequest request) {
    var now = Instant.now();
    var remaining = Duration.between(now, request.window().expiresAt()).toSeconds();

    return Math.max(0, remaining);
  }

  private static List<String> replacePending(
      @NonNull List<String> loreTemplate, @NonNull String pendingText) {
    var lore = new ArrayList<String>(loreTemplate.size());
    for (var line : loreTemplate) {
      lore.add(line.replace("{pending}", pendingText));
    }
    return lore;
  }
}
