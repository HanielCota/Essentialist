package com.hanielcota.essentials.database.executor;

import java.sql.Connection;
import java.sql.SQLException;
import lombok.NonNull;

/** Functional interface representing a block of work to be run in a database transaction. */
@FunctionalInterface
public interface TxBlock {

  void run(@NonNull Connection conn) throws SQLException;
}
