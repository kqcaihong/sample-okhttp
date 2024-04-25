package com.learn.more.connection;

import com.learn.more.Address;
import com.learn.more.ThreadUtil;
import java.io.IOException;
import java.net.Socket;
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

/**
 * 管理连接：保存、查找、清除等
 */
public class RealConnectionPool {

  private final int maxIdleConnections;
  private final long keepAliveDurationNs;
  private final Deque<RealConnection> connections = new ArrayDeque<>();

  // todo 清理
  private static final ExecutorService executor = new ThreadPoolExecutor(1, 1, 60, TimeUnit.SECONDS, new SynchronousQueue<>(),
      ThreadUtil.threadFactory("OkHttp RealConnectionPool", true));
  private volatile boolean cleanupRunning = false;
  private Runnable cleanupRunnable = () -> {
    while (true) {
      long waitNanos = cleanup();
      if (waitNanos < 0) {
        return;
      }
      if (waitNanos > 0) {
        long waitMillis = waitNanos / 1000000L;
        waitNanos -= (waitMillis * 1000000L);
        synchronized (this) {
          try {
            // todo 何时唤醒
            this.wait(waitMillis, (int) waitNanos);
          } catch (InterruptedException e) {
          }
        }
      }
    }
  };

  public RealConnectionPool(int maxIdleConnections, long keepAliveDuration, TimeUnit timeUnit) {
    this.maxIdleConnections = maxIdleConnections;
    if (keepAliveDuration <= 0) {
      throw new IllegalArgumentException("keepAliveDuration <= 0: " + keepAliveDuration);
    }
    this.keepAliveDurationNs = timeUnit.toNanos(keepAliveDuration);
  }

  public void put(RealConnection connection) {
    synchronized (this) {
      connections.add(connection);
      if (!cleanupRunning) {
        cleanupRunning = true;
        executor.execute(cleanupRunnable);
      }
    }
  }

  public int idleConnectionCount() {
    int count = 0;
    for (RealConnection connection : connections) {
      if (connection.idle()) {
        count++;
      }
    }
    return count;
  }

  public int connectionCount() {
    return connections.size();
  }

  public synchronized void evictAll() {
    List<RealConnection> idleConnections = new ArrayList<>();
    synchronized (this) {
      for (Iterator<RealConnection> iterator = connections.iterator(); iterator.hasNext(); ) {
        RealConnection connection = iterator.next();
        if (connection.idle()) {
          iterator.remove();
          connection.noCarryTransmitter();
          idleConnections.add(connection);
        }
      }
    }

    for (RealConnection connection : idleConnections) {
      closeQuietly(connection.socket);
    }
  }

  private void closeQuietly(Socket socket) {
    try {
      if (Objects.nonNull(socket)) {
        socket.close();
      }
    } catch (IOException e) {
    }
  }

  // todo 返回到下次执行的剩余时间
  private long cleanup() {
    return -1;
  }

  public RealConnection findConnection(int connectTimeout, int readTimeout, int writeTimeout, Address address, Transmitter transmitter) {
    RealConnection selected = null;
    for (RealConnection connection : connections) {
      if (!connection.isEligible(address)) {
        continue;
      }
      selected = connection;
    }
    if (Objects.nonNull(selected)) {
      selected.addTransmitter(transmitter);
    }

    // 创建一个连接
    try {
      RealConnection connection = new RealConnection(connectTimeout, readTimeout, writeTimeout, address);
      connection.addTransmitter(transmitter);
      put(connection);
    } catch (IOException e) {
    }
    return selected;
  }
}
