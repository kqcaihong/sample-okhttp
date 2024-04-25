package com.learn.more.connection;

import com.learn.more.Interceptor;
import com.learn.more.OkHttpClient;
import com.learn.more.Response;
import com.learn.more.http.RealInterceptorChain;

public class ConnectInterceptor implements Interceptor {
  private final OkHttpClient client;

  public ConnectInterceptor(OkHttpClient client) {
    this.client = client;
  }

  // todo 如何将Connection继续传递
  @Override
  public Response intercept(Chain chain) throws Exception {
    RealInterceptorChain interceptorChain = (RealInterceptorChain) chain;
    Transmitter transmitter = interceptorChain.getTransmitter();
    RealConnectionPool connectionPool = client.getConnectionPool().realConnectionPool();
    connectionPool.findConnection(client.getConnectTimeout(), client.getReadTimeout(),
        client.getWriteTimeout(), interceptorChain.getCall().address(), transmitter);

    System.out.println(this.getClass().getSimpleName() + " " + transmitter.connection);
    System.out.println("connectionCount= " + connectionPool.connectionCount());
    Response response = chain.proceed(chain.request());
    // post
    System.out.println(this.getClass().getSimpleName() + " post");
    return response;
  }
}
