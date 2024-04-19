package com.learn.more.http;

import com.learn.more.Interceptor;
import com.learn.more.Response;

// 最后一个Interceptor
public class CallServerInterceptor implements Interceptor {

  @Override
  public Response intercept(Chain chain) throws Exception {
    // pre
    System.out.println(this.getClass().getName() + " pre");
    // 网络IO
    Response response = new Response();
    // post
    System.out.println(this.getClass().getName() + " post");
    return response;
  }
}
