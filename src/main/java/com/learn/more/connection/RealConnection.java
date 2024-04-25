package com.learn.more.connection;

import com.learn.more.Address;
import com.learn.more.Connection;
import com.learn.more.http.HttpUrl;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 持有底层Socket封装，并维护复用
 *
 * todo 如何判断一个连接是空闲还是忙碌
 */
public class RealConnection implements Connection {

  // 不能被使用了，用于清理前标记，防止被标记后再次被使用
  private volatile boolean noCarryTransmitter = false;
  // 因为已经知道Socket不可并发使用，因此用一个Transmitter引用而非数组也可以。
  private List<Transmitter> transmitters = new ArrayList<>();
  private int allocationLimit = 1;

  // 底层socket
  Socket socket;
  // 目标地址
  private final Address address;

  public RealConnection(int connectTimeout, int readTimeout, int writeTimeout, Address address) throws IOException {
    this.address = address;
    this.socket = connect(connectTimeout, readTimeout, writeTimeout);
  }

  public boolean idle() {
    return transmitters.isEmpty();
  }

  public void noCarryTransmitter() {
    noCarryTransmitter = true;
  }

  // 是否可用于某个请求
  public boolean isHealthy() {
    return true;
  }

  public boolean isEligible(Address address) {
    if (noCarryTransmitter || transmitters.size() > allocationLimit) {
      return false;
    }
    // todo 目标主机相同
    return true;
  }

  public void addTransmitter(Transmitter transmitter) {
    transmitters.add(transmitter);
  }

  // todo 未考虑代理、ipv4或ipv6、http2等
  private Socket connect(int connectTimeout, int readTimeout, int writeTimeout) throws IOException {
    Socket socket;
    Proxy proxy = address.proxy();
    HttpUrl url = address.url();
    if (Objects.nonNull(proxy)) {
      socket = new Socket(proxy);
    } else {
      socket = new Socket(url.getHost(), url.getPort());
    }
    InetAddress inetAddress = InetAddress.getByName(url.getHost());
    socket.connect(new InetSocketAddress(inetAddress, url.getPort()), connectTimeout);
    socket.setSoTimeout(readTimeout);
    return socket;
  }
}
