package com.hanielcota.essentials.modules.msg.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.msg.config.MsgConfig;
import com.hanielcota.essentials.modules.msg.service.MsgService;
import com.hanielcota.essentials.modules.msg.service.SocialSpyBridge;
import com.hanielcota.essentials.paper.ActorFactory;
import io.github.hanielcota.commandframework.core.CommandActor;
import java.util.Optional;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;

class MsgExchangeOrchestratorTest {

  @Test
  void sendPairsParticipantsForReply() {
    var partners = new MsgService();
    var configHandle =
        new ConfigHandle<MsgConfig>() {
          @Override
          public String name() {
            return "msg";
          }

          @Override
          public MsgConfig value() {
            return MsgConfig.defaults();
          }
        };
    var actorFactory =
        new ActorFactory() {
          @Override
          public CommandActor actorOf(org.bukkit.entity.Player player) {
            var delivered = new java.util.ArrayList<Object>();
            return new CommandActor() {
              @Override
              public void sendMessage(String message) {
                delivered.add(message);
              }

              @Override
              public void sendMessage(Component message) {
                delivered.add(message);
              }

              @Override
              public void sendSuccess(String message) {
                delivered.add(message);
              }

              @Override
              public void sendError(String message) {
                delivered.add(message);
              }

              @Override
              public String uniqueId() {
                return player.getUniqueId().toString();
              }

              @Override
              public String name() {
                return "Test";
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
            };
          }
        };
    var notifier = new MsgNotifier(configHandle, actorFactory);
    var spyBridge = new SocialSpyBridge(() -> Optional.empty());
    var orchestrator = new MsgExchangeOrchestrator(partners, notifier, spyBridge);

    var alice = fakePlayer("Alice", UUID.randomUUID());
    var bob = fakePlayer("Bob", UUID.randomUUID());
    orchestrator.send(alice, bob, "Hello");

    assertEquals(bob.getUniqueId(), partners.lastPartner(alice.getUniqueId()).orElseThrow());
    assertEquals(alice.getUniqueId(), partners.lastPartner(bob.getUniqueId()).orElseThrow());
  }

  private static org.bukkit.entity.Player fakePlayer(String name, UUID id) {
    return (org.bukkit.entity.Player)
        java.lang.reflect.Proxy.newProxyInstance(
            org.bukkit.entity.Player.class.getClassLoader(),
            new Class<?>[] {org.bukkit.entity.Player.class},
            (ignored, method, args) -> {
              return switch (method.getName()) {
                case "getName" -> name;
                case "getUniqueId" -> id;
                case "getEffectivePermissions" -> java.util.Set.of();
                case "toString" -> name;
                default -> throw new UnsupportedOperationException(method.getName());
              };
            });
  }
}
