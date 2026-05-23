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
import org.jspecify.annotations.Nullable;

/**
 * Synchronous SQLite-backed {@link TpaHistory}: owns the table, the statements and the row
 * translation in one place. Off-thread execution is layered on separately by {@link
 * AsyncTpaHistory}.
 */
public final class SqliteTpaHistory implements TpaHistory {

  private static final String CREATE_TABLE =
      """
      CREATE TABLE IF NOT EXISTS tpa_history (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        requester_id TEXT NOT NULL,
        target_id TEXT NOT NULL,
        target_name TEXT NOT NULL,
        type TEXT NOT NULL,
        status TEXT NOT NULL,
        created_at INTEGER NOT NULL,
        resolved_at INTEGER NOT NULL,
        world TEXT,
        x REAL,
        y REAL,
        z REAL
      )
      """;

  private static final String CREATE_INDEX =
      """
      CREATE INDEX IF NOT EXISTS idx_tpa_history_requester \
      ON tpa_history(requester_id, created_at)
      """;

  private static final String INSERT =
      """
      INSERT INTO tpa_history \
      (requester_id, target_id, target_name, type, status, created_at, resolved_at, world, x, y, z) \
      VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)\
      """;

  private static final String TRIM =
      """
      DELETE FROM tpa_history
      WHERE requester_id = ?
        AND id NOT IN (
          SELECT id FROM tpa_history
          WHERE requester_id = ?
          ORDER BY created_at DESC
          LIMIT ?
        )
      """;

  private static final String LIST =
      """
      SELECT requester_id, target_id, target_name, type, status, created_at, resolved_at, world, x, y, z \
      FROM tpa_history \
      WHERE requester_id = ? ORDER BY created_at DESC LIMIT ?\
      """;

  private final SqlExecutor sqlExecutor;

  public SqliteTpaHistory(SqlExecutor sqlExecutor) {
    this.sqlExecutor = sqlExecutor;
    sqlExecutor.ddl(CREATE_TABLE, CREATE_INDEX);
  }

  private static void setNullable(
      PreparedStatement stmt, int index, @Nullable Object value, int sqlType) throws SQLException {
    if (value == null) {
      stmt.setNull(index, sqlType);
    } else {
      stmt.setObject(index, value, sqlType);
    }
  }

  /**
   * Reads one row, or {@code null} when a persisted enum no longer exists — dropped from output.
   */
  private static @Nullable TpaHistoryEntry mapRow(ResultSet rs) throws SQLException {
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

  private static @Nullable Destination readDestination(ResultSet rs) throws SQLException {
    var world = rs.getString("world");
    if (world == null) {
      return null;
    }
    return new Destination(world, rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z"));
  }

  private static <E extends Enum<E>> @Nullable E parseEnum(Class<E> type, String value) {
    try {
      return Enum.valueOf(type, value);
    } catch (IllegalArgumentException | NullPointerException _) {
      return null;
    }
  }

  @Override
  public void push(TpaHistoryEntry entry) {
    sqlExecutor.tx(
        conn -> {
          insert(conn, entry);
          trim(conn, entry.requester());
        });
  }

  @Override
  public List<TpaHistoryEntry> list(UUID requester) {
    return sqlExecutor.query(LIST, SqliteTpaHistory::mapRow, requester.toString(), CAPACITY);
  }

  private void insert(Connection conn, TpaHistoryEntry entry) throws SQLException {
    try (var statement = conn.prepareStatement(INSERT)) {
      var destination = entry.destination();
      statement.setString(1, entry.requester().toString());
      statement.setString(2, entry.target().id().toString());
      statement.setString(3, entry.target().name());
      statement.setString(4, entry.type().name());
      statement.setString(5, entry.status().name());
      statement.setLong(6, entry.createdAt());
      statement.setLong(7, entry.resolvedAt());
      setNullable(statement, 8, destination == null ? null : destination.world(), Types.VARCHAR);
      setNullable(statement, 9, destination == null ? null : destination.x(), Types.REAL);
      setNullable(statement, 10, destination == null ? null : destination.y(), Types.REAL);
      setNullable(statement, 11, destination == null ? null : destination.z(), Types.REAL);
      statement.executeUpdate();
    }
  }

  private void trim(Connection conn, UUID requester) throws SQLException {
    var requesterId = requester.toString();
    sqlExecutor.execute(conn, TRIM, requesterId, requesterId, CAPACITY);
  }
}
