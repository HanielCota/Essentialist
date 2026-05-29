package com.hanielcota.essentials.command;

import io.github.hanielcota.commandframework.core.CommandContext;
import io.github.hanielcota.commandframework.core.CommandResult;
import io.github.hanielcota.commandframework.core.CommandStatus;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Registers exception handlers for the command framework. Error message templates are injectable so
 * presentation logic can change (i18n, branding) without modifying this class.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommandExceptionHandler {

  public static void register(
      @NonNull PaperCommandFramework.Builder builder,
      @NonNull Logger logger,
      @NonNull String illegalArgTemplate,
      @NonNull String unexpectedTemplate) {
    builder.onException(
        IllegalArgumentException.class,
        (ctx, ex) -> handleIllegalArgument(ctx, ex, illegalArgTemplate));
    builder.onException(
        RuntimeException.class, (ctx, ex) -> handleUnexpected(ctx, ex, logger, unexpectedTemplate));
  }

  private static CommandResult handleIllegalArgument(
      CommandContext ctx, RuntimeException ex, @NonNull String template) {
    var actor = ctx.actor();
    var errorMessage = ex.getMessage();

    var displayMessage = template + errorMessage;
    actor.sendError(displayMessage);

    return CommandResult.failure(CommandStatus.INVALID_USAGE, errorMessage);
  }

  private static CommandResult handleUnexpected(
      CommandContext ctx, RuntimeException ex, Logger logger, @NonNull String template) {
    var actor = ctx.actor();
    actor.sendError(template);

    Supplier<String> messageSupplier = () -> "Unhandled command exception";
    logger.log(Level.WARNING, ex, messageSupplier);

    return CommandResult.failure(CommandStatus.ERROR, "unexpected");
  }
}
