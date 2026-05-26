package com.hanielcota.essentials.modules.chat.service;

import com.hanielcota.essentials.modules.chat.permission.ChatPermissions;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.bukkit.entity.Player;

/**
 * Converts a player's typed chat message into a styled {@link Component} based on the chat
 * permissions they hold.
 *
 * <ul>
 *   <li>No permission → message is inserted as a literal {@link Component#text(String)}; ampersand
 *       codes and MiniMessage tags stay verbatim. This is the format-injection defence — players
 *       cannot craft clickable {@code /op} commands by typing {@code <click:run_command:...>}.
 *   <li>{@link ChatPermissions#CHAT_COLOR} → colour codes ({@code &0..&f}, {@code &x}, {@code
 *       <red>}, {@code <#aabbcc>}, ...) are recognised.
 *   <li>{@link ChatPermissions#CHAT_FORMAT} → decorations ({@code &k..&o}, {@code <bold>}, {@code
 *       <italic>}, ...) are recognised.
 * </ul>
 *
 * <p>Dangerous tags — {@code <click>}, {@code <hover>}, {@code <gradient>}, {@code <rainbow>},
 * {@code <font>}, {@code <insertion>}, {@code <keybind>}, etc. — are never reachable, regardless of
 * permission. The whitelist lives in {@link #buildMiniMessage} so adding a new tag requires
 * touching one line.
 *
 * <p>Performance: the four {@link MiniMessage} instances are pre-built statics. The legacy {@code
 * &} conversion walks the input once with a {@link StringBuilder}, short-circuiting when no {@code
 * &} appears. {@code MiniMessage.deserialize} is the dominant cost and runs only when the player
 * has at least one styling permission.
 */
public final class PlayerMessageStyler {

  private static final MiniMessage COLOR_ONLY = buildMiniMessage(true, false);
  private static final MiniMessage DECORATIONS_ONLY = buildMiniMessage(false, true);
  private static final MiniMessage FULL = buildMiniMessage(true, true);

  // Indexed by lowercase legacy code char. Slot is non-null when the code is a colour code.
  private static final String[] COLOR_TAGS = buildColorTable();
  // Indexed similarly for decoration / reset codes (&k..&o, &r).
  private static final String[] DECORATION_TAGS = buildDecorationTable();
  private static final int TABLE_SIZE = 'r' + 1;

  public Component style(@NonNull Player sender, @NonNull String plainMessage) {
    var hasColor = sender.hasPermission(ChatPermissions.CHAT_COLOR);
    var hasFormat = sender.hasPermission(ChatPermissions.CHAT_FORMAT);

    if (!hasColor && !hasFormat) {
      return Component.text(plainMessage);
    }

    var converted = convertLegacy(plainMessage, hasColor, hasFormat);
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

  private static String convertLegacy(
      @NonNull String input, boolean allowColors, boolean allowDecorations) {
    if (input.indexOf('&') < 0) {
      return input;
    }

    var length = input.length();
    var builder = new StringBuilder(length + 16);

    for (var i = 0; i < length; i++) {
      var c = input.charAt(i);
      if (c != '&' || i + 1 >= length) {
        builder.append(c);
        continue;
      }

      var lower = Character.toLowerCase(input.charAt(i + 1));
      var tag = tagFor(lower, allowColors, allowDecorations);
      if (tag == null) {
        builder.append(c);
        continue;
      }

      builder.append(tag);
      i++;
    }

    return builder.toString();
  }

  private static String tagFor(char lower, boolean allowColors, boolean allowDecorations) {
    if (lower >= TABLE_SIZE) {
      return null;
    }

    if (allowColors) {
      var colorTag = COLOR_TAGS[lower];
      if (colorTag != null) {
        return colorTag;
      }
    }
    if (allowDecorations) {
      var decorationTag = DECORATION_TAGS[lower];
      if (decorationTag != null) {
        return decorationTag;
      }
    }
    return null;
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

  private static String[] buildColorTable() {
    var table = new String[TABLE_SIZE];
    table['0'] = "<black>";
    table['1'] = "<dark_blue>";
    table['2'] = "<dark_green>";
    table['3'] = "<dark_aqua>";
    table['4'] = "<dark_red>";
    table['5'] = "<dark_purple>";
    table['6'] = "<gold>";
    table['7'] = "<gray>";
    table['8'] = "<dark_gray>";
    table['9'] = "<blue>";
    table['a'] = "<green>";
    table['b'] = "<aqua>";
    table['c'] = "<red>";
    table['d'] = "<light_purple>";
    table['e'] = "<yellow>";
    table['f'] = "<white>";

    return table;
  }

  private static String[] buildDecorationTable() {
    var table = new String[TABLE_SIZE];
    table['k'] = "<obfuscated>";
    table['l'] = "<bold>";
    table['m'] = "<strikethrough>";
    table['n'] = "<underlined>";
    table['o'] = "<italic>";
    // <reset> sits in the decoration permission because formatting plugins traditionally treat &r
    // as part of the decoration-control family rather than the colour family.
    table['r'] = "<reset>";

    return table;
  }
}
