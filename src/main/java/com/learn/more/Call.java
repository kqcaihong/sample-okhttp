package com.learn.more;

public interface Call extends Cloneable {
  Request request();

  /**
   * 同步
   *
   * @return
   * @throws Exception
   */
  Response execute() throws Exception;

  void asyncExecute(Callback callback);

  void cancel();

  boolean canceled();


  Call clone();

  // 为调用方提供的工厂
  interface Factory {
    // 调用方只需提供request即可
    Call newCall(Request request);
  }
}
