package com.hanielcota.essentials.paper;

public interface TaskDispatcher {

  void sync(Runnable task);

  void async(Runnable task);
}
