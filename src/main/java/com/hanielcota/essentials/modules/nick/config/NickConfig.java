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
        "<green>O seu apelido agora é <gold>{nick}</gold>.",
        "<green>Apelido de <gold>{player}</gold> definido como <gold>{nick}</gold>.",
        "<green>O seu apelido foi removido.",
        "<green>Apelido de <gold>{player}</gold> removido.",
        "<gray>O seu apelido foi alterado para <gold>{nick}</gold>.",
        "<gray>O seu apelido foi removido.",
        "<gold>{nick}</gold> <gray>é</gray> <gold>{player}</gold>.",
        "<red>Nenhum jogador usa o apelido <gold>{nick}</gold>.",
        "<red>Você não tem um apelido para remover.",
        "<red>O apelido deve ter entre <gold>{min}</gold> e <gold>{max}</gold> caracteres.",
        "<red>O apelido só pode conter letras, números e underscore.",
        "<red>Esse apelido já está em uso.");
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
