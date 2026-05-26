package com.hanielcota.essentials.modules.chat.config;

import lombok.NonNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record ChatMessages(
    @Comment("Shown after `/chat reload` succeeds. Placeholder: {count}.") String reloadSuccess,
    @Comment(
            "Shown when one or more configs fail to reload. Placeholders: {succeeded}, {total},"
                + " {failed}.")
        String reloadFailure,
    @Comment("Shown when /chat is run without a subcommand.") String usage,
    @Comment("Shown when the caller lacks the required permission.") String noPermission) {

  public static ChatMessages defaults() {
    return new ChatMessages(
        "<green>Reloaded <gold>{count}</gold> config(s).",
        "<red>Reloaded {succeeded}/{total}. Failed: <gold>{failed}</gold>.",
        "<yellow>Usage: <gray>/chat reload</gray>.",
        "<red>You do not have permission to use this command.");
  }

  public String formatReloadSuccess(int count) {
    var countStr = String.valueOf(count);

    return reloadSuccess.replace("{count}", countStr);
  }

  public String formatReloadFailure(int succeeded, int total, @NonNull String failed) {
    var succeededStr = String.valueOf(succeeded);
    var totalStr = String.valueOf(total);

    var withSucceeded = reloadFailure.replace("{succeeded}", succeededStr);
    var withTotal = withSucceeded.replace("{total}", totalStr);

    return withTotal.replace("{failed}", failed);
  }
}
