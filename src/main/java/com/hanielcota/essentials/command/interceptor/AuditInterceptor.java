package com.hanielcota.essentials.command.interceptor;

import io.github.hanielcota.commandframework.core.CommandContext;
import io.github.hanielcota.commandframework.core.CommandResult;
import io.github.hanielcota.commandframework.core.ParsedParameter;
import io.github.hanielcota.commandframework.core.RichCommandInterceptor;
import java.util.List;
import java.util.StringJoiner;
import java.util.logging.Logger;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;

/** Rich interceptor that logs every dispatched command with its resolved parameters. */
@RequiredArgsConstructor
public final class AuditInterceptor implements RichCommandInterceptor {

  private final @NonNull Logger logger;

  @Override
  public @NonNull CommandResult before(
      @NonNull CommandContext context, @NonNull List<ParsedParameter<?>> parameters) {
    var actor = context.actor().name();

    var pathTokens = context.route().path();
    var route = String.join(" ", pathTokens);

    if (parameters.isEmpty()) {
      var messageWithoutArgs = "[CMD] %s executed /%s".formatted(actor, route);
      this.logger.info(messageWithoutArgs);

      return CommandResult.success();
    }

    var joiner = new StringJoiner(", ");
    for (var parameter : parameters) {
      var rawValue = parameter.value();
      var simplifiedStr = simplifyValue(rawValue);

      joiner.add(simplifiedStr);
    }

    var args = joiner.toString();
    var messageWithArgs =
        "[CMD] %s executed /%s with parameters: [%s]".formatted(actor, route, args);
    this.logger.info(messageWithArgs);

    return CommandResult.success();
  }

  private String simplifyValue(Object value) {
    if (value == null) {
      return "null";
    }

    if (value instanceof CommandSender sender) {
      return sender.getName();
    }

    var clazz = value.getClass();
    if (clazz.isPrimitive()
        || value instanceof String
        || value instanceof Number
        || value instanceof Boolean
        || clazz.isEnum()) {
      return value.toString();
    }

    // toString() of a record is "Class[a=1, b=2]"; Lombok @ToString is "Class(a=1, b=2)";
    // older Bukkit/Paper classes use "Class{a=1, b=2}". Pick whichever bracket comes first.
    var fullString = value.toString();
    var openIndex = firstBracketIndex(fullString);
    var jsonPart = openIndex >= 0 ? fullString.substring(openIndex) : "";

    return clazz.getSimpleName() + jsonPart;
  }

  private static int firstBracketIndex(@NonNull String s) {
    var earliest = -1;
    for (var bracket : new char[] {'[', '{', '('}) {
      var idx = s.indexOf(bracket);
      if (idx >= 0 && (earliest < 0 || idx < earliest)) {
        earliest = idx;
      }
    }
    return earliest;
  }
}
