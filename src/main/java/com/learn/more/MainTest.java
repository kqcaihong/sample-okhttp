package com.learn.more;

import com.learn.more.connection.ConnectionInterceptor;
import com.learn.more.http.CallServerInterceptor;
import com.learn.more.http.RealInterceptorChain;
import com.learn.more.http.RetryAndFollowUpInterceptor;
import java.util.ArrayList;
import java.util.List;

public class MainTest {

  public static void main(String[] args) throws Exception {
    List<Interceptor> interceptors = new ArrayList<>();
    interceptors.add(new RetryAndFollowUpInterceptor());
    interceptors.add(new ConnectionInterceptor());
    interceptors.add(new CallServerInterceptor());
    RealInterceptorChain chain = new RealInterceptorChain(interceptors, new Request(), new RealCall(), 0);
    Response response = chain.proceed(chain.request());
  }

}
