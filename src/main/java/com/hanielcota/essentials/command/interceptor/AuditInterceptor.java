package com.hanielcota.essentials.command.interceptor;

import io.github.hanielcota.commandframework.core.CommandContext;
import io.github.hanielcota.commandframework.core.CommandResult;
import io.github.hanielcota.commandframework.core.ParsedParameter;
import io.github.hanielcota.commandframework.core.RichCommandInterceptor;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    String actor = context.actor().name();
    String route = String.join(" ", context.route().path());
    logger.log(
        Level.INFO,
        () ->
            "[CMD] %s executed /%s with %d parameter(s)"
                .formatted(actor, route, parameters.size()));
    return CommandResult.success();
  }
}
