package com.learn.more.connection;

import com.learn.more.Call;
import com.learn.more.EventListener;
import com.learn.more.OkHttpClient;

// 是Connection到Call的中间层
public class Transmitter {
  private final OkHttpClient client;
  private final Call call;
  private final EventListener listener;

  public Transmitter(OkHttpClient client, Call call) {
    this.client = client;
    this.call = call;
    this.listener = client.getEventListenerFactory().create(call);
  }

  public void callStart() {
    listener.callStart(call);
  }

}
