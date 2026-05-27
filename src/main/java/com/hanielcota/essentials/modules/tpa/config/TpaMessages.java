package com.hanielcota.essentials.modules.tpa.config;

import com.hanielcota.essentials.modules.tpa.domain.TeleportRequestType;
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
    @Comment(
            "Shown when accepting but the target (accepter) is no longer online. Placeholders:"
                + " {player}.")
        String targetOffline,
    @Comment("Shown when the teleport itself fails.") String teleportFailed,
    @Comment("/tpa request line shown to the target. Placeholders: {player}, {seconds}.")
        String requestReceived,
    @Comment("/tpahere request line shown to the target. Placeholders: {player}, {seconds}.")
        String requestReceivedHere,
    @Comment(
            "/tpa request line shown to the target when the requester is in their favorites. "
                + "Placeholders: {player}, {seconds}.")
        String requestReceivedFavorite,
    @Comment(
            "/tpahere request line shown to the target when the requester is in their favorites. "
                + "Placeholders: {player}, {seconds}.")
        String requestReceivedFavoriteHere,
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
        String viewingOther,
    @Comment("/tpa blocked because target disabled incoming /tpa. Placeholders: {player}.")
        String tpaDisabled,
    @Comment("/tpahere blocked because target disabled incoming /tpahere. Placeholders: {player}.")
        String tpaHereDisabled,
    @Comment("Shown when the target blocked this requester. Placeholders: {player}.")
        String blockedByPlayer,
    @Comment("/tpablock confirmation. Placeholders: {player}.") String blockedPlayer,
    @Comment("/tpaunblock confirmation. Placeholders: {player}.") String unblockedPlayer,
    @Comment("Shown when a player tries to block themselves.") String blockSelf,
    @Comment("Prompt sent when the player clicks the add-favorite button. Placeholders: {seconds}.")
        String favoritePrompt,
    @Comment("Favorite added confirmation. Placeholders: {player}.") String favoriteAdded,
    @Comment("Favorite removed confirmation. Placeholders: {player}.") String favoriteRemoved,
    @Comment(
            "Shown when the player tried to add a favorite they already had. Placeholders:"
                + " {player}.")
        String favoriteAlready,
    @Comment("Shown when the typed name does not look like a valid Minecraft nickname.")
        String favoriteInvalidName,
    @Comment("Shown when the favorite-add prompt times out.") String favoritePromptTimeout,
    @Comment("Shown when the player cancels the favorite-add prompt.")
        String favoritePromptCancelled,
    @Comment("Shown when a player tries to favorite themselves.") String favoriteSelf,
    @Comment(
            "Shown when the typed name does not match any known online or offline player. "
                + "Placeholders: {player}.")
        String favoriteUnknownPlayer,
    @Comment("Shown when the chosen favorite is not online right now. Placeholders: {player}.")
        String favoriteOffline,
    @Comment("Shown when the target is in Do-Not-Disturb mode. Placeholders: {player}.")
        String dndActive,
    @Comment(
            "Sent to a player when another player adds them to their favorites. Gated by the "
                + "target's notifyWhenFavorited toggle. Placeholders: {player}.")
        String favoriteNotifyTarget,
    @Comment(
            "Shown when the target refuses TPA requests from other worlds. Placeholders: {player}.")
        String crossWorldRefused,
    @Comment("Sent after the player clicks accept-all in the pending menu. Placeholder: {count}.")
        String acceptedAllMessage,
    @Comment(
            "Sent after accept-all when some requests were left pending (extra TPAHERE requests "
                + "the viewer cannot accept in bulk). Placeholder: {count}.")
        String acceptedAllSkippedMessage,
    @Comment("Sent after the player clicks deny-all in the pending menu. Placeholder: {count}.")
        String deniedAllMessage,
    @Comment(
            "Sent to the target when the requester is in their favorites and auto-accept fired. "
                + "Placeholders: {player}.")
        String autoAcceptedNotice,
    @Comment(
            "Sent to the target of an outgoing request when the requester switched their target "
                + "to someone else. Placeholders: {player}.")
        String requesterSwitchedTarget,
    @Comment(
            "Sent to the target of an outgoing request when the requester cancelled it from the "
                + "hub menu. Placeholders: {player}.")
        String cancelledByRequester) {

  public static TpaMessages defaults() {
    return new TpaMessages(
        "<green>Pedido enviado para ir até <gold>{player}</gold>.",
        "<green>Pedido enviado para chamar <gold>{player}</gold> até você.",
        "<red>Você não pode enviar um pedido de teleporte para si mesmo.",
        "<red>Você não tem nenhum pedido pendente para cancelar.",
        "<yellow>Pedido de teleporte para <gold>{player}</gold> cancelado.",
        "<red>Seu pedido de teleporte para <gold>{player}</gold> expirou.",
        "<green><gold>{player}</gold> aceitou seu pedido de teleporte.",
        "<red><gold>{player}</gold> recusou seu pedido de teleporte.",
        "<red><gold>{player}</gold> está offline — o pedido foi cancelado.",
        "<red><gold>{player}</gold> não está mais online — o pedido foi cancelado.",
        "<red>O teleporte não pôde ser concluído.",
        "<gold>{player}</gold> <yellow>quer se teleportar até você. <gray>(expira em {seconds}s)",
        "<gold>{player}</gold> <yellow>quer que você se teleporte até ele. "
            + "<gray>(expira em {seconds}s)",
        "<light_purple>★</light_purple> <gold>{player}</gold> <yellow>(seu favorito) "
            + "<yellow>quer se teleportar até você. <gray>(expira em {seconds}s)",
        "<light_purple>★</light_purple> <gold>{player}</gold> <yellow>(seu favorito) "
            + "<yellow>quer que você se teleporte até ele. <gray>(expira em {seconds}s)",
        "<green><bold>[ACEITAR]</bold>",
        "<red><bold>[RECUSAR]</bold>",
        "<green>Clique para aceitar o pedido de <gold>{player}</gold>.",
        "<red>Clique para recusar o pedido de <gold>{player}</gold>.",
        "<red>Você não tem nenhum pedido de teleporte pendente.",
        "<red>Você tem vários pedidos. Use <gold>/tpaccept <jogador></gold> para aceitar "
            + "ou <gold>/tpdeny <jogador></gold> para recusar.",
        "<green>Você aceitou o pedido de <gold>{player}</gold>.",
        "<yellow>Você recusou o pedido de <gold>{player}</gold>.",
        "<red>O pedido de teleporte foi cancelado — <gold>{player}</gold> saiu do servidor.",
        "<red>Você ainda não enviou nenhum pedido de teleporte.",
        "<red><gold>{player}</gold> ainda não enviou nenhum pedido de teleporte.",
        "<red>Você não tem permissão para ver o histórico de outros jogadores.",
        "<red>O jogador <gold>{player}</gold> nunca entrou neste servidor.",
        "<gray>Mostrando o histórico de teleportes de <gold>{player}</gold>.",
        "<red><gold>{player}</gold> não permite que outros jogadores teleportem até ele.",
        "<red><gold>{player}</gold> não permite ser chamado até outros jogadores.",
        "<red><gold>{player}</gold> não está recebendo pedidos seus.",
        "<green>Você bloqueou pedidos de TPA de <gold>{player}</gold>.",
        "<green>Você desbloqueou pedidos de TPA de <gold>{player}</gold>.",
        "<red>Você não pode bloquear pedidos de TPA de si mesmo.",
        "<yellow>Digite no chat o nick do jogador que deseja adicionar aos favoritos. "
            + "<gray>(<white>{seconds}s</white>, digite <white>cancelar</white> para abortar)",
        "<green>Você adicionou <gold>{player}</gold> aos favoritos.",
        "<yellow>Você removeu <gold>{player}</gold> dos favoritos.",
        "<red><gold>{player}</gold> já está nos seus favoritos.",
        "<red>Nick inválido. Use apenas letras, números e _.",
        "<red>Você não digitou nada a tempo — adição de favorito cancelada.",
        "<yellow>Adição de favorito cancelada.",
        "<red>Você não pode adicionar a si mesmo como favorito.",
        "<red>O jogador <gold>{player}</gold> nunca entrou neste servidor.",
        "<red><gold>{player}</gold> não está online no momento.",
        "<red><gold>{player}</gold> está no modo Não-Perturbe e não está recebendo pedidos.",
        "<gold>{player}</gold> te adicionou aos favoritos.",
        "<red><gold>{player}</gold> não está aceitando pedidos vindos de outros mundos.",
        "<green>Você aceitou <white>{count}</white> pedido(s).",
        "<yellow><white>{count}</white> pedido(s) de teleporte ficaram pendentes — abra o menu para"
            + " resolvê-los.",
        "<yellow>Você recusou <white>{count}</white> pedido(s).",
        "<gold>{player}</gold> <green>foi auto-aceito (está nos seus favoritos) — teleportando…",
        "<yellow><gold>{player}</gold> trocou o destino do pedido de teleporte.",
        "<yellow><gold>{player}</gold> cancelou o pedido de teleporte.");
  }

  /**
   * The request line shown to the target, picked by {@code type} and whether {@code requester} is
   * in the target's favorites.
   */
  public String formatRequestReceived(
      @NonNull TeleportRequestType type, @NonNull String player, long seconds, boolean favorite) {
    var line = pickRequestLine(type, favorite);

    var secondsStr = Long.toString(seconds);

    var withPlayer = line.replace("{player}", player);
    return withPlayer.replace("{seconds}", secondsStr);
  }

  private String pickRequestLine(@NonNull TeleportRequestType type, boolean favorite) {
    var isHere = type == TeleportRequestType.TPAHERE;
    if (favorite) {
      return isHere ? this.requestReceivedFavoriteHere : this.requestReceivedFavorite;
    }
    return isHere ? this.requestReceivedHere : this.requestReceived;
  }

  /** The "disabled" message for the given {@code type}, shown when the target refuses that kind. */
  public String disabledFor(@NonNull TeleportRequestType type) {
    var isHere = type == TeleportRequestType.TPAHERE;
    return isHere ? this.tpaHereDisabled : this.tpaDisabled;
  }
}
