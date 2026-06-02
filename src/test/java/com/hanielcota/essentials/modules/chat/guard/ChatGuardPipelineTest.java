package com.hanielcota.essentials.modules.chat.guard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hanielcota.essentials.modules.chat.channel.ChatChannel;
import com.hanielcota.essentials.modules.chat.channel.StaffChannel;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ChatGuardPipelineTest {

  private static final org.bukkit.entity.Player FAKE_PLAYER = fakePlayer();
  private static final ChatChannel FAKE_CHANNEL = new StaffChannel();

  private static org.bukkit.entity.Player fakePlayer() {
    return (org.bukkit.entity.Player)
        java.lang.reflect.Proxy.newProxyInstance(
            org.bukkit.entity.Player.class.getClassLoader(),
            new Class<?>[] {org.bukkit.entity.Player.class},
            (ignored, method, args) -> {
              return switch (method.getName()) {
                case "getUniqueId" -> UUID.randomUUID();
                case "toString" -> "FakePlayer";
                default -> throw new UnsupportedOperationException(method.getName());
              };
            });
  }

  @Test
  void shouldBlockShortCircuitsOnFirstBlock() {
    var firstCheck = new FixedOutcomeCheck(ChatGuardOutcome.BLOCK);
    var secondCheck = new CountingCheck(ChatGuardOutcome.ALLOW);
    var pipeline = new ChatGuardPipeline(List.of(firstCheck, secondCheck));

    var blocked = pipeline.shouldBlock(FAKE_PLAYER, FAKE_CHANNEL, "msg");

    assertTrue(blocked);
    assertEquals(0, secondCheck.evaluateCount);
  }

  @Test
  void shouldBlockReturnsFalseWhenAllAllow() {
    var checks =
        List.<ChatGuardCheck>of(
            new FixedOutcomeCheck(ChatGuardOutcome.ALLOW),
            new FixedOutcomeCheck(ChatGuardOutcome.ALLOW));
    var pipeline = new ChatGuardPipeline(checks);

    assertTrue(!pipeline.shouldBlock(FAKE_PLAYER, FAKE_CHANNEL, "msg"));
  }

  @Test
  void onPassCallsAllChecks() {
    var firstCheck = new CountingCheck(ChatGuardOutcome.ALLOW);
    var secondCheck = new CountingCheck(ChatGuardOutcome.ALLOW);
    var pipeline = new ChatGuardPipeline(List.of(firstCheck, secondCheck));

    pipeline.onPass("msg", UUID.randomUUID(), "channel");

    assertEquals(1, firstCheck.passCount);
    assertEquals(1, secondCheck.passCount);
  }

  private static final class FixedOutcomeCheck implements ChatGuardCheck {

    private final ChatGuardOutcome outcome;

    FixedOutcomeCheck(ChatGuardOutcome outcome) {
      this.outcome = outcome;
    }

    @Override
    public ChatGuardOutcome evaluate(
        org.bukkit.entity.Player sender,
        com.hanielcota.essentials.modules.chat.channel.ChatChannel channel,
        String message) {
      return outcome;
    }
  }

  private static final class CountingCheck implements ChatGuardCheck {

    private final ChatGuardOutcome outcome;
    int evaluateCount;
    int passCount;

    CountingCheck(ChatGuardOutcome outcome) {
      this.outcome = outcome;
    }

    @Override
    public ChatGuardOutcome evaluate(
        org.bukkit.entity.Player sender,
        com.hanielcota.essentials.modules.chat.channel.ChatChannel channel,
        String message) {
      evaluateCount++;
      return outcome;
    }

    @Override
    public void onPass(String message, UUID senderId, String channelId) {
      passCount++;
    }
  }
}
