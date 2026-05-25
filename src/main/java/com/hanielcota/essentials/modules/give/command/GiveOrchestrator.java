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
 * Runs the {@code /give} use cases: validate the requested item and amount against the configured
 * maximums, delegate to {@link GiveService}, and route every outcome through {@link GiveNotifier}.
 * Keeps the command thin — it only forwards sender / subject / item / amount.
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
    if (!validate(sender, item, amount)) {
      return;
    }

    var result = this.service.giveResult(subject, item, amount);
    this.notifier.notifyDelivered(sender, subject, self, item, result);
  }

  public void giveAll(
      @NonNull CommandActor sender,
      @NonNull Iterable<? extends Player> roster,
      @NonNull Material item,
      int amount) {
    if (!validate(sender, item, amount)) {
      return;
    }

    var count =
        this.service.giveAll(
            roster,
            item,
            amount,
            (recipient, result) -> this.notifier.notifyRecipient(recipient, item, result));

    this.notifier.sendAllSummary(sender, item, amount, count);
  }

  private boolean validate(@NonNull CommandActor sender, @NonNull Material item, int amount) {
    if (!item.isItem()) {
      this.notifier.sendInvalidItem(sender);
      return false;
    }

    var snap = this.config.value();
    if (amount > snap.maxAmount()) {
      this.notifier.sendAmountTooLarge(sender);
      return false;
    }

    return true;
  }
}
