package com.learn.more;

import com.learn.more.OkHttpClient.Builder;
import com.learn.more.http.Headers;
import com.learn.more.http.HttpUrl;
import com.learn.more.http.HttpUrl.SchemeEnum;
import com.learn.more.http.RequestBody;

public class MainTest {

  public static void main(String[] args) throws Exception {
    OkHttpClient client = new Builder().build();
    Request request = new Request(new HttpUrl(SchemeEnum.http.name(), "localhost", 80, "/find"), "POST", new Headers(), new RequestBody());
    //    Response response = client.newCall(request).execute();
    client.newCall(request).asyncExecute(new Callback() {
      @Override
      public void onFailure(Call call, Exception e) {
        System.out.println("failure");
      }

      @Override
      public void onSuccess(Call call, Response response) {
        System.out.println("success");
      }
    });

  }
}
