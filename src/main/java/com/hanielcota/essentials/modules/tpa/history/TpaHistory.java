package com.hanielcota.essentials.modules.tpa.history;

import java.util.List;
import java.util.UUID;
import lombok.NonNull;

/** Persistent log of resolved teleport requests, queried per requester. */
public interface TpaHistory {

  /** How many recent entries are kept per requester (and shown in the menu). */
  int CAPACITY = 5;

  /** Appends a resolved request to history. */
  void push(@NonNull TpaHistoryEntry entry);

  /** Returns the requester's most recent entries, newest first, capped at {@link #CAPACITY}. */
  List<TpaHistoryEntry> list(@NonNull UUID requester);
}
