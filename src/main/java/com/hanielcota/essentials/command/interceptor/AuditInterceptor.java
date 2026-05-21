package com.hanielcota.essentials.command.interceptor;

import io.github.hanielcota.commandframework.core.CommandContext;
import io.github.hanielcota.commandframework.core.CommandResult;
import io.github.hanielcota.commandframework.core.ParsedParameter;
import io.github.hanielcota.commandframework.core.RichCommandInterceptor;
import java.util.List;
import java.util.logging.Logger;

/** Rich interceptor that logs every dispatched command with its resolved parameters. */
public final class AuditInterceptor implements RichCommandInterceptor {

  private final Logger logger;

  public AuditInterceptor(Logger logger) {
    this.logger = logger;
  }

  @Override
  public CommandResult before(CommandContext context, List<ParsedParameter<?>> parameters) {
    String actor = context.actor().name();
    String route = String.join(" ", context.route().path());
    logger.info(
        "[CMD] %s executed /%s with %d parameter(s)".formatted(actor, route, parameters.size()));
    return CommandResult.success();
  }
}
