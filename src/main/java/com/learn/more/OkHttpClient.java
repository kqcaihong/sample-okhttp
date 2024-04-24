package com.learn.more;

import lombok.Getter;

import java.net.Proxy;
import java.net.ProxySelector;
import java.util.ArrayList;
import java.util.List;

@Getter
public class OkHttpClient implements Call.Factory{
  private final Dispatcher dispatcher;
  private final List<Interceptor> interceptors;
  private final Proxy proxy;
  private final ProxySelector proxySelector;
  private final ConnectionPool connectionPool;
  private final EventListener.Factory eventListenerFactory;
  private final int connectTimeout;
  private final int readTimeout;
  private final int writeTimeout;

  public OkHttpClient() {
    this(new Builder());
  }

  OkHttpClient(Builder builder) {
    this.dispatcher = builder.dispatcher;
    this.interceptors = builder.interceptors;
    this.proxy = builder.proxy;
    this.proxySelector = builder.proxySelector;
    this.connectionPool = builder.connectionPool;
    this.eventListenerFactory = builder.eventListenerFactory;
    this.connectTimeout = builder.connectTimeout;
    this.readTimeout = builder.readTimeout;
    this.writeTimeout = builder.writeTimeout;
  }


  public static final class Builder {
    private final Dispatcher dispatcher;
    private final List<Interceptor> interceptors;
    private Proxy proxy;
    private ProxySelector proxySelector;
    private final ConnectionPool connectionPool;
    private EventListener.Factory eventListenerFactory;
    private final int connectTimeout;
    private final int readTimeout;
    private final int writeTimeout;

    // 用默认值
    public Builder() {
      this.dispatcher = new Dispatcher();
      this.interceptors = new ArrayList<>();
      this.connectionPool = new ConnectionPool();
      this.eventListenerFactory = EventListener.factory(EventListener.NONE);
      this.connectTimeout = 10000;
      this.readTimeout = 10000;
      this.writeTimeout = 10000;
    }

    // setter
    public Builder proxy(Proxy proxy) {
      this.proxy = proxy;
      return this;
    }

    public Builder proxy(ProxySelector proxySelector) {
      this.proxySelector = proxySelector;
      return this;
    }


    public OkHttpClient build() {
      return new OkHttpClient(this);
    }
  }

  @Override
  public Call newCall(Request request) {
    return new RealCall(this,request);
  }
}
