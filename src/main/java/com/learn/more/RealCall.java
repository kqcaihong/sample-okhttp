package com.learn.more;

import com.learn.more.connection.ConnectInterceptor;
import com.learn.more.connection.Transmitter;
import com.learn.more.http.CallServerInterceptor;
import com.learn.more.http.RealInterceptorChain;
import com.learn.more.http.RetryAndFollowUpInterceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

public class RealCall implements Call {

  private final OkHttpClient client;
  private final Request request;
  private final Transmitter transmitter;
  private boolean executed;

  public RealCall(OkHttpClient client, Request request) {
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
    RealInterceptorChain chain = new RealInterceptorChain(interceptors, request, this, transmitter, 0);
    try {
      return chain.proceed(request);
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      // todo 归还连接等;

    }
  }

  @Override
  public void asyncExecute(Callback callback) {
    synchronized (this) {
      if (executed) {
        throw new IllegalStateException("Already Executed");
      }
      executed = true;
    }
    transmitter.callStart();
    client.getDispatcher().asyncExecute(new AsyncCall(callback));
  }

  @Override
  public void cancel() {
    transmitter.cancel();
  }

  @Override
  public boolean canceled() {
    return false;
  }

  @Override
  public Call clone() {
    return new RealCall(client, request);
  }


  public Address address() {
    return new Address(request.getUrl(), client.getProxy());
  }

  public class AsyncCall implements Runnable {

    private final Callback callback;
    // 对每个host的并发限制
    private volatile AtomicInteger callsPerHostCounter = new AtomicInteger(0);

    public AsyncCall(Callback callback) {
      this.callback = callback;
    }

    public AtomicInteger counter() {
      return callsPerHostCounter;
    }


    public boolean belowTheLimit(int limit) {
      return callsPerHostCounter.get() < limit;
    }

    public void executeOn(ExecutorService executorService) {
      executorService.execute(this);
    }

    public String host() {
      return request.getUrl().getHost();
    }

    public void reuseHostCounter(AsyncCall existCall) {
      this.callsPerHostCounter = existCall.callsPerHostCounter;
    }

    // 同一线程执行回调
    @Override
    public void run() {
      boolean callbacked = false;
      try {
        Response response = getResponseWithInterceptorChain();
        // 如果onSuccess报错，将导致onFailure被执行
        callbacked = true;
        callback.onSuccess(RealCall.this, response);
      } catch (Exception e) {
        if (!callbacked) {
          callback.onFailure(RealCall.this, e);
        }
      } finally {
        client.getDispatcher().finished(this);
      }
    }
  }
}
