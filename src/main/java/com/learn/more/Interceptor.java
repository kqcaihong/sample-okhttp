package com.learn.more;

public interface Interceptor {
  Response intercept(Chain chain) throws Exception;

  interface Chain {
    Request request();

    Response proceed(Request request) throws Exception;

  }
}
