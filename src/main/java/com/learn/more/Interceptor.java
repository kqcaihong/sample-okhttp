package com.learn.more;

public interface Interceptor {

  Response intercept(Chain chain) throws Exception;

  // 把超时时间放到了这儿
  interface Chain {

    Request request();

    Call call();

    Response proceed (Request request) throws Exception;
    }
  }
