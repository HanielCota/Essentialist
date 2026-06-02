package com.hanielcota.essentials.modules.teleport.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.hanielcota.commandframework.core.CommandActor;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.junit.jupiter.api.Test;

class DelayedTeleportPromptTest {

  @Test
  void onScheduledWithPositiveSecondsSendsMessage() {
    var actor = new RecordingTestActor();
    var prompt =
        new DelayedTeleportPrompt(
            actor, "Teleporting in {seconds}s", "Teleported!", "Cancelled!", "Failed!");

    prompt.onScheduled(5);

    assertEquals("Teleporting in 5s", actor.lastMessage);
  }

  @Test
  void onScheduledWithZeroSecondsSendsNothing() {
    var actor = new RecordingTestActor();
    var prompt =
        new DelayedTeleportPrompt(
            actor, "Teleporting in {seconds}s", "Teleported!", "Cancelled!", "Failed!");

    prompt.onScheduled(0);

    assertEquals("", actor.lastMessage);
  }

  @Test
  void onScheduledWithNegativeSecondsSendsNothing() {
    var actor = new RecordingTestActor();
    var prompt =
        new DelayedTeleportPrompt(
            actor, "Teleporting in {seconds}s", "Teleported!", "Cancelled!", "Failed!");

    prompt.onScheduled(-1);

    assertEquals("", actor.lastMessage);
  }

  @Test
  void onSuccessSendsTeleportedMessage() {
    var actor = new RecordingTestActor();
    var prompt =
        new DelayedTeleportPrompt(
            actor, "Teleporting in {seconds}s", "Teleported!", "Cancelled!", "Failed!");

    prompt.onSuccess();

    assertEquals("Teleported!", actor.lastSuccess);
  }

  @Test
  void onCancelledSendsError() {
    var actor = new RecordingTestActor();
    var prompt =
        new DelayedTeleportPrompt(
            actor, "Teleporting in {seconds}s", "Teleported!", "Cancelled!", "Failed!");

    prompt.onCancelled();

    assertEquals("Cancelled!", actor.lastError);
  }

  @Test
  void onFailedSendsError() {
    var actor = new RecordingTestActor();
    var prompt =
        new DelayedTeleportPrompt(
            actor, "Teleporting in {seconds}s", "Teleported!", "Cancelled!", "Failed!");

    prompt.onFailed();

    assertEquals("Failed!", actor.lastError);
  }

  private static final class RecordingTestActor implements CommandActor {

    String lastMessage = "";
    String lastSuccess = "";
    String lastError = "";

    @Override
    public void sendMessage(String message) {
      this.lastMessage = message;
    }

    @Override
    public void sendMessage(Component message) {
      this.lastMessage = PlainTextComponentSerializer.plainText().serialize(message);
    }

    @Override
    public void sendSuccess(String message) {
      this.lastSuccess = message;
    }

    @Override
    public void sendError(String message) {
      this.lastError = message;
    }

    @Override
    public String uniqueId() {
      return UUID.randomUUID().toString();
    }

    @Override
    public String name() {
      return "TestActor";
    }

    @Override
    public boolean isPlayer() {
      return false;
    }

    @Override
    public io.github.hanielcota.commandframework.core.ActorKind kind() {
      return io.github.hanielcota.commandframework.core.ActorKind.PLAYER;
    }

    @Override
    public boolean is(Class<?> type) {
      return false;
    }

    @Override
    public <T> T as(Class<T> type) {
      return null;
    }

    @Override
    public boolean hasPermission(String permission) {
      return false;
    }

    @Override
    public <T> T unwrap(Class<T> type) {
      return null;
    }
  }
}
