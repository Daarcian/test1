package dev.mvc.ip;

import java.util.Map;

public interface IpProcInter {
  

  public Map<String, Object> getIpInfo(String connection_ip);
  
  public boolean when_ip_joinned(String ip);
}
