package ca.sc.hc.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

@RestController
@Slf4j
public class AppController {

  @GetMapping("ip-address")
  public String getIpAddress(HttpServletRequest request) throws UnknownHostException {
    String ipAddress = "";
    log.info(request.getRemoteAddr());
    log.info("X-FORWARDED-FOR :: " +request.getHeader("X-FORWARDED-FOR"));
    log.info("X-Real-IP :: " +request.getHeader("X-Real-IP"));
    log.info(InetAddress.getLocalHost().getHostAddress());
    log.info(Inet4Address.getLocalHost().getHostAddress());
    log.info(Inet6Address.getLocalHost().getHostAddress());
    ipAddress = InetAddress.getLocalHost().getHostAddress();
    return ipAddress;
  }
}
