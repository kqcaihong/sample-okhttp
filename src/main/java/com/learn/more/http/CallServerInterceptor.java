package com.learn.more.http;

import com.learn.more.Call;
import com.learn.more.Interceptor;
import com.learn.more.OkHttpClient;
import com.learn.more.Request;
import com.learn.more.Response;
import com.learn.more.connection.RealConnection;

// 最后一个Interceptor，不调用Chain.proceed
public class CallServerInterceptor implements Interceptor {

  private static final String BLANK = " ";

  private final OkHttpClient client;

  public CallServerInterceptor(OkHttpClient client) {
    this.client = client;
  }

  @Override
  public Response intercept(Chain chain) throws Exception {
    // pre
    System.out.println(this.getClass().getSimpleName() + " pre");
    // 网络IO
    Response response = doGet(chain);

    // post
    System.out.println(this.getClass().getSimpleName() + " post");
    return response;
  }

  private Response doGet(Chain chain) {
    Response response = new Response();
    StringBuilder builder = new StringBuilder();
    Request request = chain.request();
    HttpUrl url = request.getUrl();
    String line = builder.append(request.getMethod()).append(BLANK).append(url.getUrl()).append(BLANK).append(url.scheme + "/1.1").toString();
    RealConnection connection = chain.call().transmitter().connection();
//    todo.
    return response;
  }
}
