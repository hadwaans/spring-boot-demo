package ca.sc.hc.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

@RestController
@Slf4j
public class AppController {

  private final String LOCALHOST_IPV4 = "127.0.0.1";
  private final String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";

  @GetMapping("ip/v1") public String getIpAddressV1(HttpServletRequest request)
      throws UnknownHostException {
    String ipAddress = "";
    log.info(request.getRemoteAddr());
    log.info("X-FORWARDED-FOR :: " + request.getHeader("X-FORWARDED-FOR"));
    log.info("X-Real-IP :: " + request.getHeader("X-Real-IP"));
    log.info(InetAddress.getLocalHost().getHostAddress());
    ipAddress = InetAddress.getLocalHost().getHostAddress();
    return ipAddress;
  }

  @GetMapping("ip/v2") public String getIpAddressV2(HttpServletRequest request)
      throws UnknownHostException
    {
      String ipAddress = request.getHeader("X-Forwarded-For");
      if (StringUtils.isEmpty(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
        ipAddress = request.getHeader("Proxy-Client-IP");
      }

      if (StringUtils.isEmpty(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
        ipAddress = request.getHeader("WL-Proxy-Client-IP");
      }

      if (StringUtils.isEmpty(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
        ipAddress = request.getRemoteAddr();
        if (LOCALHOST_IPV4.equals(ipAddress) || LOCALHOST_IPV6.equals(ipAddress)) {
          try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            ipAddress = inetAddress.getHostAddress();
          } catch (UnknownHostException e) {
            e.printStackTrace();
          }
        }
      }

      if (!StringUtils.isEmpty(ipAddress) && ipAddress.length() > 15 && ipAddress.indexOf(",") > 0) {
        ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
      }
      return ipAddress;
    }

    @GetMapping("ip/v3")
    public String getIpAddressV3(HttpServletRequest request){
      System.out.println("UDP connection IP lookup: " + getLocalIpAddressUdp());
      System.out.println("Socket connection IP lookup: " + getLocalIpAddressSocket());
      System.out.println("AWS connection IP lookup: " + getPublicIpAddressAws());
      return "check console";
    }

  public static String getLocalIpAddressUdp() {
    try (final DatagramSocket datagramSocket = new DatagramSocket()) {
      datagramSocket.connect(InetAddress.getByName("8.8.8.8"), 12345);
      return datagramSocket.getLocalAddress().getHostAddress();
    } catch (SocketException | UnknownHostException exception) {
      throw new RuntimeException(exception);
    }
  }

  public static String getLocalIpAddressSocket() {
    try (Socket socket = new Socket()) {
      socket.connect(new InetSocketAddress("google.com", 80));
      return socket.getLocalAddress().getHostAddress();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static String getPublicIpAddressAws() {
    try {
      String urlString = "http://checkip.amazonaws.com/";
      URL url = new URL(urlString);
      try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
        return br.readLine();
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
