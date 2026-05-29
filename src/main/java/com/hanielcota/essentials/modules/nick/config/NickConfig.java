package com.hanielcota.essentials.modules.nick.config;

import com.hanielcota.essentials.shared.Placeholders;
import lombok.NonNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record NickConfig(
    @Comment("Minimum nickname length.") int minLength,
    @Comment("Maximum nickname length.") int maxLength,
    @Comment("Shown to the caller after setting their own nick. Placeholder: {nick}.")
        String nickSetSelf,
    @Comment(
            "Shown to the caller after setting another player's nick. Placeholders: {player},"
                + " {nick}.")
        String nickSetOther,
    @Comment("Shown to the caller after removing their own nick.") String nickResetSelf,
    @Comment("Shown to the caller after removing another player's nick. Placeholder: {player}.")
        String nickResetOther,
    @Comment("Shown to the target when an operator changes their nick. Placeholder: {nick}.")
        String nickSetByOther,
    @Comment("Shown to the target when an operator removes their nick.") String nickResetByOther,
    @Comment("Shown to the caller after a successful /realname. Placeholders: {nick}, {player}.")
        String realNameOf,
    @Comment("Shown when /realname can't find a player. Placeholder: {nick}.") String unknownNick,
    @Comment("Shown when /nick already has nothing to reset.") String alreadyHasNoNick,
    @Comment("Length error. Placeholders: {min}, {max}.") String invalidLength,
    @Comment("Charset error.") String invalidChars,
    @Comment("Shown when the nickname is already taken by someone else.") String nickTaken) {

  public static NickConfig defaults() {
    return new NickConfig(
        3,
        16,
        "<green>Your nickname is now <gold>{nick}</gold>.",
        "<green>Nickname of <gold>{player}</gold> set to <gold>{nick}</gold>.",
        "<green>Your nickname has been removed.",
        "<green>Nickname of <gold>{player}</gold> removed.",
        "<gray>Your nickname has been changed to <gold>{nick}</gold>.",
        "<gray>Your nickname has been removed.",
        "<gold>{nick}</gold> <gray>is</gray> <gold>{player}</gold>.",
        "<red>No player is using the nickname <gold>{nick}</gold>.",
        "<red>You don't have a nickname to remove.",
        "<red>The nickname must be between <gold>{min}</gold> and <gold>{max}</gold> characters.",
        "<red>The nickname can only contain letters, numbers and underscores.",
        "<red>That nickname is already in use.");
  }

  public String formatNickSetSelf(@NonNull String nick) {
    return nickSetSelf.replace("{nick}", nick);
  }

  public String formatNickSetOther(@NonNull String player, @NonNull String nick) {
    return Placeholders.format(nickSetOther, "player", player, "nick", nick);
  }

  public String formatNickResetOther(@NonNull String player) {
    return nickResetOther.replace("{player}", player);
  }

  public String formatNickSetByOther(@NonNull String nick) {
    return nickSetByOther.replace("{nick}", nick);
  }

  public String formatRealNameOf(@NonNull String nick, @NonNull String player) {
    return Placeholders.format(realNameOf, "nick", nick, "player", player);
  }

  public String formatUnknownNick(@NonNull String nick) {
    return unknownNick.replace("{nick}", nick);
  }

  public String formatInvalidLength() {
    var minStr = Integer.toString(minLength);
    var maxStr = Integer.toString(maxLength);

    return Placeholders.format(invalidLength, "min", minStr, "max", maxStr);
  }
}
