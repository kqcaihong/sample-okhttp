package com.learn.more.http;

import com.learn.more.Interceptor;
import com.learn.more.OkHttpClient;
import com.learn.more.Request;
import com.learn.more.Response;
import com.learn.more.connection.RealConnection;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

// 最后一个Interceptor，不调用Chain.proceed
public class CallServerInterceptor implements Interceptor {

  private static final String BLANK = " ";
  private static final String SEPARATE = "\r\n";

  private static final byte[] BUFFER = new byte[1024 * 8];

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

  // todo 一个简单网络IO
  private Response doGet(Chain chain) {
    Response response = new Response();
    StringBuilder builder = new StringBuilder();
    Request request = chain.request();
    HttpUrl url = request.getUrl();
    String line = builder.append(request.getMethod()).append(BLANK).append(url.getUrl()).append(BLANK).append(url.scheme).append("/1.1")
        .append(SEPARATE)
        .append("Content-Type: application/json").append(SEPARATE)
        .append("Accept: application/json, text/html").append(SEPARATE)
        .append(SEPARATE)
        .toString();

    RealConnection connection = chain.call().transmitter().connection();
    try {
      OutputStream out = connection.socket().getOutputStream();
      out.write(line.getBytes(StandardCharsets.UTF_8));
      out.flush();
      InputStream in = connection.socket().getInputStream();
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      int read = 0;
      while ((read = in.read(BUFFER)) != -1) {
        buffer.write(BUFFER, 0, read);
      }
      String content = buffer.toString(StandardCharsets.UTF_8.name());
      response.setContent(content);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return response;
  }
}
