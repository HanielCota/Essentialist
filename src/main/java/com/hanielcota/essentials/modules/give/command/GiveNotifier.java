package com.hanielcota.essentials.modules.give.command;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.give.config.GiveConfig;
import com.hanielcota.essentials.modules.give.service.GiveResult;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import java.util.Locale;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Owns every user-visible message emitted by {@code /give}: validation errors, dual sender/target
 * messaging, per-recipient delivery feedback for {@code /give all}, and the all-summary line. Keeps
 * the command class free of {@code .replace} chains and direct sends so it stays a thin dispatcher.
 */
@RequiredArgsConstructor
public final class GiveNotifier {

  private final ConfigHandle<GiveConfig> config;
  private final PaperCommandFramework framework;

  public void sendInvalidItem(@NonNull CommandActor sender) {
    var snap = this.config.value();
    var invalidMsg = snap.invalidItem();

    sender.sendError(invalidMsg);
  }

  public void sendAmountTooLarge(@NonNull CommandActor sender) {
    var snap = this.config.value();
    var tooLargeMsg = snap.formatAmountTooLarge();

    sender.sendError(tooLargeMsg);
  }

  /** Sender-facing delivery feedback. Routes to self / dual depending on {@code self}. */
  public void notifyDelivered(
      @NonNull CommandActor sender,
      @NonNull Player subject,
      boolean self,
      @NonNull Material item,
      @NonNull GiveResult result) {
    var snap = this.config.value();
    var itemName = displayName(item);
    var name = subject.getName();

    if (result.noneGiven()) {
      sendInventoryFull(sender, snap, self, itemName, name, result);
      return;
    }

    var messages = result.partial() ? snap.whenPartial() : snap.whenGiven();

    if (self) {
      var selfTemplate = messages.forSender(true, name);
      var selfMsg = format(selfTemplate, itemName, result);
      sender.sendSuccess(selfMsg);
      return;
    }

    var senderTemplate = messages.forSender(false, name);
    var targetTemplate = messages.forTarget(name);
    var senderMsg = format(senderTemplate, itemName, result);
    var targetMsg = format(targetTemplate, itemName, result);
    var targetActor = this.framework.actorOf(subject);

    sender.sendDualMessage(targetActor, senderMsg, targetMsg);
  }

  /** Per-recipient feedback used by {@code /give all}. */
  public void notifyRecipient(
      @NonNull Player recipient, @NonNull Material item, @NonNull GiveResult result) {
    var snap = this.config.value();
    var itemName = displayName(item);
    var name = recipient.getName();
    var recipientActor = this.framework.actorOf(recipient);

    if (result.noneGiven()) {
      var fullPair = snap.whenInventoryFull();
      var fullTemplate = fullPair.forTarget(name);
      var fullMsg = format(fullTemplate, itemName, result);
      recipientActor.sendError(fullMsg);
      return;
    }

    var messages = result.partial() ? snap.whenPartial() : snap.whenGiven();
    var template = messages.forTarget(name);
    var givenMsg = format(template, itemName, result);
    recipientActor.sendSuccess(givenMsg);
  }

  public void sendAllSummary(
      @NonNull CommandActor sender, @NonNull Material item, int amount, int count) {
    var snap = this.config.value();
    var itemName = displayName(item);
    var msg = snap.formatGivenAll(itemName, amount, count);

    sender.sendSuccess(msg);
  }

  private void sendInventoryFull(
      @NonNull CommandActor sender,
      @NonNull GiveConfig snap,
      boolean self,
      @NonNull String itemName,
      @NonNull String targetName,
      @NonNull GiveResult result) {
    var fullPair = snap.whenInventoryFull();
    var fullTemplate = fullPair.forSender(self, targetName);
    var fullMsg = format(fullTemplate, itemName, result);

    sender.sendError(fullMsg);
  }

  private static String displayName(@NonNull Material item) {
    var rawName = item.name();

    return rawName.toLowerCase(Locale.ROOT);
  }

  private static String format(
      @NonNull String template, @NonNull String item, @NonNull GiveResult result) {
    var given = result.given();
    var leftover = result.leftover();

    return fill(template, item, given, leftover);
  }

  private static String fill(
      @NonNull String template, @NonNull String item, int amount, int leftover) {
    var amountStr = Integer.toString(amount);
    var leftoverStr = Integer.toString(leftover);

    var withItem = template.replace("{item}", item);
    var withAmount = withItem.replace("{amount}", amountStr);

    return withAmount.replace("{leftover}", leftoverStr);
  }
}
