package com.hanielcota.essentials.modules.tpa.history;

import com.hanielcota.essentials.database.SqlExecutor;
import com.hanielcota.essentials.modules.tpa.domain.Destination;
import com.hanielcota.essentials.modules.tpa.domain.Participant;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequestStatus;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequestType;
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
    var typeRaw = rs.getString("type");
    var type = parseEnum(TeleportRequestType.class, typeRaw);

    var statusRaw = rs.getString("status");
    var status = parseEnum(TeleportRequestStatus.class, statusRaw);

    if (type == null || status == null) {
      return null;
    }

    var requesterRaw = rs.getString("requester_id");
    var requesterId = UUID.fromString(requesterRaw);

    var targetRaw = rs.getString("target_id");
    var targetId = UUID.fromString(targetRaw);
    var targetName = rs.getString("target_name");
    var target = new Participant(targetId, targetName);

    var createdAt = rs.getLong("created_at");
    var resolvedAt = rs.getLong("resolved_at");
    var destination = readDestination(rs);

    return new TpaHistoryEntry(
        requesterId, target, type, status, createdAt, resolvedAt, destination);
  }

  private static @Nullable Destination readDestination(@NonNull ResultSet rs) throws SQLException {
    var world = rs.getString("world");
    if (world == null) {
      return null;
    }

    var x = rs.getDouble("x");
    var y = rs.getDouble("y");
    var z = rs.getDouble("z");

    return new Destination(world, x, y, z);
  }

  private static <E extends Enum<E>> @Nullable E parseEnum(
      @NonNull Class<E> type, @NonNull String value) {
    try {
      return Enum.valueOf(type, value);
    } catch (IllegalArgumentException | NullPointerException _) {
      return null;
    }
  }

  private static void bindEntry(
      @NonNull PreparedStatement statement, @NonNull TpaHistoryEntry entry) throws SQLException {
    var requesterId = entry.requester().toString();
    statement.setString(1, requesterId);

    var targetParticipant = entry.target();
    var targetUuid = targetParticipant.id().toString();
    var targetName = targetParticipant.name();
    statement.setString(2, targetUuid);
    statement.setString(3, targetName);

    var typeName = entry.type().name();
    var statusName = entry.status().name();
    statement.setString(4, typeName);
    statement.setString(5, statusName);

    statement.setLong(6, entry.createdAt());
    statement.setLong(7, entry.resolvedAt());
  }

  private static void bindDestination(
      @NonNull PreparedStatement statement, @Nullable Destination destination) throws SQLException {
    String worldValue = null;
    Double xValue = null;
    Double yValue = null;
    Double zValue = null;

    if (destination != null) {
      worldValue = destination.world();
      xValue = destination.x();
      yValue = destination.y();
      zValue = destination.z();
    }

    setNullable(statement, 8, worldValue, Types.VARCHAR);
    setNullable(statement, 9, xValue, Types.REAL);
    setNullable(statement, 10, yValue, Types.REAL);
    setNullable(statement, 11, zValue, Types.REAL);
  }

  @Override
  public void push(@NonNull TpaHistoryEntry entry) {
    this.sqlExecutor.tx(conn -> persist(conn, entry));
  }

  @Override
  public List<TpaHistoryEntry> list(@NonNull UUID requester) {
    var requesterId = requester.toString();
    var capacity = TpaHistory.CAPACITY;

    return this.sqlExecutor.query(
        TpaHistoryTable.LIST, SqliteTpaHistory::mapRow, requesterId, capacity);
  }

  private void persist(@NonNull Connection conn, @NonNull TpaHistoryEntry entry)
      throws SQLException {
    insert(conn, entry);
    trim(conn, entry.requester());
  }

  private void insert(@NonNull Connection conn, @NonNull TpaHistoryEntry entry)
      throws SQLException {
    try (var statement = conn.prepareStatement(TpaHistoryTable.INSERT)) {
      bindEntry(statement, entry);
      bindDestination(statement, entry.destination());

      statement.executeUpdate();
    }
  }

  private void trim(@NonNull Connection conn, @NonNull UUID requester) throws SQLException {
    var requesterId = requester.toString();
    this.sqlExecutor.execute(
        conn, TpaHistoryTable.TRIM, requesterId, requesterId, TpaHistory.CAPACITY);
  }
}
