package com.hanielcota.essentials.modules.info.menu;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.hanielcota.essentials.paper.PlayerProvider;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

class InfoMenuStateTest {

  private final InfoMenuState state = new InfoMenuState(new NoopPlayerProvider());

  @Test
  void prepareWithSelfSeedsCategoriesTab() {
    var viewer = UUID.randomUUID();

    state.prepare(viewer, viewer);

    assertEquals(InfoTab.CATEGORIES, state.tab(viewer));
  }

  @Test
  void prepareWithOtherJumpsStraightToPlayerTab() {
    var viewer = UUID.randomUUID();
    var target = UUID.randomUUID();

    state.prepare(viewer, target);

    assertEquals(InfoTab.PLAYER, state.tab(viewer));
  }

  @Test
  void tabDefaultsToCategoriesForUnknownViewer() {
    var stranger = UUID.randomUUID();

    assertEquals(InfoTab.CATEGORIES, state.tab(stranger));
  }

  @Test
  void switchTabOverridesPreparedTab() {
    var viewer = UUID.randomUUID();
    state.prepare(viewer, viewer);

    state.switchTab(viewer, InfoTab.ABOUT);

    assertEquals(InfoTab.ABOUT, state.tab(viewer));
  }

  @Test
  void clearRemovesPreparedStateSoNextOpenStartsFresh() {
    var viewer = UUID.randomUUID();
    var target = UUID.randomUUID();
    state.prepare(viewer, target);
    state.switchTab(viewer, InfoTab.SERVER);

    state.clear(viewer);

    assertEquals(InfoTab.CATEGORIES, state.tab(viewer));
  }

  private static final class NoopPlayerProvider implements PlayerProvider {

    @Override
    public Optional<Player> online(@NonNull UUID id) {
      return Optional.empty();
    }

    @Override
    public Optional<Player> online(@NonNull String name) {
      return Optional.empty();
    }

    @Override
    public OfflinePlayer offline(@NonNull UUID id) {
      throw new UnsupportedOperationException("offline(UUID) not exercised by these tests");
    }

    @Override
    public Optional<OfflinePlayer> offlineByName(@NonNull String name) {
      return Optional.empty();
    }

    @Override
    public Collection<Player> all() {
      return List.of();
    }

    @Override
    public int maxPlayers() {
      return 0;
    }

    @Override
    public Collection<OfflinePlayer> whitelisted() {
      return List.of();
    }
  }
}
