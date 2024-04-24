package com.learn.more;

import java.util.ArrayDeque;
import java.util.Deque;

// todo 支持异步任务
public class Dispatcher {
  private Runnable idleCallback;

  private final Deque<RealCall> runningSyncCalls = new ArrayDeque<>();

  public Dispatcher() {
  }

  public void setIdleCallback(Runnable idleCallback) {
    this.idleCallback = idleCallback;
  }

  synchronized void executed(RealCall call) {
    runningSyncCalls.add(call);
  }

  public void finished(RealCall call) {
    synchronized (this) {
      if (!runningSyncCalls.remove(call)) {
        throw new AssertionError("Call wasn't in-flight!");
      }
    }
    // todo
    boolean isRunning = false;
    if (!isRunning && idleCallback != null) {
      idleCallback.run();
    }

  }
}
