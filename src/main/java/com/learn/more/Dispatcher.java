package com.learn.more;

import com.learn.more.RealCall.AsyncCall;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

// todo 支持异步任务
public class Dispatcher {

  private Runnable idleCallback;
  private int maxRequests = 128;
  private int maxRequestsPerHost = 8;
  private ExecutorService executorService;

  private final Deque<RealCall> runningSyncCalls = new ArrayDeque<>();
  private final Deque<AsyncCall> readyAsyncCalls = new ArrayDeque<>();
  private final Deque<AsyncCall> runningAsyncCalls = new ArrayDeque<>();

  public Dispatcher() {
  }

  public Dispatcher(ExecutorService executorService) {
    this.executorService = executorService;
  }


  public synchronized ExecutorService executorService() {
    if (Objects.isNull(executorService)) {
      executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS, new SynchronousQueue<>(),
          ThreadUtil.threadFactory("OkHttp Dispatcher", false));
    }
    return executorService;
  }

  public void setIdleCallback(Runnable idleCallback) {
    this.idleCallback = idleCallback;
  }

  synchronized void executed(RealCall call) {
    runningSyncCalls.add(call);
  }

  public void finished(RealCall call) {
    finished(runningSyncCalls, call);
  }


  public void finished(AsyncCall call) {
    call.counter().decrementAndGet();
    finished(runningAsyncCalls, call);
  }

  private <T> void finished(Deque<T> deque, T call) {
    synchronized (this) {
      if (!deque.remove(call)) {
        throw new AssertionError("Call wasn't in-flight!");
      }
    }
    // 接着处理队列任务
    boolean isRunning = promoteAndExecute();
    if (!isRunning && idleCallback != null) {
      idleCallback.run();
    }
  }

  public void asyncExecute(AsyncCall call) {
    synchronized (this) {
      readyAsyncCalls.add(call);
      // 对每个host的并发限制
      AsyncCall existCall = findExistCallWithSameHost(call);
      if (Objects.nonNull(existCall)) {
        call.reuseHostCounter(existCall);
      }
    }
    promoteAndExecute();
  }

  private AsyncCall findExistCallWithSameHost(AsyncCall call) {
    String host = call.host();
    for (AsyncCall existingCall : runningAsyncCalls) {
      if (existingCall.host().equals(host)) {
        return existingCall;
      }
    }
    for (AsyncCall existingCall : readyAsyncCalls) {
      if (existingCall.host().equals(host)) {
        return existingCall;
      }
    }
    return null;

  }

  private boolean promoteAndExecute() {
    assert !Thread.holdsLock(this);

    List<AsyncCall> executableCalls = new ArrayList<>();
    boolean isRunning = false;
    synchronized (this) {
      Iterator<AsyncCall> iterator = readyAsyncCalls.iterator();
      while (iterator.hasNext()) {
        AsyncCall call = iterator.next();
        if (runningAsyncCalls.size() > maxRequests) {
          break;
        }
        //  host限流如何实现
        if (!call.belowTheLimit(maxRequestsPerHost)) {
          continue;
        }
        call.counter().incrementAndGet();
        iterator.remove();
        executableCalls.add(call);
        runningAsyncCalls.add(call);
      }
      isRunning = runningAsyncCalls.size() > 0;
    }

    for (AsyncCall call : executableCalls) {
      // 因为有时间、异常处理、回调等，因此交给AsyncCall来执行
      call.executeOn(executorService());
    }
    return isRunning;
  }
}
