package com.learn.more;

public abstract class EventListener {
  public static final EventListener NONE = new EventListener() {
  };

  static EventListener.Factory factory(EventListener listener) {
    return call -> listener;
  }

  public void callStart(Call call) {

  }

  public void connectStart(Call call) {

  }

  public void connectionAcquired(Call call, Connection connection) {
  }

  public void connectionReleased(Call call, Connection connection) {
  }

  public void connectEnd(Call call) {

  }

  public void callEnd(Call call) {

  }

  public interface Factory {
    EventListener create(Call call);
  }
}
