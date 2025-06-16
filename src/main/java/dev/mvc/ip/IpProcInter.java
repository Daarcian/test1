package dev.mvc.ip;

import java.util.HashMap;
import java.util.Map;

public interface IpProcInter {
  

  public Map<String, Object> getIpInfo(String connection_ip);
  
  public String when_ip_joinned(String ip, String member_id);
  
  public String send_CerificationEmail_to_member(String member_email);
  
  public int check_ip_exists(String ip_address);
  
//  public int check_member_connect_exists(HashMap<String, Object> map);
  
  public int create_member_connect(String member_id, String ip, String cerification, String connect_status);
}
