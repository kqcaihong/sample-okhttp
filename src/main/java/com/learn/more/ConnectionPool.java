package com.learn.more;

import com.learn.more.connection.RealConnectionPool;
import java.util.concurrent.TimeUnit;

// 装饰类
public class ConnectionPool {

  private RealConnectionPool delegate;

  public ConnectionPool() {
    this(5, 5, TimeUnit.MINUTES);
  }

  public ConnectionPool(int maxIdleConnections, long keepAliveDuration, TimeUnit timeUnit) {
    this.delegate = new RealConnectionPool(maxIdleConnections, maxIdleConnections, timeUnit);
  }

  // 获取连接池状态
  public int idleConnectionCount() {
    return delegate.idleConnectionCount();
  }

  public int connectionCount() {
    return delegate.connectionCount();
  }

  // 清理空闲连接，不受空闲数、空闲时间限制
  public void evictAll() {
    delegate.evictAll();
  }
}
