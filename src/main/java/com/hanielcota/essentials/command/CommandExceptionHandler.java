package com.hanielcota.essentials.command;

import io.github.hanielcota.commandframework.core.CommandContext;
import io.github.hanielcota.commandframework.core.CommandResult;
import io.github.hanielcota.commandframework.core.CommandStatus;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.NonNull;

/**
 * Registers exception handlers for the command framework. Extracted from {@link CommandBootstrap}
 * so error presentation and logging are separate from framework wiring.
 */
public final class CommandExceptionHandler {

  private CommandExceptionHandler() {}

  public static void register(
      @NonNull PaperCommandFramework.Builder builder, @NonNull Logger logger) {
    builder.onException(
        IllegalArgumentException.class, (ctx, ex) -> handleIllegalArgument(ctx, ex));
    builder.onException(RuntimeException.class, (ctx, ex) -> handleUnexpected(ctx, ex, logger));
  }

  private static CommandResult handleIllegalArgument(CommandContext ctx, RuntimeException ex) {
    var actor = ctx.actor();
    var errorMessage = ex.getMessage();

    var displayMessage = "<red>Erro: " + errorMessage;
    actor.sendError(displayMessage);

    return CommandResult.failure(CommandStatus.INVALID_USAGE, errorMessage);
  }

  private static CommandResult handleUnexpected(
      CommandContext ctx, RuntimeException ex, Logger logger) {
    var actor = ctx.actor();
    actor.sendError("<red>Ocorreu um erro inesperado.");

    Supplier<String> messageSupplier = () -> "Unhandled command exception";
    logger.log(Level.WARNING, ex, messageSupplier);

    return CommandResult.failure(CommandStatus.ERROR, "unexpected");
  }
}
