package com.learn.more;

import com.learn.more.http.Headers;
import com.learn.more.http.HttpUrl;
import com.learn.more.http.RequestBody;

public class Request {

  private final HttpUrl url;
  private final String method;
  private final Headers headers;
  private final RequestBody requestBody;

  public Request(HttpUrl url, String method, Headers headers, RequestBody requestBody) {
    this.url = url;
    this.method = method;
    this.headers = headers;
    this.requestBody = requestBody;
  }

  public HttpUrl getUrl() {
    return url;
  }

  public String getMethod() {
    return method;
  }

  public Headers getHeaders() {
    return headers;
  }

  public RequestBody getRequestBody() {
    return requestBody;
  }
}
