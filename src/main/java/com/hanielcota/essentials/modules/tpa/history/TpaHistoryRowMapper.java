package com.hanielcota.essentials.modules.tpa.history;

import com.hanielcota.essentials.database.Sql;
import com.hanielcota.essentials.modules.tpa.model.Destination;
import com.hanielcota.essentials.modules.tpa.model.Participant;
import com.hanielcota.essentials.modules.tpa.model.TeleportRequestStatus;
import com.hanielcota.essentials.modules.tpa.model.TeleportRequestType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.jspecify.annotations.Nullable;

/**
 * Translates a {@link TpaHistoryEntry} to and from a {@code tpa_history} row.
 *
 * <p>Sole responsibility: object–relational mapping. It holds no connection and runs no statement —
 * {@link SqliteTpaHistory} owns those, and {@link StatementCursor} owns positional binding.
 */
final class TpaHistoryRowMapper {

  /** Returns {@code null} when the row stored no destination. */
  private static @Nullable Destination readDestination(ResultSet rs) throws SQLException {
    String world = rs.getString("world");
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

  /** Binds every column of an entry onto the INSERT statement, in table-declaration order. */
  void bind(PreparedStatement insert, TpaHistoryEntry entry) throws SQLException {
    var column = new StatementCursor(insert);
    column.text(entry.requester().toString());
    column.text(entry.target().id().toString());
    column.text(entry.target().name());
    column.text(entry.type().name());
    column.text(entry.status().name());
    column.number(entry.createdAt());
    column.number(entry.resolvedAt());

    var destination = entry.destination();
    column.nullableText(destination == null ? null : destination.world());
    column.nullableNumber(destination == null ? null : destination.x());
    column.nullableNumber(destination == null ? null : destination.y());
    column.nullableNumber(destination == null ? null : destination.z());
  }

  /**
   * Reads one row into a {@link TpaHistoryEntry}, or {@code null} when a persisted enum name no
   * longer exists in code — {@link Sql#query} drops null rows from the result.
   */
  @Nullable TpaHistoryEntry map(ResultSet rs) throws SQLException {
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
}
