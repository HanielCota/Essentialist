package com.hanielcota.essentials.modules.tpa.config.menu;

import com.hanielcota.essentials.menu.NavigationButtonsConfig;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequestStatus;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequestType;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.NonNull;
import org.bukkit.Material;
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
    @Comment("Material of the placeholder shown when the history is empty.") Material emptyMaterial,
    @Comment("Name of the placeholder shown when the history is empty.") String emptyName,
    @Comment("Lore of the placeholder shown when the history is empty.") List<String> emptyLore,
    @Comment("Label for an accepted request.") String statusAccepted,
    @Comment("Label for a denied request.") String statusDenied,
    @Comment("Label for an expired request.") String statusExpired,
    @Comment("Label for a cancelled request.") String statusCancelled,
    @Comment("Label for the /tpa request type.") String typeTpa,
    @Comment("Label for the /tpahere request type.") String typeTpaHere,
    @Comment("Slot of the status filter cycle button.") int filterSlot,
    @Comment("Material of the status filter button.") Material filterIcon,
    @Comment("Name of the status filter button. Placeholder: {filter}.") String filterName,
    @Comment(
            "Lore of the status filter button. Use {filter} for the current label and {options} to "
                + "expand the full list of filter states with the active one marked.")
        List<String> filterLore,
    @Comment("Label shown in {filter} when no status filter is active.") String filterAll,
    @Comment("Suffix appended to the active option in the {options} expansion.")
        String filterActiveMarker,
    @Comment("Slot of the back item. Clicking returns to the /tpa hub.") int backSlot,
    @Comment("Material of the back item.") Material backIcon,
    @Comment("Name of the back item.") String backName,
    @Comment("Lore of the back item.") List<String> backLore,
    @Comment("Click-to-copy hint sent in chat after clicking an accepted entry.")
        String destinationCopyMessage,
    @Comment("Previous/next page navigation buttons.") NavigationButtonsConfig navigation) {

  private static final DateTimeFormatter FALLBACK_TIME_FORMAT =
      DateTimeFormatter.ofPattern("dd/MM HH:mm");

  public static TpaMenuConfig defaults() {
    return new TpaMenuConfig(
        "Histórico de TPA",
        6,
        List.of(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34),
        "<gold>#{index} <yellow>{target}",
        false,
        "dd/MM HH:mm",
        List.of(
            "<gray>{type} <gold>{target}</gold>.",
            "<gray>Resultado: {status}",
            "<gray>Onde: <white>{world} {x}, {y}, {z}",
            "<gray>Quando: <white>{time}"),
        Material.BARRIER,
        "<red>Sem histórico ainda",
        List.of(
            "<gray>Você não tem teleportes recentes.",
            "",
            "<yellow>/tpa <jogador> <gray>para visitar alguém",
            "<yellow>/tpahere <jogador> <gray>para chamar alguém"),
        "<green>aceito",
        "<red>recusado",
        "<gray>expirado",
        "<yellow>cancelado",
        "Você foi até",
        "Você chamou",
        49,
        Material.HOPPER,
        "Filtro: {filter}",
        List.of(
            "<gray>Filtra os resultados mostrados.",
            "",
            "{options}",
            "",
            "<yellow>Clique para alternar."),
        "<yellow>todos",
        " <gold>◀",
        45,
        Material.ARROW,
        "<yellow>Voltar",
        List.of("<gray>Retorna ao menu de TPA."),
        "<gray>Destino: <white>{world} {x}, {y}, {z}",
        NavigationButtonsConfig.defaults(47, 51));
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
    var indexStr = Integer.toString(humanIndex);

    var withIndex = itemName.replace("{index}", indexStr);
    return withIndex.replace("{target}", target);
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
