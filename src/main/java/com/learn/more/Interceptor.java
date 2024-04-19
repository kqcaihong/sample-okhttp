package com.learn.more;

/**
 * 拦截器
 */
public interface Interceptor {

  // 拦截逻辑
  Response intercept(Chain chain) throws Exception;

  /**
   * 拦截器
   */
  interface Chain {

    // CallServerInterceptor#intercept，没有调用该方法
    Response proceed(Request request) throws Exception;

    Request request();
  }
}
