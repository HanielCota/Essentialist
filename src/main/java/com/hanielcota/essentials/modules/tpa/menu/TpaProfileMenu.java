package com.hanielcota.essentials.modules.tpa.menu;

import com.github.hanielcota.menuframework.MenuFramework;
import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.api.MenuSession;
import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.github.hanielcota.menuframework.definition.PaginationConfig;
import com.github.hanielcota.menuframework.definition.SlotDefinition;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.EssentialsMenu;
import com.hanielcota.essentials.menu.MenuLayouts;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.config.menu.TpaProfileMenuConfig;
import com.hanielcota.essentials.modules.tpa.domain.TpaContact;
import com.hanielcota.essentials.modules.tpa.domain.TpaProfile;
import com.hanielcota.essentials.modules.tpa.menu.presentation.TpaProfileStatsFormatter;
import com.hanielcota.essentials.modules.tpa.service.TeleportRequestService;
import com.hanielcota.essentials.modules.tpa.service.TpaContactService;
import com.hanielcota.essentials.modules.tpa.service.TpaProfileService;
import com.hanielcota.essentials.util.ComponentUtils;
import com.hanielcota.essentials.util.Placeholders;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/** Stats-only sub-menu opened from the profile slot in {@code TpaHelpMenu}. */
@RequiredArgsConstructor
public final class TpaProfileMenu implements EssentialsMenu {

  public static final String ID = "essentials.tpa.profile";

  private final ConfigHandle<TpaConfig> config;
  private final TpaProfileService profiles;
  private final TeleportRequestService requests;
  private final TpaContactService contacts;

  static List<Integer> contentSlots(@NonNull TpaProfileMenuConfig settings, int rows) {
    return List.of(
        MenuLayouts.sanitizeSlot(settings.headSlot(), rows, 0),
        MenuLayouts.sanitizeSlot(settings.sentSlot(), rows, 0),
        MenuLayouts.sanitizeSlot(settings.receivedSlot(), rows, 0),
        MenuLayouts.sanitizeSlot(settings.acceptRateSlot(), rows, 0),
        MenuLayouts.sanitizeSlot(settings.avgResponseSlot(), rows, 0),
        MenuLayouts.sanitizeSlot(settings.mostContactedSlot(), rows, 0),
        MenuLayouts.sanitizeSlot(settings.backSlot(), rows, 0));
  }

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void register(@NonNull MenuService menus) {
    var settings = this.config.value().profileMenu();
    var rows = MenuLayouts.clampRows(settings.rows());

    var builder = MenuFramework.builder(ID, menus);
    builder.rows(rows);
    builder.title(ComponentUtils.mini(settings.title()));
    builder.pagination(
        PaginationConfig.builder().contentSlots(contentSlots(settings, rows)).build());
    builder.dynamicContent(this::buildSlots);

    var menu = builder.build();
    menu.register();
  }

  private List<SlotDefinition> buildSlots(@NonNull Player player, @NonNull MenuSession session) {
    var settings = this.config.value().profileMenu();
    var rows = MenuLayouts.clampRows(settings.rows());

    var playerId = player.getUniqueId();
    var profile = this.profiles.profile(playerId);
    var pending = this.requests.incoming(playerId).size();
    var mostContacted =
        this.contacts.mostContacted(playerId).map(TpaContact::targetName).orElse(null);

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
    var lore = replacePlayer(settings.headLore(), playerName);

    var builder = ItemTemplate.builder(settings.headIcon());
    if (settings.headUsePlayerHead()) {
      builder.head(player.getUniqueId());
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
    var lore = replace(settings.sentLore(), "{sent}", sent);
    var template = simpleTemplate(settings.sentIcon(), name, lore);

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
    var template = simpleTemplate(settings.receivedIcon(), name, lore);

    var safeSlot = MenuLayouts.sanitizeSlot(settings.receivedSlot(), rows, 0);
    return SlotDefinition.of(safeSlot, template, click -> {});
  }

  private SlotDefinition acceptRateSlot(
      @NonNull TpaProfileMenuConfig settings, int rows, @NonNull TpaProfile profile) {
    var fallback = settings.statsFallback();
    var rate = TpaProfileStatsFormatter.acceptRate(profile, fallback);
    var name = settings.acceptRateName().replace("{accept_rate}", rate);
    var lore = replace(settings.acceptRateLore(), "{accept_rate}", rate);
    var template = simpleTemplate(settings.acceptRateIcon(), name, lore);

    var safeSlot = MenuLayouts.sanitizeSlot(settings.acceptRateSlot(), rows, 0);
    return SlotDefinition.of(safeSlot, template, click -> {});
  }

  private SlotDefinition avgResponseSlot(
      @NonNull TpaProfileMenuConfig settings, int rows, @NonNull TpaProfile profile) {
    var fallback = settings.statsFallback();
    var avg = TpaProfileStatsFormatter.averageAccept(profile, fallback);
    var name = settings.avgResponseName().replace("{avg_accept}", avg);
    var lore = replace(settings.avgResponseLore(), "{avg_accept}", avg);
    var template = simpleTemplate(settings.avgResponseIcon(), name, lore);

    var safeSlot = MenuLayouts.sanitizeSlot(settings.avgResponseSlot(), rows, 0);
    return SlotDefinition.of(safeSlot, template, click -> {});
  }

  private SlotDefinition mostContactedSlot(
      @NonNull TpaProfileMenuConfig settings,
      int rows,
      @org.jspecify.annotations.Nullable String mostContacted) {
    var fallback = settings.statsFallback();
    var label = TpaProfileStatsFormatter.mostContactedName(mostContacted, fallback);
    var name = settings.mostContactedName().replace("{most_contacted}", label);
    var lore = replace(settings.mostContactedLore(), "{most_contacted}", label);
    var template = simpleTemplate(settings.mostContactedIcon(), name, lore);

    var safeSlot = MenuLayouts.sanitizeSlot(settings.mostContactedSlot(), rows, 0);
    return SlotDefinition.of(safeSlot, template, click -> {});
  }

  private SlotDefinition backSlot(@NonNull TpaProfileMenuConfig settings, int rows) {
    var template = simpleTemplate(settings.backIcon(), settings.backName(), settings.backLore());
    var safeSlot = MenuLayouts.sanitizeSlot(settings.backSlot(), rows, 0);

    return SlotDefinition.of(safeSlot, template, click -> click.switchTo(TpaHelpMenu.ID));
  }

  private static ItemTemplate simpleTemplate(
      @NonNull Material material, @NonNull String name, @NonNull List<String> lore) {
    var builder = ItemTemplate.builder(material);
    builder.name(name);
    builder.lore(lore.toArray(String[]::new));
    builder.italic(false);
    return builder.build();
  }

  private static List<String> replacePlayer(@NonNull List<String> lines, @NonNull String player) {
    var replaced = new ArrayList<String>(lines.size());
    for (var line : lines) {
      replaced.add(line.replace("{player}", player));
    }
    return replaced;
  }

  private static List<String> replace(
      @NonNull List<String> lines, @NonNull String token, @NonNull String value) {
    var replaced = new ArrayList<String>(lines.size());
    for (var line : lines) {
      replaced.add(line.replace(token, value));
    }
    return replaced;
  }
}
