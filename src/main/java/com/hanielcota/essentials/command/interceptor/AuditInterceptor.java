package com.hanielcota.essentials.command.interceptor;

import io.github.hanielcota.commandframework.core.CommandContext;
import io.github.hanielcota.commandframework.core.CommandResult;
import io.github.hanielcota.commandframework.core.ParsedParameter;
import io.github.hanielcota.commandframework.core.RichCommandInterceptor;
import java.util.List;
import java.util.StringJoiner;
import java.util.logging.Logger;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.NonNull;

/** Rich interceptor that logs every dispatched command with its resolved parameters. */
public final class AuditInterceptor implements RichCommandInterceptor {

  private final Logger logger;

  public AuditInterceptor(Logger logger) {
    this.logger = logger;
  }

  @Override
  public @NonNull CommandResult before(
      @NonNull CommandContext context, @NonNull List<ParsedParameter<?>> parameters) {

    var actor = context.actor().name();
    var route = String.join(" ", context.route().path());

    if (parameters.isEmpty()) {
      var messageWithoutArgs = "[CMD] %s executed /%s".formatted(actor, route);
      logger.info(() -> messageWithoutArgs);
      return CommandResult.success();
    }

    var joiner = new StringJoiner(", ");

    for (var parameter : parameters) {
      joiner.add(simplifyValue(parameter.value()));
    }

    var args = joiner.toString();
    var messageWithArgs =
        "[CMD] %s executed /%s with parameters: [%s]".formatted(actor, route, args);
    logger.info(() -> messageWithArgs);

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

    var fullString = value.toString();
    return clazz.getSimpleName()
        + fullString.substring(fullString.indexOf('{') != -1 ? fullString.indexOf('{') : 0);
  }
}
