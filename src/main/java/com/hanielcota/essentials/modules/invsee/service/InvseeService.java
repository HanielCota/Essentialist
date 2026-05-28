package com.hanielcota.essentials.modules.invsee.service;

import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 * Thin facade for the /invsee feature: acquires the single-viewer lock via {@link InvseeLocks},
 * delegates GUI assembly to {@link InvseeViewBuilder} and the writeback to {@link InvseeWriteback}.
 *
 * <p>Inventory slot layout constants live in {@link InvseeLayout} so listeners can reason about
 * locked-slot ranges without depending on the service.
 */
@RequiredArgsConstructor
public final class InvseeService {

  private final @NonNull InvseeLocks locks;

  /**
   * Builds the /invsee GUI for {@code target}. Returns empty when another viewer already holds the
   * target — concurrent views with stale snapshots would corrupt the target on writeback. The
   * caller must call {@link #release(UUID, UUID)} when the view closes.
   */
  public Optional<Inventory> createView(
      @NonNull Player viewer, @NonNull Player target, @NonNull String title) {
    var targetId = target.getUniqueId();
    var viewerId = viewer.getUniqueId();

    // Any existing holder — even the same viewer — denies the new view. The previous view's
    // InventoryCloseEvent will fire `release(...)` for the old holder; allowing two open views
    // from the same viewer would let the old close handler free the lock while the new view is
    // still live and editing.
    var acquired = this.locks.tryAcquire(targetId, viewerId);
    if (!acquired) {
      return Optional.empty();
    }

    var view = InvseeViewBuilder.build(target, title, targetId);

    return Optional.of(view);
  }

  /** Releases the viewer lock on {@code targetId} if held by {@code viewerId}. */
  public void release(@NonNull UUID targetId, @NonNull UUID viewerId) {
    this.locks.release(targetId, viewerId);
  }

  /** Releases the viewer lock on {@code targetId} unconditionally (target quit/died). */
  public void releaseTarget(@NonNull UUID targetId) {
    this.locks.releaseTarget(targetId);
  }

  /** Writes the editable slots of {@code view} back into {@code target}'s inventory. */
  public void sync(@NonNull Player target, @NonNull Inventory view) {
    InvseeWriteback.apply(target, view);
  }
}
