package com.learn.more.http;

import com.learn.more.Call;
import com.learn.more.Interceptor;
import com.learn.more.Request;
import com.learn.more.Response;
import java.util.List;

public class RealInterceptorChain implements Interceptor.Chain {

  private final List<Interceptor> interceptors;
  private final Request request;
  private final Call call;
  private final int index;

  public RealInterceptorChain(List<Interceptor> interceptors, Request request, Call call, int index) {
    this.interceptors = interceptors;
    this.request = request;
    this.call = call;
    if (index < 0) {
      throw new IllegalArgumentException("index is wrong.");
    }
    this.index = index;
  }

  @Override
  public Response proceed(Request request) throws Exception {
    if (index > interceptors.size()) {
      throw new IllegalArgumentException("The last interceptor should not call proceed()");
    }
    RealInterceptorChain chain = new RealInterceptorChain(interceptors, request, call, index + 1);
    Interceptor current = interceptors.get(index);
    return current.intercept(chain);
  }

  @Override
  public Request request() {
    return request;
  }
}
