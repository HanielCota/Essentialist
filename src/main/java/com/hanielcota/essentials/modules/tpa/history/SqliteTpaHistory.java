package com.hanielcota.essentials.modules.tpa.history;

import com.hanielcota.essentials.database.SqlExecutor;
import com.hanielcota.essentials.modules.tpa.model.Destination;
import com.hanielcota.essentials.modules.tpa.model.Participant;
import com.hanielcota.essentials.modules.tpa.model.TeleportRequestStatus;
import com.hanielcota.essentials.modules.tpa.model.TeleportRequestType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

/** SQLite-backed implementation of {@link TpaHistory}. */
@RequiredArgsConstructor
public final class SqliteTpaHistory implements TpaHistory {

  private final @NonNull SqlExecutor sqlExecutor;

  private static void setNullable(
      @NonNull PreparedStatement stmt, int index, @Nullable Object value, int sqlType)
      throws SQLException {
    if (value == null) {
      stmt.setNull(index, sqlType);
      return;
    }
    stmt.setObject(index, value, sqlType);
  }

  /**
   * Reads one row, or {@code null} when a persisted enum no longer exists — dropped from output.
   */
  private static @Nullable TpaHistoryEntry mapRow(@NonNull ResultSet rs) throws SQLException {
    var type = parseEnum(TeleportRequestType.class, rs.getString("type"));
    var status = parseEnum(TeleportRequestStatus.class, rs.getString("status"));
    if (type == null || status == null) {
      return null;
    }
    return new TpaHistoryEntry(
        UUID.fromString(rs.getString("requester_id")),
        new Participant(UUID.fromString(rs.getString("target_id")), rs.getString("target_name")),
        type,
        status,
        rs.getLong("created_at"),
        rs.getLong("resolved_at"),
        readDestination(rs));
  }

  private static @Nullable Destination readDestination(@NonNull ResultSet rs) throws SQLException {
    var world = rs.getString("world");
    if (world == null) {
      return null;
    }
    return new Destination(world, rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z"));
  }

  private static <E extends Enum<E>> @Nullable E parseEnum(
      @NonNull Class<E> type, @NonNull String value) {
    try {
      return Enum.valueOf(type, value);
    } catch (IllegalArgumentException | NullPointerException _) {
      return null;
    }
  }

  @Override
  public void push(@NonNull TpaHistoryEntry entry) {
    this.sqlExecutor.tx(
        conn -> {
          insert(conn, entry);
          trim(conn, entry.requester());
        });
  }

  @Override
  public List<TpaHistoryEntry> list(@NonNull UUID requester) {
    return this.sqlExecutor.query(
        TpaHistoryTable.LIST, SqliteTpaHistory::mapRow, requester.toString(), TpaHistory.CAPACITY);
  }

  private void insert(@NonNull Connection conn, @NonNull TpaHistoryEntry entry)
      throws SQLException {
    try (var statement = conn.prepareStatement(TpaHistoryTable.INSERT)) {
      var destination = entry.destination();

      statement.setString(1, entry.requester().toString());

      var targetParticipant = entry.target();
      var targetUuid = targetParticipant.id().toString();
      statement.setString(2, targetUuid);
      statement.setString(3, targetParticipant.name());

      statement.setString(4, entry.type().name());
      statement.setString(5, entry.status().name());
      statement.setLong(6, entry.createdAt());
      statement.setLong(7, entry.resolvedAt());

      var worldValue = destination == null ? null : destination.world();
      var xValue = destination == null ? null : destination.x();
      var yValue = destination == null ? null : destination.y();
      var zValue = destination == null ? null : destination.z();

      setNullable(statement, 8, worldValue, Types.VARCHAR);
      setNullable(statement, 9, xValue, Types.REAL);
      setNullable(statement, 10, yValue, Types.REAL);
      setNullable(statement, 11, zValue, Types.REAL);

      statement.executeUpdate();
    }
  }

  private void trim(@NonNull Connection conn, @NonNull UUID requester) throws SQLException {
    var requesterId = requester.toString();
    this.sqlExecutor.execute(
        conn, TpaHistoryTable.TRIM, requesterId, requesterId, TpaHistory.CAPACITY);
  }
}
