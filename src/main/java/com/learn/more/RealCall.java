package com.learn.more;

public class RealCall implements Call{

  @Override
  public Request request() {
    return null;
  }

  @Override
  public Response execute() throws Exception {
    return null;
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
