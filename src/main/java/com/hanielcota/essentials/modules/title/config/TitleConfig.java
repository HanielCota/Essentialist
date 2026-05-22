package com.hanielcota.essentials.modules.title.config;

import com.hanielcota.essentials.config.MessagePair;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record TitleConfig(
    @Comment("Title fade-in time in ticks (20 ticks = 1 second).") int fadeInTicks,
    @Comment("Title stay time in ticks (20 ticks = 1 second).") int stayTicks,
    @Comment("Title fade-out time in ticks (20 ticks = 1 second).") int fadeOutTicks,
    @Comment("Shown to the sender after sending a title to themselves.") String sent,
    @Comment("Shown to the sender after sending a title to another player. Placeholders: {player}.")
        String sentOther,
    @Comment("Shown to the sender after /title broadcast. Placeholders: {count}.")
        String broadcasted,
    @Comment("Shown when targeting another player without essentials.title.others.")
        String noPermissionOther,
    @Comment("Shown when /title is used without any text.") String usage) {

  public static TitleConfig defaults() {
    return new TitleConfig(
        10,
        70,
        20,
        "<green>Título enviado.",
        "<green>Título enviado para <gold>{player}</gold>.",
        "<green>Título enviado para <gold>{count}</gold> jogador(es).",
        "<red>Você não tem permissão para enviar títulos a outros jogadores.",
        """
        <yellow>Uso: <gray>/title [jogador] "título" "subtítulo"</gray> — o subtítulo é opcional.\
        """);
  }

  public MessagePair whenSent() {
    return new MessagePair(sent, sentOther);
  }

  public String formatBroadcasted(int count) {
    var countStr = Integer.toString(count);
    return broadcasted.replace("{count}", countStr);
  }
}
