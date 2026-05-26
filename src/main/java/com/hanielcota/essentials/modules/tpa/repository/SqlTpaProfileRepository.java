package com.hanielcota.essentials.modules.tpa.repository;

import com.hanielcota.essentials.database.SqlExecutor;
import com.hanielcota.essentials.modules.tpa.domain.FavoriteOrdering;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequestType;
import com.hanielcota.essentials.modules.tpa.domain.TpaProfile;
import com.hanielcota.essentials.modules.tpa.service.TpaProfileService;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class SqlTpaProfileRepository implements TpaProfileRepository {

  private final SqlExecutor sqlExecutor;
  private final TpaProfileTable table;

  private static TpaProfileService.Entry readRow(@NonNull ResultSet rs) throws SQLException {
    var playerIdRaw = rs.getString("player_id");
    var playerId = UUID.fromString(playerIdRaw);

    var receiveByType = new EnumMap<TeleportRequestType, Boolean>(TeleportRequestType.class);
    receiveByType.put(TeleportRequestType.TPA, rs.getInt("receive_tpa") != 0);
    receiveByType.put(TeleportRequestType.TPAHERE, rs.getInt("receive_tpahere") != 0);

    var sentRequests = rs.getLong("sent_requests");
    var receivedRequests = rs.getLong("received_requests");
    var acceptedSent = rs.getLong("accepted_sent");
    var acceptCount = rs.getLong("accept_count");
    var totalAcceptLatencyMs = rs.getLong("total_accept_latency_ms");
    var autoAcceptFavorites = rs.getInt("auto_accept_favorites") != 0;
    var soundsEnabled = rs.getInt("sounds_enabled") != 0;
    var allowCrossWorld = rs.getInt("allow_cross_world") != 0;
    var notifyWhenFavorited = rs.getInt("notify_when_favorited") != 0;
    var dndUntilMs = rs.getLong("dnd_until_ms");
    var favoriteOrdering = parseOrdering(rs.getString("favorite_ordering"));

    var profile =
        new TpaProfile(
            Map.copyOf(receiveByType),
            sentRequests,
            receivedRequests,
            acceptedSent,
            acceptCount,
            totalAcceptLatencyMs,
            autoAcceptFavorites,
            soundsEnabled,
            allowCrossWorld,
            notifyWhenFavorited,
            dndUntilMs,
            favoriteOrdering);

    return new TpaProfileService.Entry(playerId, profile);
  }

  private static FavoriteOrdering parseOrdering(String raw) {
    if (raw == null || raw.isBlank()) {
      return FavoriteOrdering.NAME;
    }
    try {
      return FavoriteOrdering.valueOf(raw);
    } catch (IllegalArgumentException _) {
      return FavoriteOrdering.NAME;
    }
  }

  public List<TpaProfileService.Entry> listAll() {
    return this.sqlExecutor.query(TpaProfileTable.SELECT_ALL, SqlTpaProfileRepository::readRow);
  }

  public void save(@NonNull UUID playerId, @NonNull TpaProfile profile) {
    var playerIdRaw = playerId.toString();
    var receiveByType = profile.receiveByType();
    var receiveTpa = mapReceive(receiveByType, TeleportRequestType.TPA);
    var receiveTpaHere = mapReceive(receiveByType, TeleportRequestType.TPAHERE);
    var sentRequests = profile.sentRequests();
    var receivedRequests = profile.receivedRequests();
    var acceptedSent = profile.acceptedSent();
    var acceptCount = profile.acceptCount();
    var totalAcceptLatencyMs = profile.totalAcceptLatencyMs();
    var autoAcceptFavorites = profile.autoAcceptFavorites() ? 1 : 0;
    var soundsEnabled = profile.soundsEnabled() ? 1 : 0;
    var allowCrossWorld = profile.allowCrossWorld() ? 1 : 0;
    var notifyWhenFavorited = profile.notifyWhenFavorited() ? 1 : 0;
    var dndUntilMs = profile.dndUntilEpochMs();
    var favoriteOrdering = profile.favoriteOrdering().name();
    var updatedAt = Instant.now().toEpochMilli();

    this.sqlExecutor.update(
        this.table.upsert(),
        playerIdRaw,
        receiveTpa,
        receiveTpaHere,
        sentRequests,
        receivedRequests,
        acceptedSent,
        acceptCount,
        totalAcceptLatencyMs,
        autoAcceptFavorites,
        soundsEnabled,
        allowCrossWorld,
        notifyWhenFavorited,
        dndUntilMs,
        favoriteOrdering,
        updatedAt);
  }

  private static int mapReceive(
      @NonNull Map<TeleportRequestType, Boolean> map, @NonNull TeleportRequestType type) {
    return map.getOrDefault(type, false) ? 1 : 0;
  }
}
