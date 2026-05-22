package com.hanielcota.essentials.modules.essentials.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record EssentialsConfig(
    @Comment("Placeholders: {count}.") String reloadSuccess,
    @Comment("Placeholders: {succeeded}, {total}, {failed}.") String reloadFailure,
    @Comment("Shown when /essentials is used without a subcommand.") String usage) {

  public static EssentialsConfig defaults() {
    return new EssentialsConfig(
        "<green>Reloaded <gold>{count}</gold> config(s).",
        "<red>Reloaded {succeeded}/{total}. Failed: <gold>{failed}</gold>.",
        "<yellow>Usage: <gray>/essentials reload</gray>.</yellow>");
  }

  public String formatSuccess(int count) {
    return reloadSuccess.replace("{count}", String.valueOf(count));
  }

  public String formatFailure(int succeeded, int total, String failed) {
    return reloadFailure
        .replace("{succeeded}", String.valueOf(succeeded))
        .replace("{total}", String.valueOf(total))
        .replace("{failed}", failed);
  }
}
