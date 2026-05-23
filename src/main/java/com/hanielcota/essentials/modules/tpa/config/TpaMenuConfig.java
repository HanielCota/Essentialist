package com.hanielcota.essentials.modules.tpa.config;

import com.hanielcota.essentials.modules.tpa.model.TeleportRequestStatus;
import com.hanielcota.essentials.modules.tpa.model.TeleportRequestType;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.NonNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/** Appearance and labels of the {@code /tpahistory} menu. */
@ConfigSerializable
public record TpaMenuConfig(
    @Comment("/tpahistory menu title.") String title,
    @Comment("/tpahistory menu rows (1-6).") int rows,
    @Comment("Slots (0-based) where /tpahistory entries are placed.") List<Integer> contentSlots,
    @Comment("History item name. Placeholders: {index}, {target}.") String itemName,
    @Comment("History item enchanted glow.") boolean itemGlow,
    @Comment("Date/time pattern for {time}, see java.time.format.DateTimeFormatter.")
        String timeFormat,
    @Comment(
            "History item lore. Placeholders: {target}, {type}, {status}, {world}, {x}, {y}, "
                + "{z}, {time}.")
        List<String> itemLore,
    @Comment("Label for an accepted request.") String statusAccepted,
    @Comment("Label for a denied request.") String statusDenied,
    @Comment("Label for an expired request.") String statusExpired,
    @Comment("Label for a cancelled request.") String statusCancelled,
    @Comment("Label for the /tpa request type.") String typeTpa,
    @Comment("Label for the /tpahere request type.") String typeTpaHere) {

  private static final DateTimeFormatter FALLBACK_TIME_FORMAT =
      DateTimeFormatter.ofPattern("dd/MM HH:mm");

  public static TpaMenuConfig defaults() {
    return new TpaMenuConfig(
        "<dark_aqua>Histórico de TPA",
        1,
        List.of(2, 3, 4, 5, 6),
        "<gold>#{index} <yellow>{target}",
        false,
        "dd/MM HH:mm",
        List.of(
            "<gray>Para: <white>{target}",
            "<gray>Tipo: <white>{type}",
            "<gray>Resultado: {status}",
            "<gray>Local: <white>{world} {x}, {y}, {z}",
            "<gray>Quando: <white>{time}"),
        "<green>Aceito",
        "<red>Recusado",
        "<gray>Expirado",
        "<yellow>Cancelado",
        "Ir até o jogador",
        "Trazer o jogador");
  }

  /** Configured time pattern, falling back to a safe default when malformed. */
  public DateTimeFormatter timeFormatter() {
    try {
      return DateTimeFormatter.ofPattern(timeFormat);
    } catch (IllegalArgumentException e) {
      return FALLBACK_TIME_FORMAT;
    }
  }

  public String formatItemName(int humanIndex, @NonNull String target) {
    return itemName.replace("{index}", Integer.toString(humanIndex)).replace("{target}", target);
  }

  public String statusLabel(@NonNull TeleportRequestStatus status) {
    return switch (status) {
      case ACCEPTED -> statusAccepted;
      case DENIED -> statusDenied;
      case EXPIRED -> statusExpired;
      case CANCELLED -> statusCancelled;
    };
  }

  public String typeLabel(@NonNull TeleportRequestType type) {
    return switch (type) {
      case TPA -> typeTpa;
      case TPAHERE -> typeTpaHere;
    };
  }
}
