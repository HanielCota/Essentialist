package com.hanielcota.essentials.modules.tpa.history;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import org.jspecify.annotations.Nullable;

/**
 * Writes parameters onto a {@link PreparedStatement} left to right.
 *
 * <p>Callers append values in order and never juggle a column index, so the index can never drift
 * out of sync with the SQL.
 */
final class StatementCursor {

  private final PreparedStatement statement;
  private int index;

  StatementCursor(PreparedStatement statement) {
    this.statement = statement;
  }

  void text(String value) throws SQLException {
    statement.setString(++index, value);
  }

  void number(long value) throws SQLException {
    statement.setLong(++index, value);
  }

  void nullableText(@Nullable String value) throws SQLException {
    write(value, Types.VARCHAR);
  }

  void nullableNumber(@Nullable Double value) throws SQLException {
    write(value, Types.REAL);
  }

  private void write(@Nullable Object value, int sqlType) throws SQLException {
    if (value == null) {
      statement.setNull(++index, sqlType);
      return;
    }
    statement.setObject(++index, value, sqlType);
  }
}
