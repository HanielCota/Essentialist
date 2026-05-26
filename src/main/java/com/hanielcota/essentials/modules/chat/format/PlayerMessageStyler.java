package com.hanielcota.essentials.modules.chat.format;

import com.hanielcota.essentials.modules.chat.service.ChatPermissions;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.bukkit.entity.Player;

/**
 * Converts a player's typed chat message into a styled {@link Component} based on the chat
 * permissions they hold. Single source for the legacy table is {@link LegacyTagDictionary}.
 *
 * <ul>
 *   <li>No permission → message is inserted as a literal {@link Component#text(String)}; ampersand
 *       codes and MiniMessage tags stay verbatim. This is the format-injection defence — players
 *       cannot craft clickable {@code /op} commands by typing {@code <click:...>}.
 *   <li>{@link ChatPermissions#CHAT_COLOR} → colour codes ({@code &0..&f}, {@code <red>}, ...) are
 *       recognised.
 *   <li>{@link ChatPermissions#CHAT_FORMAT} → decorations ({@code &k..&o}, {@code <bold>}, ...) are
 *       recognised.
 * </ul>
 *
 * <p>Dangerous tags — {@code <click>}, {@code <hover>}, {@code <gradient>}, {@code <rainbow>},
 * {@code <font>}, {@code <insertion>}, {@code <keybind>}, etc. — are never reachable, regardless of
 * permission. The whitelist lives in {@link #buildMiniMessage} so adding a new tag requires
 * touching one line.
 *
 * <p>Performance: the three {@link MiniMessage} instances are pre-built statics. The legacy {@code
 * &} conversion delegates to {@link LegacyTagDictionary#convertLegacy(String, boolean, boolean)}.
 * {@code MiniMessage.deserialize} is the dominant cost and runs only when the player has at least
 * one styling permission.
 */
public final class PlayerMessageStyler {

  private static final MiniMessage COLOR_ONLY = buildMiniMessage(true, false);
  private static final MiniMessage DECORATIONS_ONLY = buildMiniMessage(false, true);
  private static final MiniMessage FULL = buildMiniMessage(true, true);

  public Component style(@NonNull Player sender, @NonNull String plainMessage) {
    var hasColor = sender.hasPermission(ChatPermissions.CHAT_COLOR);
    var hasFormat = sender.hasPermission(ChatPermissions.CHAT_FORMAT);

    if (!hasColor && !hasFormat) {
      return Component.text(plainMessage);
    }

    var converted = LegacyTagDictionary.convertLegacy(plainMessage, hasColor, hasFormat);
    var mini = pickMini(hasColor, hasFormat);

    return mini.deserialize(converted);
  }

  private static MiniMessage pickMini(boolean color, boolean decorations) {
    if (color && decorations) {
      return FULL;
    }
    if (color) {
      return COLOR_ONLY;
    }
    return DECORATIONS_ONLY;
  }

  private static MiniMessage buildMiniMessage(boolean colors, boolean decorations) {
    var resolver = TagResolver.builder();
    if (colors) {
      resolver.resolver(StandardTags.color());
    }
    if (decorations) {
      resolver.resolver(StandardTags.decorations());
    }
    resolver.resolver(StandardTags.reset());

    return MiniMessage.builder().tags(resolver.build()).build();
  }
}
