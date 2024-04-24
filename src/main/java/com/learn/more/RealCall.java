package com.learn.more;

import com.learn.more.connection.ConnectInterceptor;
import com.learn.more.connection.Transmitter;
import com.learn.more.http.CallServerInterceptor;
import com.learn.more.http.RealInterceptorChain;
import com.learn.more.http.RetryAndFollowUpInterceptor;

import java.util.ArrayList;
import java.util.List;

public class RealCall implements Call {
  private final OkHttpClient client;
  private final Request request;
  private final Transmitter transmitter;
  private boolean executed;

  private RealCall(OkHttpClient client, Request request) {
    this.client = client;
    this.request = request;
    this.transmitter = new Transmitter(client, this);
  }

  @Override
  public Request request() {
    return request;
  }

  @Override
  public Response execute() throws Exception {
    synchronized (this) {
      if (executed) {
        throw new IllegalStateException("Already Executed");
      }
      executed = true;
    }

    transmitter.callStart();
    client.getDispatcher().executed(this);
    try {
      return getResponseWithInterceptorChain();
    } finally {
      client.getDispatcher().finished(this);
    }
  }

  private Response getResponseWithInterceptorChain() {
    List<Interceptor> interceptors = new ArrayList<>();
    interceptors.add(new RetryAndFollowUpInterceptor(client));
    interceptors.add(new ConnectInterceptor(client));
    interceptors.add(new CallServerInterceptor(client));
    RealInterceptorChain chain = new RealInterceptorChain(interceptors, request, this, 0);
    try {
      return chain.proceed(request);
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      // todo 归还连接;
    }
  }

  @Override
  public void asyncExecute(Callback callback) {

  }

  @Override
  public void cancel() {

  }

  @Override
  public boolean canceled() {
    return false;
  }

  @Override
  public Call clone() {
    return null;
  }
}
