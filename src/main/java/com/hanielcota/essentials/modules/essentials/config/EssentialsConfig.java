package com.hanielcota.essentials.modules.essentials.config;

import lombok.NonNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record EssentialsConfig(
    @Comment("Placeholders: {count}.") String reloadSuccess,
    @Comment("Placeholders: {succeeded}, {total}, {failed}.") String reloadFailure,
    @Comment("Shown when console runs /essentials without a subcommand.") String usage,
    @Comment("The module-control menu opened by /essentials (in-game).") ModulesMenuConfig menu) {

  public static EssentialsConfig defaults() {
    return new EssentialsConfig(
        "<green>Reloaded <gold>{count}</gold> config(s).",
        "<red>Reloaded {succeeded}/{total}. Failed: <gold>{failed}</gold>.",
        "<yellow>Usage: <gray>/essentials reload</gray>.</yellow>",
        ModulesMenuConfig.defaults());
  }

  public String formatSuccess(int count) {
    var countStr = String.valueOf(count);

    return reloadSuccess.replace("{count}", countStr);
  }

  public String formatFailure(int succeeded, int total, @NonNull String failed) {
    var succeededStr = String.valueOf(succeeded);
    var totalStr = String.valueOf(total);

    var withSucceeded = reloadFailure.replace("{succeeded}", succeededStr);
    var withTotal = withSucceeded.replace("{total}", totalStr);

    return withTotal.replace("{failed}", failed);
  }
}
