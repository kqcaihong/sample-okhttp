package com.learn.more;

import com.learn.more.connection.Transmitter;

public interface Call extends Cloneable {

  Request request();

  Transmitter transmitter();

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
