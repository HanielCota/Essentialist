package com.hanielcota.essentials.database;

import java.sql.Connection;
import java.sql.SQLException;
import lombok.NonNull;

/**
 * Transactional SQL surface: lets a single block of work commit/rollback atomically and lets the
 * block run additional statements on the active connection.
 */
public interface SqlTransactor {

  /** Runs a block of operations within a transaction. */
  void tx(@NonNull TxBlock work);

  /**
   * Executes a single statement on an existing connection (typically from inside a {@link
   * #tx(TxBlock)} block).
   */
  int execute(@NonNull Connection conn, @NonNull String sql, @NonNull Object... params)
      throws SQLException;
}
