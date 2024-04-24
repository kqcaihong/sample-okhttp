package com.learn.more.connection;

import com.learn.more.Interceptor;
import com.learn.more.OkHttpClient;
import com.learn.more.Response;

public class ConnectInterceptor implements Interceptor {
  private final OkHttpClient client;

  public ConnectInterceptor(OkHttpClient client) {
    this.client = client;
  }

  @Override
  public Response intercept(Chain chain) throws Exception {
    // pre
    System.out.println(this.getClass().getSimpleName() + " pre");
    Response response = chain.proceed(chain.request());
    // post
    System.out.println(this.getClass().getSimpleName() + " post");
    return response;
  }
}
