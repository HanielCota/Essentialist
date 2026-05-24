package com.hanielcota.essentials.modules.homes.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.Test;

class HomeLimitResolverTest {

  @Test
  void readsFallbackLimitEachTimeSoConfigReloadsApply() {
    var fallback = new AtomicInteger(1);
    var resolver = new HomeLimitResolver(fallback::get);
    var player = new FakePermissible();

    assertEquals(1, resolver.resolve(player));

    fallback.set(4);

    assertEquals(4, resolver.resolve(player));
  }

  @Test
  void highestPositivePermissionLimitWinsOverFallback() {
    var resolver = new HomeLimitResolver(() -> 1);
    var player =
        new FakePermissible(
            "essentials.home.limit.2",
            "essentials.home.limit.not-a-number",
            "essentials.home.limit.5");

    assertEquals(5, resolver.resolve(player));
  }

  private static final class FakePermissible implements Permissible {

    private final Set<PermissionAttachmentInfo> permissions = new LinkedHashSet<>();

    private FakePermissible(String... nodes) {
      for (var node : nodes) {
        permissions.add(new PermissionAttachmentInfo(this, node, null, true));
      }
    }

    @Override
    public boolean isPermissionSet(String name) {
      return false;
    }

    @Override
    public boolean isPermissionSet(Permission perm) {
      return false;
    }

    @Override
    public boolean hasPermission(String name) {
      return false;
    }

    @Override
    public boolean hasPermission(Permission perm) {
      return false;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
      throw new UnsupportedOperationException();
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
      throw new UnsupportedOperationException();
    }

    @Override
    public PermissionAttachment addAttachment(
        Plugin plugin, String name, boolean value, int ticks) {
      throw new UnsupportedOperationException();
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void removeAttachment(PermissionAttachment attachment) {}

    @Override
    public void recalculatePermissions() {}

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
      return permissions;
    }

    @Override
    public boolean isOp() {
      return false;
    }

    @Override
    public void setOp(boolean value) {}
  }
}
