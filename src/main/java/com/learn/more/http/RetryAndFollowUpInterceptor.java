package com.learn.more.http;

import com.learn.more.Interceptor;
import com.learn.more.Request;
import com.learn.more.Response;

public class RetryAndFollowUpInterceptor implements Interceptor {

  @Override
  public Response intercept(Chain chain) throws Exception {
    Request request = chain.request();
    // pre
    System.out.println(this.getClass().getName()+" pre");
    Response response = chain.proceed(request);
    // post
    System.out.println(this.getClass().getName()+" post");
    return response;
  }
}
