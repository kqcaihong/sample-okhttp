package com.learn.more;

import java.util.concurrent.ThreadFactory;

public class ThreadUtil {

  public static ThreadFactory threadFactory(String name, boolean deamon) {
    return runnable -> {
      Thread thread = new Thread(runnable, name);
      thread.setDaemon(deamon);
      return thread;
    };
  }
}
