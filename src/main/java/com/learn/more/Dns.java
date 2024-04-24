package com.learn.more;

import java.net.InetAddress;
import java.util.List;

public interface Dns {
  List<InetAddress> lookup(String hostname);
}
