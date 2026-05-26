package com.hanielcota.essentials.database.executor;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import lombok.NonNull;

/** Functional interface for binding parameters to a {@link PreparedStatement}. */
@FunctionalInterface
public interface StatementBinder {

  void bind(@NonNull PreparedStatement stmt) throws SQLException;
}
