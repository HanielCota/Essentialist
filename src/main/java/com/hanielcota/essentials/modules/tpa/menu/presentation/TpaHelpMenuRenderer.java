package com.hanielcota.essentials.modules.tpa.menu.presentation;

import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.hanielcota.essentials.modules.tpa.config.menu.TpaHelpMenuConfig;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequest;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequestType;
import com.hanielcota.essentials.modules.tpa.domain.TpaProfile;
import com.hanielcota.essentials.shared.Placeholders;
import com.hanielcota.essentials.shared.PlayerHeadTextures;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

public final class TpaHelpMenuRenderer {

  public ItemTemplate template(
      @NonNull Material icon,
      @NonNull String headTexture,
      @NonNull String name,
      @NonNull List<String> lore) {
    var loreArray = lore.toArray(String[]::new);

    var builder = ItemTemplate.builder(icon);
    if (icon == Material.PLAYER_HEAD && !headTexture.isBlank()) {
      builder.head(headTexture);
    }
    builder.name(name);
    builder.lore(loreArray);
    builder.italic(false);

    return builder.build();
  }

  public List<String> applyProfilePlaceholders(
      @NonNull List<String> lore,
      @NonNull Player player,
      @NonNull TpaProfile profile,
      int pending,
      @Nullable String mostContacted,
      @NonNull TpaHelpMenuConfig settings) {
    var replaced = new ArrayList<String>(lore.size());
    for (var line : lore) {
      var rendered =
          applyProfilePlaceholders(line, player, profile, pending, mostContacted, settings);
      replaced.add(rendered);
    }
    return replaced;
  }

  public String applyProfilePlaceholders(
      @NonNull String raw,
      @NonNull Player player,
      @NonNull TpaProfile profile,
      int pending,
      @Nullable String mostContacted,
      @NonNull TpaHelpMenuConfig settings) {
    var playerName = player.getName();
    var sent = Long.toString(profile.sentRequests());
    var received = Long.toString(profile.receivedRequests());
    var pendingRequests = Integer.toString(pending);
    var receiveByType = profile.receiveByType();
    var receiveTpa =
        receiveByType.getOrDefault(TeleportRequestType.TPA, false)
            ? settings.enabledLabel()
            : settings.disabledLabel();
    var receiveTpaHere =
        receiveByType.getOrDefault(TeleportRequestType.TPAHERE, false)
            ? settings.enabledLabel()
            : settings.disabledLabel();
    var statsFallback = settings.statsFallback();
    var acceptRate = TpaProfileStatsFormatter.acceptRate(profile, statsFallback);
    var avgAccept = TpaProfileStatsFormatter.averageAccept(profile, statsFallback);
    var mostContactedText =
        TpaProfileStatsFormatter.mostContactedName(mostContacted, statsFallback);

    var values =
        Map.of(
            "player", playerName,
            "sent", sent,
            "received", received,
            "pending", pendingRequests,
            "receive_tpa", receiveTpa,
            "receive_tpahere", receiveTpaHere,
            "accept_rate", acceptRate,
            "avg_accept", avgAccept,
            "most_contacted", mostContactedText);

    return Placeholders.format(raw, values);
  }

  public List<String> replacePending(@NonNull List<String> lore, int pending) {
    var replaced = new ArrayList<String>(lore.size());
    var pendingText = Integer.toString(pending);
    for (var line : lore) {
      replaced.add(line.replace("{pending}", pendingText));
    }
    return replaced;
  }

  public List<String> replaceFavorites(@NonNull List<String> lore, int favoriteCount) {
    var replaced = new ArrayList<String>(lore.size());
    var countText = Integer.toString(favoriteCount);
    for (var line : lore) {
      replaced.add(line.replace("{favorites}", countText));
    }
    return replaced;
  }

  public ItemTemplate idleOutgoingTemplate(@NonNull TpaHelpMenuConfig helpMenu) {
    var builder = ItemTemplate.builder(helpMenu.outgoingIdleIcon());
    builder.name(helpMenu.outgoingIdleName());
    builder.lore(helpMenu.outgoingIdleLore().toArray(String[]::new));
    builder.italic(false);
    return builder.build();
  }

  public ItemTemplate activeOutgoingTemplate(
      @NonNull TpaHelpMenuConfig helpMenu, @NonNull TeleportRequest request) {
    var targetName = request.target().name();
    var typeLabel = outgoingTypeLabel(helpMenu, request);
    var seconds = Long.toString(secondsLeft(request));
    var name = outgoingName(helpMenu, targetName, typeLabel, seconds);
    var lore = outgoingLore(helpMenu, targetName, typeLabel, seconds);

    var builder = ItemTemplate.builder(helpMenu.outgoingIcon());
    applyOutgoingHead(builder, helpMenu, request);
    builder.name(name);
    builder.lore(lore.toArray(String[]::new));
    builder.italic(false);

    return builder.build();
  }

  private static String outgoingTypeLabel(
      @NonNull TpaHelpMenuConfig helpMenu, @NonNull TeleportRequest request) {
    return request.type() == TeleportRequestType.TPA
        ? helpMenu.outgoingTypeTpa()
        : helpMenu.outgoingTypeTpaHere();
  }

  private static String outgoingName(
      @NonNull TpaHelpMenuConfig helpMenu,
      @NonNull String targetName,
      @NonNull String typeLabel,
      @NonNull String seconds) {
    return Placeholders.format(
        helpMenu.outgoingName(), "target", targetName, "type", typeLabel, "seconds", seconds);
  }

  private static List<String> outgoingLore(
      @NonNull TpaHelpMenuConfig helpMenu,
      @NonNull String targetName,
      @NonNull String typeLabel,
      @NonNull String seconds) {
    var values = Map.of("target", targetName, "type", typeLabel, "seconds", seconds);
    return helpMenu.outgoingLore().stream().map(line -> Placeholders.format(line, values)).toList();
  }

  private static void applyOutgoingHead(
      @NonNull ItemTemplate.Builder builder,
      @NonNull TpaHelpMenuConfig helpMenu,
      @NonNull TeleportRequest request) {
    if (helpMenu.outgoingIcon() != Material.PLAYER_HEAD) {
      return;
    }
    if (helpMenu.outgoingUsePlayerHead()) {
      var targetId = request.target().id();
      PlayerHeadTextures.applyTo(builder, targetId);
      return;
    }
    if (!helpMenu.outgoingHeadTexture().isBlank()) {
      builder.head(helpMenu.outgoingHeadTexture());
    }
  }

  private static long secondsLeft(@NonNull TeleportRequest request) {
    var now = Instant.now();
    var remaining = Duration.between(now, request.window().expiresAt()).toSeconds();

    return Math.max(0, remaining);
  }
}
