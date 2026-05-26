package com.hanielcota.essentials.core.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hanielcota.essentials.modules.warps.domain.Warp;
import com.hanielcota.essentials.modules.warps.repository.WarpCache;
import com.hanielcota.essentials.modules.warps.service.WarpService;
import com.hanielcota.essentials.support.NoopAsyncDatabaseWriter;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.NonNull;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.Test;

class WarpsApiAdapterTest {

  private static final UUID CREATOR = UUID.randomUUID();

  @Test
  void warpsReturnsCachedEntriesSortedByName() {
    var cache = new WarpCache();
    cache.put(warp("Zulu"));
    cache.put(warp("Alpha"));

    var adapter =
        new WarpsApiAdapter(new WarpService(null, cache, NoopAsyncDatabaseWriter.INSTANCE));

    var names = adapter.warps().stream().map(Warp::name).toList();
    assertEquals(List.of("Alpha", "Zulu"), names);
  }

  @Test
  void findWarpIsCaseInsensitive() {
    var cache = new WarpCache();
    cache.put(warp("Spawn"));

    var adapter =
        new WarpsApiAdapter(new WarpService(null, cache, NoopAsyncDatabaseWriter.INSTANCE));

    assertTrue(adapter.findWarp("SPAWN").isPresent());
    assertTrue(adapter.findWarp("spawn").isPresent());
  }

  @Test
  void canUseHonorsPerWarpPermissionAndWildcard() {
    var cache = new WarpCache();
    cache.put(warp("Vip"));

    var adapter =
        new WarpsApiAdapter(new WarpService(null, cache, NoopAsyncDatabaseWriter.INSTANCE));

    assertFalse(adapter.canUse(new StubPermissible(Set.of()), "Vip"));
    assertTrue(adapter.canUse(new StubPermissible(Set.of("essentials.warp.use.vip")), "Vip"));
    assertTrue(adapter.canUse(new StubPermissible(Set.of("essentials.warp.use.*")), "Vip"));
  }

  @Test
  void visibleToFiltersWarpsTheViewerCannotUse() {
    var cache = new WarpCache();
    cache.put(warp("Public"));
    cache.put(warp("Vip"));

    var adapter =
        new WarpsApiAdapter(new WarpService(null, cache, NoopAsyncDatabaseWriter.INSTANCE));

    var viewer = new StubPermissible(Set.of("essentials.warp.use.public"));
    var visible = adapter.visibleTo(viewer).stream().map(Warp::name).toList();

    assertEquals(List.of("Public"), visible);
  }

  private static Warp warp(@NonNull String name) {
    return new Warp(name, "world", 0, 64, 0, 0, 0, 0L, CREATOR);
  }

  private static final class StubPermissible implements Permissible {

    private final Set<String> nodes;

    StubPermissible(@NonNull Set<String> nodes) {
      this.nodes = nodes;
    }

    @Override
    public boolean isPermissionSet(@NonNull String name) {
      return this.nodes.contains(name);
    }

    @Override
    public boolean isPermissionSet(@NonNull Permission perm) {
      return isPermissionSet(perm.getName());
    }

    @Override
    public boolean hasPermission(@NonNull String name) {
      return this.nodes.contains(name);
    }

    @Override
    public boolean hasPermission(@NonNull Permission perm) {
      return hasPermission(perm.getName());
    }

    @Override
    public PermissionAttachment addAttachment(
        @NonNull Plugin plugin, @NonNull String name, boolean value) {
      throw new UnsupportedOperationException();
    }

    @Override
    public PermissionAttachment addAttachment(@NonNull Plugin plugin) {
      throw new UnsupportedOperationException();
    }

    @Override
    public PermissionAttachment addAttachment(
        @NonNull Plugin plugin, @NonNull String name, boolean value, int ticks) {
      throw new UnsupportedOperationException();
    }

    @Override
    public PermissionAttachment addAttachment(@NonNull Plugin plugin, int ticks) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void removeAttachment(@NonNull PermissionAttachment attachment) {}

    @Override
    public void recalculatePermissions() {}

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
      return Set.of();
    }

    @Override
    public boolean isOp() {
      return false;
    }

    @Override
    public void setOp(boolean value) {}
  }
}
