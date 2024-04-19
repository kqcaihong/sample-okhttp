package com.learn.more;

public interface Interceptor {
  Response intercept(Object object) throws Exception;
}
