package com.learn.more;

public interface Callback {
  void onFailure(Call call, Exception e);

  void onSuccess(Call call, Response response);
}
