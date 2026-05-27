package com.hanielcota.essentials.command.interceptor;

import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.core.CommandContext;
import io.github.hanielcota.commandframework.core.CommandResult;
import io.github.hanielcota.commandframework.core.CommandRoute;
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

  private static String jsonPartOf(@NonNull String fullString, int openIndex) {
    if (openIndex < 0) {
      return "";
    }
    return fullString.substring(openIndex);
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

  @Override
  public @NonNull CommandResult before(
      @NonNull CommandContext context, @NonNull List<ParsedParameter<?>> parameters) {
    var commandActor = context.actor();
    var actor = commandActor.name();

    var route = context.route();
    var displayPath = displayPathOf(route);

    var args = joinUserParameters(parameters);

    if (args.isEmpty()) {
      var template = "[CMD] %s executed /%s";
      var messageWithoutArgs = template.formatted(actor, displayPath);
      this.logger.info(messageWithoutArgs);

      return CommandResult.success();
    }

    var template = "[CMD] %s executed /%s with parameters: [%s]";
    var messageWithArgs = template.formatted(actor, displayPath, args);
    this.logger.info(messageWithArgs);

    return CommandResult.success();
  }

  private static String displayPathOf(@NonNull CommandRoute route) {
    var root = route.root();
    var pathTokens = route.path();
    if (pathTokens.isEmpty()) {
      return root;
    }
    var subPath = String.join(" ", pathTokens);
    return root + " " + subPath;
  }

  // Skip the auto-injected CommandActor parameter — it's redundant with the actor name already on
  // the log line. Any other parameter (positional args, flags) keeps its simplified value.
  private String joinUserParameters(@NonNull List<ParsedParameter<?>> parameters) {
    var joiner = new StringJoiner(", ");
    for (var parameter : parameters) {
      var rawValue = parameter.value();
      if (rawValue instanceof CommandActor) {
        continue;
      }
      var simplifiedStr = simplifyValue(rawValue);
      joiner.add(simplifiedStr);
    }
    return joiner.toString();
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
    var jsonPart = jsonPartOf(fullString, openIndex);

    var simpleName = clazz.getSimpleName();
    return simpleName + jsonPart;
  }
}
