package com.learn.more.http;

public class HttpUrl {

  final String scheme;
  private final String host;
  private final int port;
  private final String url;

  public HttpUrl(String scheme, String host, int port, String url) {
    this.scheme = scheme;
    this.host = host;
    this.port = port;
    this.url = url;
  }

  public String getScheme() {
    return scheme;
  }

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }

  public String getUrl() {
    return url;
  }

  public enum SchemeEnum {
    HTTPS, HTTP
  }
}
