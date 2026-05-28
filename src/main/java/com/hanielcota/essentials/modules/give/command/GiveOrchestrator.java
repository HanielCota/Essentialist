package com.hanielcota.essentials.modules.give.command;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.give.config.GiveConfig;
import com.hanielcota.essentials.modules.give.service.GiveService;
import io.github.hanielcota.commandframework.core.CommandActor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Runs the {@code /give} use cases: validate the requested item and amount, delegate to {@link
 * GiveService}, and route every outcome through {@link GiveNotifier}.
 */
@RequiredArgsConstructor
public final class GiveOrchestrator {

  private final ConfigHandle<GiveConfig> config;
  private final GiveService service;
  private final GiveNotifier notifier;

  public void deliver(
      @NonNull CommandActor sender,
      @NonNull Player subject,
      @NonNull Material item,
      int amount,
      boolean self) {
    var validation = validate(item, amount);

    switch (validation) {
      case INVALID_ITEM -> this.notifier.sendInvalidItem(sender);
      case AMOUNT_TOO_LARGE -> this.notifier.sendAmountTooLarge(sender);
      case OK -> {
        var result = this.service.giveResult(subject, item, amount);
        this.notifier.notifyDelivered(sender, subject, self, item, result);
      }
    }
  }

  public void giveAll(
      @NonNull CommandActor sender,
      @NonNull Iterable<? extends Player> roster,
      @NonNull Material item,
      int amount) {
    var validation = validate(item, amount);

    switch (validation) {
      case INVALID_ITEM -> this.notifier.sendInvalidItem(sender);
      case AMOUNT_TOO_LARGE -> this.notifier.sendAmountTooLarge(sender);
      case OK -> {
        var count =
            this.service.giveAll(
                roster,
                item,
                amount,
                (recipient, result) -> this.notifier.notifyRecipient(recipient, item, result));

        this.notifier.sendAllSummary(sender, item, amount, count);
      }
    }
  }

  private ValidationResult validate(@NonNull Material item, int amount) {
    if (!item.isItem()) {
      return ValidationResult.INVALID_ITEM;
    }

    var snap = this.config.value();
    if (amount > snap.maxAmount()) {
      return ValidationResult.AMOUNT_TOO_LARGE;
    }

    return ValidationResult.OK;
  }

  private enum ValidationResult {
    OK,
    INVALID_ITEM,
    AMOUNT_TOO_LARGE
  }
}
