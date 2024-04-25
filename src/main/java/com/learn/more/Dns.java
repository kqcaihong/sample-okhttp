package com.learn.more;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

@FunctionalInterface
public interface Dns {
  List<InetAddress> lookup(String hostname);

  Dns SYSTEM = hostname -> {
    try {
      return Arrays.asList(InetAddress.getAllByName(hostname));
    } catch (UnknownHostException e) {
      throw new RuntimeException(e);
    }
  };

}
