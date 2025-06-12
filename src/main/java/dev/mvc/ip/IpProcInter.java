package dev.mvc.ip;

import java.util.Map;

public interface IpProcInter {
  
  public int create(IpVO ipVO);

  public Map<String, Object> getIpInfo(String connection_ip);
  
  public void when_ip_joinned(String ip);
}
