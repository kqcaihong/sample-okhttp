package com.learn.more.http;

import com.learn.more.Interceptor;
import com.learn.more.OkHttpClient;
import com.learn.more.Response;

// 最后一个Interceptor
public class CallServerInterceptor implements Interceptor {
  private final OkHttpClient client;

  public CallServerInterceptor(OkHttpClient client) {
    this.client = client;
  }

  @Override
  public Response intercept(Chain chain) throws Exception {
    // pre
    System.out.println(this.getClass().getSimpleName() + " pre");
    // 网络IO
    Response response = new Response();
    // post
    System.out.println(this.getClass().getSimpleName() + " post");
    return response;
  }
}
