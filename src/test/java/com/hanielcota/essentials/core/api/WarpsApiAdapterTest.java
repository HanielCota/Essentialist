package com.hanielcota.essentials.core.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hanielcota.essentials.database.AsyncDatabaseWriter;
import com.hanielcota.essentials.modules.warps.domain.Warp;
import com.hanielcota.essentials.modules.warps.repository.WarpCache;
import com.hanielcota.essentials.modules.warps.service.WarpService;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import lombok.NonNull;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.junit.jupiter.api.Test;

class WarpsApiAdapterTest {

  private static final UUID CREATOR = UUID.randomUUID();

  @Test
  void warpsReturnsCachedEntriesSortedByName() {
    var cache = new WarpCache();
    cache.put(warp("Zulu"));
    cache.put(warp("Alpha"));

    var adapter = new WarpsApiAdapter(new WarpService(null, cache, new NoopWriter()));

    var names = adapter.warps().stream().map(Warp::name).toList();
    assertEquals(java.util.List.of("Alpha", "Zulu"), names);
  }

  @Test
  void findWarpIsCaseInsensitive() {
    var cache = new WarpCache();
    cache.put(warp("Spawn"));

    var adapter = new WarpsApiAdapter(new WarpService(null, cache, new NoopWriter()));

    assertTrue(adapter.findWarp("SPAWN").isPresent());
    assertTrue(adapter.findWarp("spawn").isPresent());
  }

  @Test
  void canUseHonorsPerWarpPermissionAndWildcard() {
    var cache = new WarpCache();
    cache.put(warp("Vip"));

    var adapter = new WarpsApiAdapter(new WarpService(null, cache, new NoopWriter()));

    assertFalse(adapter.canUse(new StubPermissible(Set.of()), "Vip"));
    assertTrue(adapter.canUse(new StubPermissible(Set.of("essentials.warp.use.vip")), "Vip"));
    assertTrue(adapter.canUse(new StubPermissible(Set.of("essentials.warp.use.*")), "Vip"));
  }

  @Test
  void visibleToFiltersWarpsTheViewerCannotUse() {
    var cache = new WarpCache();
    cache.put(warp("Public"));
    cache.put(warp("Vip"));

    var adapter = new WarpsApiAdapter(new WarpService(null, cache, new NoopWriter()));

    var viewer = new StubPermissible(Set.of("essentials.warp.use.public"));
    var visible = adapter.visibleTo(viewer).stream().map(Warp::name).toList();

    assertEquals(java.util.List.of("Public"), visible);
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
    public boolean isPermissionSet(@NonNull org.bukkit.permissions.Permission perm) {
      return isPermissionSet(perm.getName());
    }

    @Override
    public boolean hasPermission(@NonNull String name) {
      return this.nodes.contains(name);
    }

    @Override
    public boolean hasPermission(@NonNull org.bukkit.permissions.Permission perm) {
      return hasPermission(perm.getName());
    }

    @Override
    public org.bukkit.permissions.PermissionAttachment addAttachment(
        @NonNull org.bukkit.plugin.Plugin plugin, @NonNull String name, boolean value) {
      throw new UnsupportedOperationException();
    }

    @Override
    public org.bukkit.permissions.PermissionAttachment addAttachment(
        @NonNull org.bukkit.plugin.Plugin plugin) {
      throw new UnsupportedOperationException();
    }

    @Override
    public org.bukkit.permissions.PermissionAttachment addAttachment(
        @NonNull org.bukkit.plugin.Plugin plugin, @NonNull String name, boolean value, int ticks) {
      throw new UnsupportedOperationException();
    }

    @Override
    public org.bukkit.permissions.PermissionAttachment addAttachment(
        @NonNull org.bukkit.plugin.Plugin plugin, int ticks) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void removeAttachment(@NonNull org.bukkit.permissions.PermissionAttachment attachment) {}

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

  private static final class NoopWriter implements AsyncDatabaseWriter {
    @Override
    public CompletableFuture<Void> submit(@NonNull String operation, @NonNull Runnable work) {
      return CompletableFuture.completedFuture(null);
    }

    @Override
    public void close() {}
  }
}
