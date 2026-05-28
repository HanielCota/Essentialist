package com.hanielcota.essentials.modules.tpa.menu.presentation;

import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.github.hanielcota.menuframework.definition.SlotDefinition;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.MenuLayouts;
import com.hanielcota.essentials.menu.MenuTemplates;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.config.menu.TpaProfileMenuConfig;
import com.hanielcota.essentials.modules.tpa.domain.TpaContact;
import com.hanielcota.essentials.modules.tpa.domain.TpaProfile;
import com.hanielcota.essentials.modules.tpa.menu.help.TpaHelpMenu;
import com.hanielcota.essentials.modules.tpa.service.TpaContactService;
import com.hanielcota.essentials.modules.tpa.service.TpaProfileService;
import com.hanielcota.essentials.modules.tpa.service.request.TeleportRequestService;
import com.hanielcota.essentials.shared.Placeholders;
import com.hanielcota.essentials.shared.PlayerHeadTextures;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

@RequiredArgsConstructor
public final class TpaProfileMenuRenderer {

  private final @NonNull ConfigHandle<TpaConfig> config;
  private final @NonNull TpaProfileService profiles;
  private final @NonNull TeleportRequestService requests;
  private final @NonNull TpaContactService contacts;

  public List<SlotDefinition> buildSlots(@NonNull Player player, int rows) {
    var settings = this.config.value().profileMenu();

    var playerId = player.getUniqueId();
    var profile = this.profiles.profile(playerId);
    var pending = this.requests.incoming(playerId).size();
    var mostContacted = this.contacts.mostContacted(playerId).orElse(null);

    var slots = new ArrayList<SlotDefinition>(7);
    slots.add(headSlot(settings, rows, player));
    slots.add(sentSlot(settings, rows, profile));
    slots.add(receivedSlot(settings, rows, profile, pending));
    slots.add(acceptRateSlot(settings, rows, profile));
    slots.add(avgResponseSlot(settings, rows, profile));
    slots.add(mostContactedSlot(settings, rows, mostContacted));
    slots.add(backSlot(settings, rows));
    return slots;
  }

  private SlotDefinition headSlot(
      @NonNull TpaProfileMenuConfig settings, int rows, @NonNull Player player) {
    var playerName = player.getName();
    var name = settings.headName().replace("{player}", playerName);
    var lore = replaceAll(settings.headLore(), "{player}", playerName);

    var builder = ItemTemplate.builder(settings.headIcon());
    if (settings.headUsePlayerHead()) {
      PlayerHeadTextures.applyTo(builder, player);
    } else if (settings.headIcon() == Material.PLAYER_HEAD
        && !settings.headHeadTexture().isBlank()) {
      builder.head(settings.headHeadTexture());
    }
    builder.name(name);
    builder.lore(lore.toArray(String[]::new));
    builder.italic(false);
    var template = builder.build();

    var safeSlot = MenuLayouts.sanitizeSlot(settings.headSlot(), rows, 0);
    return SlotDefinition.of(safeSlot, template, click -> {});
  }

  private SlotDefinition sentSlot(
      @NonNull TpaProfileMenuConfig settings, int rows, @NonNull TpaProfile profile) {
    var sent = Long.toString(profile.sentRequests());
    var name = settings.sentName().replace("{sent}", sent);
    var lore = replaceAll(settings.sentLore(), "{sent}", sent);
    var template = MenuTemplates.simple(settings.sentIcon(), name, lore);

    var safeSlot = MenuLayouts.sanitizeSlot(settings.sentSlot(), rows, 0);
    return SlotDefinition.of(safeSlot, template, click -> {});
  }

  private SlotDefinition receivedSlot(
      @NonNull TpaProfileMenuConfig settings, int rows, @NonNull TpaProfile profile, int pending) {
    var received = Long.toString(profile.receivedRequests());
    var pendingText = Integer.toString(pending);
    var name = settings.receivedName().replace("{received}", received);
    var values = Map.of("received", received, "pending", pendingText);
    var lore = new ArrayList<String>(settings.receivedLore().size());
    for (var line : settings.receivedLore()) {
      lore.add(Placeholders.format(line, values));
    }
    var template = MenuTemplates.simple(settings.receivedIcon(), name, lore);

    var safeSlot = MenuLayouts.sanitizeSlot(settings.receivedSlot(), rows, 0);
    return SlotDefinition.of(safeSlot, template, click -> {});
  }

  private SlotDefinition acceptRateSlot(
      @NonNull TpaProfileMenuConfig settings, int rows, @NonNull TpaProfile profile) {
    var fallback = settings.statsFallback();
    var rate = TpaProfileStatsFormatter.acceptRate(profile, fallback);
    var name = settings.acceptRateName().replace("{accept_rate}", rate);
    var lore = replaceAll(settings.acceptRateLore(), "{accept_rate}", rate);
    var template = MenuTemplates.simple(settings.acceptRateIcon(), name, lore);

    var safeSlot = MenuLayouts.sanitizeSlot(settings.acceptRateSlot(), rows, 0);
    return SlotDefinition.of(safeSlot, template, click -> {});
  }

  private SlotDefinition avgResponseSlot(
      @NonNull TpaProfileMenuConfig settings, int rows, @NonNull TpaProfile profile) {
    var fallback = settings.statsFallback();
    var avg = TpaProfileStatsFormatter.averageAccept(profile, fallback);
    var name = settings.avgResponseName().replace("{avg_accept}", avg);
    var lore = replaceAll(settings.avgResponseLore(), "{avg_accept}", avg);
    var template = MenuTemplates.simple(settings.avgResponseIcon(), name, lore);

    var safeSlot = MenuLayouts.sanitizeSlot(settings.avgResponseSlot(), rows, 0);
    return SlotDefinition.of(safeSlot, template, click -> {});
  }

  private SlotDefinition mostContactedSlot(
      @NonNull TpaProfileMenuConfig settings, int rows, @Nullable TpaContact mostContacted) {
    var fallback = settings.statsFallback();
    var contactName = mostContacted != null ? mostContacted.targetName() : null;
    var label = TpaProfileStatsFormatter.mostContactedName(contactName, fallback);
    var name = settings.mostContactedName().replace("{most_contacted}", label);
    var lore = replaceAll(settings.mostContactedLore(), "{most_contacted}", label);

    var builder = ItemTemplate.builder(settings.mostContactedIcon());
    if (settings.mostContactedIcon() == Material.PLAYER_HEAD && mostContacted != null) {
      PlayerHeadTextures.applyTo(builder, mostContacted.targetId());
    }
    builder.name(name);
    builder.lore(lore.toArray(String[]::new));
    builder.italic(false);
    var template = builder.build();

    var safeSlot = MenuLayouts.sanitizeSlot(settings.mostContactedSlot(), rows, 0);
    return SlotDefinition.of(safeSlot, template, click -> {});
  }

  private SlotDefinition backSlot(@NonNull TpaProfileMenuConfig settings, int rows) {
    var template =
        MenuTemplates.simple(settings.backIcon(), settings.backName(), settings.backLore());
    var safeSlot = MenuLayouts.sanitizeSlot(settings.backSlot(), rows, 0);

    return SlotDefinition.of(safeSlot, template, click -> click.switchTo(TpaHelpMenu.ID));
  }

  private static List<String> replaceAll(
      @NonNull List<String> lines, @NonNull String token, @NonNull String value) {
    var replaced = new ArrayList<String>(lines.size());
    for (var line : lines) {
      replaced.add(line.replace(token, value));
    }
    return replaced;
  }
}
