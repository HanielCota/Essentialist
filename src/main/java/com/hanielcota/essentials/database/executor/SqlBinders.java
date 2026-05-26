package com.hanielcota.essentials.database.executor;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class SqlBinders {

  static StatementBinder positional(@NonNull Object[] params) {
    return stmt -> {
      var length = params.length;

      for (var i = 0; i < length; i++) {
        var paramIndex = i + 1;
        var paramValue = params[i];

        stmt.setObject(paramIndex, paramValue);
      }
    };
  }
}
