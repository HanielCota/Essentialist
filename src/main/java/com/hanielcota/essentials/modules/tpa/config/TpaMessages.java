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
    @Comment(
            "Shown to the old target when the requester sends a new TPA to someone else. "
                + "Placeholders: {player}.")
        String requestReplaced,
    @Comment("/tpahistory when the player has no history yet.") String noHistory,
    @Comment("/tpahistory <player> when the target has no history. Placeholders: {player}.")
        String noHistoryOther,
    @Comment("/tpahistory <player> without the .others permission.") String noPermissionOther,
    @Comment("/tpahistory <player> when the target is unknown. Placeholders: {player}.")
        String playerNotFound,
    @Comment(
            "/tpahistory <player> confirmation sent before opening the menu. "
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
    @Comment("Sent after the player clicks deny-all in the pending menu. Placeholder: {count}.")
        String deniedAllMessage,
    @Comment(
            "Shown when accept-all skips TPA requests because a TPAHERE has priority. "
                + "Placeholder: {count}.")
        String tpaHerePriorityMessage,
    @Comment(
            "Shown when deny-all skips some requests that were already processed. "
                + "Placeholder: {count}.")
        String alreadyProcessedMessage,
    @Comment(
            "Sent to the requester when their outgoing request is replaced by a new one. "
                + "Placeholders: {oldTarget}, {newTarget}.")
        String outgoingReplaced) {

  public static TpaMessages defaults() {
    return new TpaMessages(
        "<green>Request sent to teleport to <gold>{player}</gold>.",
        "<green>Request sent for <gold>{player}</gold> to teleport to you.",
        "<red>You cannot send a teleport request to yourself.",
        "<red>You have no pending request to cancel.",
        "<yellow>Teleport request to <gold>{player}</gold> cancelled.",
        "<red>Your teleport request to <gold>{player}</gold> has expired.",
        "<green><gold>{player}</gold> accepted your teleport request.",
        "<red><gold>{player}</gold> denied your teleport request.",
        "<red><gold>{player}</gold> is offline — the request was cancelled.",
        "<red><gold>{player}</gold> is no longer online — the request was cancelled.",
        "<red>The teleport could not be completed.",
        "<gold>{player}</gold> <yellow>wants to teleport to you. <gray>(expires in {seconds}s)",
        "<gold>{player}</gold> <yellow>wants you to teleport to them. "
            + "<gray>(expires in {seconds}s)",
        "<light_purple>★</light_purple> <gold>{player}</gold> <yellow>(your favorite) "
            + "<yellow>wants to teleport to you. <gray>(expires in {seconds}s)",
        "<light_purple>★</light_purple> <gold>{player}</gold> <yellow>(your favorite) "
            + "<yellow>wants you to teleport to them. <gray>(expires in {seconds}s)",
        "<green><bold>[ACCEPT]</bold>",
        "<red><bold>[DENY]</bold>",
        "<green>Click to accept the request from <gold>{player}</gold>.",
        "<red>Click to deny the request from <gold>{player}</gold>.",
        "<red>You have no pending teleport request.",
        "<red>You have several requests. Use <gold>/tpaccept <player></gold> to accept "
            + "or <gold>/tpdeny <player></gold> to deny.",
        "<green>You accepted the request from <gold>{player}</gold>.",
        "<yellow>You denied the request from <gold>{player}</gold>.",
        "<red>The teleport request was cancelled — <gold>{player}</gold> left the server.",
        "<red>The teleport request was cancelled — <gold>{player}</gold> sent a new request.",
        "<red>You have not sent any teleport request yet.",
        "<red><gold>{player}</gold> has not sent any teleport request yet.",
        "<red>You do not have permission to view other players' history.",
        "<red>The player <gold>{player}</gold> has never joined this server.",
        "<gray>Showing the teleport history of <gold>{player}</gold>.",
        "<red><gold>{player}</gold> does not allow other players to teleport to them.",
        "<red><gold>{player}</gold> does not allow being summoned to other players.",
        "<red><gold>{player}</gold> is not receiving requests from you.",
        "<green>You blocked TPA requests from <gold>{player}</gold>.",
        "<green>You unblocked TPA requests from <gold>{player}</gold>.",
        "<red>You cannot block TPA requests from yourself.",
        "<yellow>Type in chat the nick of the player you want to add to your favorites. "
            + "<gray>(<white>{seconds}s</white>, type <white>cancel</white> to abort)",
        "<green>You added <gold>{player}</gold> to your favorites.",
        "<yellow>You removed <gold>{player}</gold> from your favorites.",
        "<red><gold>{player}</gold> is already in your favorites.",
        "<red>Invalid nick. Use only letters, numbers and _.",
        "<red>You did not type anything in time — adding favorite cancelled.",
        "<yellow>Adding favorite cancelled.",
        "<red>You cannot add yourself as a favorite.",
        "<red>The player <gold>{player}</gold> has never joined this server.",
        "<red><gold>{player}</gold> is not online right now.",
        "<red><gold>{player}</gold> is in Do-Not-Disturb mode and is not receiving requests.",
        "<gold>{player}</gold> added you to their favorites.",
        "<red><gold>{player}</gold> is not accepting requests from other worlds.",
        "<green>You accepted <white>{count}</white> request(s).",
        "<yellow>You denied <white>{count}</white> request(s).",
        "<yellow>/tpa requests skipped — a /tpahere request has priority. "
            + "(<white>{count}</white> skipped)",
        "<gray>Some requests were already processed and could not be denied. "
            + "(<white>{count}</white> request(s))",
        "<yellow>Your request to <gold>{oldTarget}</gold> was replaced by a new one to "
            + "<gold>{newTarget}</gold>.</yellow>");
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
