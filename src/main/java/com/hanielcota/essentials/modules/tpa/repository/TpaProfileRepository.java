package com.hanielcota.essentials.modules.tpa.repository;

import com.hanielcota.essentials.database.SqlExecutor;
import com.hanielcota.essentials.modules.tpa.domain.TpaProfile;
import com.hanielcota.essentials.modules.tpa.service.TpaProfileService;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class TpaProfileRepository {

  private final SqlExecutor sqlExecutor;
  private final TpaProfileTable table;

  private static TpaProfileService.Entry readRow(@NonNull ResultSet rs) throws SQLException {
    var playerIdRaw = rs.getString("player_id");
    var playerId = UUID.fromString(playerIdRaw);

    var receiveTpa = rs.getInt("receive_tpa") != 0;
    var receiveTpaHere = rs.getInt("receive_tpahere") != 0;
    var sentRequests = rs.getLong("sent_requests");
    var receivedRequests = rs.getLong("received_requests");

    var profile = new TpaProfile(receiveTpa, receiveTpaHere, sentRequests, receivedRequests);

    return new TpaProfileService.Entry(playerId, profile);
  }

  public List<TpaProfileService.Entry> listAll() {
    return this.sqlExecutor.query(TpaProfileTable.SELECT_ALL, TpaProfileRepository::readRow);
  }

  public void save(@NonNull UUID playerId, @NonNull TpaProfile profile) {
    var playerIdRaw = playerId.toString();
    var receiveTpa = profile.receiveTpa() ? 1 : 0;
    var receiveTpaHere = profile.receiveTpaHere() ? 1 : 0;
    var sentRequests = profile.sentRequests();
    var receivedRequests = profile.receivedRequests();
    var updatedAt = Instant.now().toEpochMilli();

    this.sqlExecutor.update(
        this.table.upsert(),
        playerIdRaw,
        receiveTpa,
        receiveTpaHere,
        sentRequests,
        receivedRequests,
        updatedAt);
  }
}
