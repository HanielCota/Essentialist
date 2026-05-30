package com.hanielcota.essentials.modules.ban.menu;

import java.util.UUID;
import lombok.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * The in-progress ban a staff member is composing in the options menu: who is targeted, the picked
 * duration (raw string parsed later; blank = permanent) and the picked reason ({@code null} until
 * one is chosen). Duration defaults to permanent so only a reason is mandatory before confirming.
 */
public record BanSelection(
    @NonNull UUID targetId,
    @NonNull String targetName,
    @NonNull String durationRaw,
    @NonNull String durationLabel,
    @Nullable String reason) {

  public BanSelection withDuration(@NonNull String raw, @NonNull String label) {
    return new BanSelection(this.targetId, this.targetName, raw, label, this.reason);
  }

  public BanSelection withReason(@NonNull String pickedReason) {
    return new BanSelection(
        this.targetId, this.targetName, this.durationRaw, this.durationLabel, pickedReason);
  }

  public boolean isPermanent() {
    return this.durationRaw.isBlank();
  }
}
