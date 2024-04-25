package com.learn.more;

import com.learn.more.http.HttpUrl;
import java.net.Proxy;

// A specification for a connection to an origin server.
public class Address {

  private HttpUrl url;
  private Proxy proxy;
  //SSL

  public HttpUrl url() {
    return url;
  }

  public Proxy proxy() {
    return proxy;
  }
}
