package com.hanielcota.essentials.modules.tpa.config;

import com.hanielcota.essentials.modules.tpa.model.TeleportRequestType;
import lombok.NonNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/**
 * Every chat line the {@code /tpa} request flow can send.
 *
 * <p>Templates are exposed raw; callers fill {@code {player}} (and any other placeholders) at the
 * call site with {@link String#replace(CharSequence, CharSequence)}.
 */
@ConfigSerializable
public record TpaMessages(
    @Comment("/tpa sender confirmation. Placeholders: {player}.") String requestSent,
    @Comment("/tpahere sender confirmation. Placeholders: {player}.") String requestSentHere,
    @Comment("Shown when a player targets themselves.") String selfTarget,
    @Comment("/tpacancel with no pending request.") String noOutgoing,
    @Comment("/tpacancel confirmation. Placeholders: {player}.") String cancelled,
    @Comment("Sent to the requester when a request expires. Placeholders: {player}.")
        String expired,
    @Comment("Sent to the requester when the target accepts. Placeholders: {player}.")
        String accepted,
    @Comment("Sent to the requester when the target denies. Placeholders: {player}.") String denied,
    @Comment("Shown when accepting but the requester has gone offline. Placeholders: {player}.")
        String requesterOffline,
    @Comment("Shown when the teleport itself fails.") String teleportFailed,
    @Comment("/tpa request line shown to the target. Placeholders: {player}, {seconds}.")
        String requestReceived,
    @Comment("/tpahere request line shown to the target. Placeholders: {player}, {seconds}.")
        String requestReceivedHere,
    @Comment("Clickable accept button label.") String buttonAccept,
    @Comment("Clickable deny button label.") String buttonDeny,
    @Comment("Accept button hover tooltip. Placeholders: {player}.") String buttonHoverAccept,
    @Comment("Deny button hover tooltip. Placeholders: {player}.") String buttonHoverDeny,
    @Comment("/tpaccept or /tpdeny with no pending request.") String noIncoming,
    @Comment("Shown when several requests are pending and none was named.") String ambiguous,
    @Comment("Sent to the accepting player. Placeholders: {player}.") String acceptedSelf,
    @Comment("Sent to the denying player. Placeholders: {player}.") String deniedSelf,
    @Comment(
            "Shown to the other party when a request dies because someone disconnected. "
                + "Placeholders: {player}.")
        String partnerLeft,
    @Comment("/tpahistory when the player has no history yet.") String noHistory,
    @Comment("/tpahistory <jogador> when the target has no history. Placeholders: {player}.")
        String noHistoryOther,
    @Comment("/tpahistory <jogador> without the .others permission.") String noPermissionOther,
    @Comment("/tpahistory <jogador> when the target is unknown. Placeholders: {player}.")
        String playerNotFound,
    @Comment(
            "/tpahistory <jogador> confirmation sent before opening the menu. "
                + "Placeholders: {player}.")
        String viewingOther) {

  public static TpaMessages defaults() {
    return new TpaMessages(
        "<green>Pedido de teleporte enviado para <gold>{player}</gold>.",
        "<green>Pedido enviado — <gold>{player}</gold> teleportará até você.",
        "<red>Você não pode enviar um pedido de teleporte para si mesmo.",
        "<red>Você não tem nenhum pedido pendente para cancelar.",
        "<yellow>Pedido de teleporte para <gold>{player}</gold> cancelado.",
        "<red>Seu pedido de teleporte para <gold>{player}</gold> expirou.",
        "<green><gold>{player}</gold> aceitou seu pedido de teleporte.",
        "<red><gold>{player}</gold> recusou seu pedido de teleporte.",
        "<red><gold>{player}</gold> está offline — o pedido foi cancelado.",
        "<red>O teleporte não pôde ser concluído.",
        "<gold>{player}</gold> <yellow>quer se teleportar até você. <gray>(expira em {seconds}s)",
        "<gold>{player}</gold> <yellow>quer que você se teleporte até ele. "
            + "<gray>(expira em {seconds}s)",
        "<green><bold>[ACEITAR]</bold>",
        "<red><bold>[RECUSAR]</bold>",
        "<green>Clique para aceitar o pedido de <gold>{player}</gold>.",
        "<red>Clique para recusar o pedido de <gold>{player}</gold>.",
        "<red>Você não tem nenhum pedido de teleporte pendente.",
        "<red>Você tem vários pedidos. Use <gold>/tpaccept <jogador></gold> ou "
            + "<gold>/tpdeny <jogador></gold>.",
        "<green>Você aceitou o pedido de <gold>{player}</gold>.",
        "<yellow>Você recusou o pedido de <gold>{player}</gold>.",
        "<red>O pedido de teleporte foi cancelado — <gold>{player}</gold> saiu do servidor.",
        "<red>Você ainda não enviou nenhum pedido de teleporte.",
        "<red><gold>{player}</gold> ainda não enviou nenhum pedido de teleporte.",
        "<red>Você não tem permissão para ver o histórico de outros jogadores.",
        "<red>O jogador <gold>{player}</gold> nunca entrou neste servidor.",
        "<gray>Mostrando o histórico de teleportes de <gold>{player}</gold>.");
  }

  /** The request line shown to the target, picked by {@code type}. */
  public String formatRequestReceived(
      @NonNull TeleportRequestType type, @NonNull String player, long seconds) {
    var line = type == TeleportRequestType.TPAHERE ? requestReceivedHere : requestReceived;
    return line.replace("{player}", player).replace("{seconds}", Long.toString(seconds));
  }
}
