package com.learn.more.http;

import com.learn.more.Call;
import com.learn.more.Interceptor;
import com.learn.more.RealCall;
import com.learn.more.Request;
import com.learn.more.Response;
import com.learn.more.connection.Transmitter;
import java.util.List;

public class RealInterceptorChain implements Interceptor.Chain {

  private final List<Interceptor> interceptors;
  private final Request request;
  private final RealCall call;
  private final Transmitter transmitter;
  private int index;

  public RealInterceptorChain(List<Interceptor> interceptors, Request request, RealCall call, Transmitter transmitter, int index) {
    this.interceptors = interceptors;
    this.request = request;
    this.call = call;
    this.transmitter = transmitter;
    this.index = index;
  }

  @Override
  public Request request() {
    return request;
  }

  @Override
  public Response proceed(Request request) throws Exception {
    if (index >= interceptors.size()) {
      throw new IllegalAccessException("no more interceptor");
    }
    RealInterceptorChain chain = new RealInterceptorChain(interceptors, request, call, transmitter, index + 1);
    Interceptor interceptor = interceptors.get(index);
    return interceptor.intercept(chain);
  }

  public List<Interceptor> getInterceptors() {
    return interceptors;
  }

  @Override
  public Call call() {
    return call;
  }
}
