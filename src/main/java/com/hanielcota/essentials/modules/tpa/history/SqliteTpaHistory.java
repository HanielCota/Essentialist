package com.hanielcota.essentials.modules.tpa.history;

import com.hanielcota.essentials.database.SqlExecutor;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Synchronous SQLite-backed {@link TpaHistory}.
 *
 * <p>Sole responsibility: run the history statements. The table definition lives in {@link
 * TpaHistorySchema}, row translation in {@link TpaHistoryRowMapper}, and off-thread execution is
 * layered on separately by {@link AsyncTpaHistory}.
 */
public final class SqliteTpaHistory implements TpaHistory {

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
  private final TpaHistoryRowMapper mapper = new TpaHistoryRowMapper();

  public SqliteTpaHistory(SqlExecutor sqlExecutor) {
    this.sqlExecutor = Objects.requireNonNull(sqlExecutor, "sqlExecutor");
    TpaHistorySchema.create(sqlExecutor);
  }

  @Override
  public void push(TpaHistoryEntry entry) {
    Objects.requireNonNull(entry, "entry");
    sqlExecutor.tx(
        conn -> {
          insert(conn, entry);
          trim(conn, entry.requester());
        });
  }

  @Override
  public List<TpaHistoryEntry> list(UUID requester) {
    Objects.requireNonNull(requester, "requester");
    return sqlExecutor.query(LIST, mapper::map, requester.toString(), CAPACITY);
  }

  /** Inserts the entry as a new history row. */
  private void insert(Connection conn, TpaHistoryEntry entry) throws SQLException {
    try (var statement = conn.prepareStatement(INSERT)) {
      mapper.bind(statement, entry);
      statement.executeUpdate();
    }
  }

  /** Drops every row of {@code requester} beyond the {@link TpaHistory#CAPACITY} most recent. */
  private void trim(Connection conn, UUID requester) throws SQLException {
    var requesterId = requester.toString();
    sqlExecutor.execute(conn, TRIM, requesterId, requesterId, CAPACITY);
  }
}
