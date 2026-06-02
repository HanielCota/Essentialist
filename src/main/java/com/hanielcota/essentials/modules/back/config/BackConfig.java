package com.hanielcota.essentials.modules.back.config;

import com.hanielcota.essentials.menu.NavigationButtonsConfig;
import com.hanielcota.essentials.modules.teleport.history.TeleportHistory.Cause;
import com.hanielcota.essentials.shared.Numbers;
import com.hanielcota.essentials.shared.Placeholders;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import lombok.NonNull;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record BackConfig(
    @Comment("/back menu title.") String menuTitle,
    @Comment("/back menu rows (1-6). Capacity is 5 entries.") int menuRows,
    @Comment("Slots (0-based) where /back entries are placed. The first slot is where they start.")
        List<Integer> menuContentSlots,
    @Comment("/back item material (Bukkit Material name).") Material itemMaterial,
    @Comment("/back item enchanted glow.") boolean itemGlow,
    @Comment("/back item name. Placeholders: {index}.") String itemName,
    @Comment("Date/time pattern for {time}, see java.time.format.DateTimeFormatter.")
        String timeFormat,
    @Comment("/back item lore lines. Placeholders: {world}, {x}, {y}, {z}, {time}, {cause}.")
        List<String> itemLore,
    @Comment("Label shown for {cause} when the entry was saved on death.") String causeDeath,
    @Comment("Label shown for {cause} when the entry was saved on teleport.") String causeTeleport,
    @Comment("Filter button slot (0-based). Cycles all -> death -> teleport.") int filterSlot,
    @Comment("Filter button material (Bukkit Material name).") Material filterIcon,
    @Comment("Filter button name. Placeholder: {filter}.") String filterName,
    @Comment("Filter button lore. Placeholders: {filter}, {options}.") List<String> filterLore,
    @Comment("Filter option label for showing every entry.") String filterAll,
    @Comment("Marker appended to the active filter option in the lore.") String filterActiveMarker,
    @Comment("Material shown when the active filter matches no entries.") Material emptyMaterial,
    @Comment("Name of the placeholder shown when the active filter matches no entries.")
        String emptyName,
    @Comment("Lore of the placeholder shown when the active filter matches no entries.")
        List<String> emptyLore,
    @Comment("/back success on click. Placeholders: {world}, {x}, {y}, {z}.") String back,
    @Comment("/back failure when there is no previous location.") String noBack,
    @Comment("Previous/next page navigation buttons (only used when menuRows > 1).")
        NavigationButtonsConfig navigation,
    @Comment("Skip the menu and teleport directly when there is only one entry.")
        boolean directTeleport,
    @Comment("Warmup delay in seconds before teleport (0 = instant).") int warmupSeconds,
    @Comment("Cooldown in seconds between /back teleports (0 = none).") int cooldownSeconds,
    @Comment(
            "Sound key played on successful teleport (empty = no sound). Example:"
                + " entity.experience_orb.pickup")
        String teleportSound,
    @Comment("Sound volume (0.0 - 1.0).") float teleportVolume,
    @Comment("Sound pitch (0.5 - 2.0).") float teleportPitch,
    @Comment("Record /back point on join so players can return after restart/crash.")
        boolean backOnJoin,
    @Comment(
            "TeleportCause names to exclude from /back recording (e.g. UNKNOWN, DISMOUNT, EXIT_BED,"
                + " SPECTATE).")
        List<String> causeBlacklist,
    @Comment("/back menu title when staff views another player's history. Placeholder: {player}.")
        String staffViewTitle,
    @Comment("Warmup message. Placeholder: {seconds}.") String warmupMessage,
    @Comment("Warmup cancelled message (damage or movement).") String warmupCancelled,
    @Comment("Cooldown message. Placeholder: {seconds}.") String cooldownMessage) {

  private static final DateTimeFormatter FALLBACK_TIME_FORMAT =
      DateTimeFormatter.ofPattern("dd/MM HH:mm");

  public static BackConfig defaults() {
    return new BackConfig(
        "Back history",
        5,
        List.of(10, 11, 12, 13, 14),
        Material.COMPASS,
        false,
        "<gold>Back #{index}",
        "dd/MM HH:mm",
        List.of(
            "<gray>Cause: <white>{cause}",
            "<gray>World: <white>{world}",
            "<gray>Coordinates: <white>{x}, {y}, {z}",
            "<gray>When: <white>{time}",
            "",
            "<yellow>Click to teleport."),
        "<red>Death",
        "<aqua>Teleport",
        40,
        Material.HOPPER,
        "<gold>Filter: <white>{filter}",
        List.of("<gray>Showing:", "{options}", "", "<yellow>Click to cycle."),
        "<white>All",
        " <green>◄",
        Material.BARRIER,
        "<red>No entries",
        List.of("<gray>No locations match this filter."),
        "<green>Returned to <gold>{world} {x}, {y}, {z}</gold>.",
        "<red>No previous location to return to.",
        NavigationButtonsConfig.defaults(39, 41),
        false,
        0,
        0,
        "",
        1.0f,
        1.0f,
        false,
        List.of("UNKNOWN", "DISMOUNT", "EXIT_BED", "SPECTATE"),
        "<gold>Back: <white>{player}",
        "<yellow>Teleporting in <white>{seconds} <yellow>seconds...",
        "<red>Teleport cancelled.",
        "<red>You must wait <white>{seconds} <red>seconds before using /back again.");
  }

  public String formatItemName(int humanIndex) {
    var indexText = Integer.toString(humanIndex);
    return itemName.replace("{index}", indexText);
  }

  public String causeLabel(@NonNull Cause cause) {
    return switch (cause) {
      case DEATH -> causeDeath;
      case TELEPORT -> causeTeleport;
    };
  }

  /**
   * Returns the configured {@code timeFormat} as a formatter, falling back to a safe default when
   * the pattern is malformed so a bad config value cannot crash the menu render.
   */
  public DateTimeFormatter timeFormatter() {
    try {
      return DateTimeFormatter.ofPattern(timeFormat);
    } catch (IllegalArgumentException e) {
      return FALLBACK_TIME_FORMAT;
    }
  }

  public String formatBack(@NonNull String world, double x, double y, double z) {
    var xStr = Numbers.display(x);
    var yStr = Numbers.display(y);
    var zStr = Numbers.display(z);

    return Placeholders.format(back, "world", world, "x", xStr, "y", yStr, "z", zStr);
  }

  public String formatStaffViewTitle(@NonNull String playerName) {
    return staffViewTitle.replace("{player}", playerName);
  }

  public String formatCooldownMessage(int seconds) {
    var secondsText = Integer.toString(seconds);
    return cooldownMessage.replace("{seconds}", secondsText);
  }

  public boolean isCauseBlacklisted(@NonNull String causeName) {
    return causeBlacklist.contains(causeName.toUpperCase(Locale.ENGLISH));
  }
}
