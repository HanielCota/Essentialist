package com.hanielcota.essentials.modules.back.config;

import com.hanielcota.essentials.util.Numbers;
import java.time.format.DateTimeFormatter;
import java.util.List;
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
    @Comment("/back item lore lines. Placeholders: {world}, {x}, {y}, {z}, {time}.")
        List<String> itemLore,
    @Comment("/back success on click. Placeholders: {world}, {x}, {y}, {z}.") String back,
    @Comment("/back failure when there is no previous location.") String noBack) {

  private static final DateTimeFormatter FALLBACK_TIME_FORMAT =
      DateTimeFormatter.ofPattern("dd/MM HH:mm");

  public static BackConfig defaults() {
    return new BackConfig(
        "Back history",
        1,
        List.of(2, 3, 4, 5, 6),
        Material.COMPASS,
        false,
        "<gold>Back #{index}",
        "dd/MM HH:mm",
        List.of(
            "<gray>World: <white>{world}",
            "<gray>Coordinates: <white>{x}, {y}, {z}",
            "<gray>When: <white>{time}",
            "",
            "<yellow>Click to teleport."),
        "<green>Returned to <gold>{world} {x}, {y}, {z}</gold>.",
        "<red>No previous location to return to.");
  }

  public String formatItemName(int humanIndex) {
    var indexText = Integer.toString(humanIndex);
    return itemName.replace("{index}", indexText);
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
    var xStr = Numbers.compact(x);
    var yStr = Numbers.compact(y);
    var zStr = Numbers.compact(z);

    var withWorld = back.replace("{world}", world);
    var withX = withWorld.replace("{x}", xStr);
    var withY = withX.replace("{y}", yStr);
    return withY.replace("{z}", zStr);
  }
}
