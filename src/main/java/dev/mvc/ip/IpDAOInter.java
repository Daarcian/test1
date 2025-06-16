package dev.mvc.ip;

import java.util.HashMap;

public interface IpDAOInter {
  
  public int create(IpVO ipVO);
  
  public int check_ip_exists(String  ip);
  
  public String ip_is_block(int ip_id);
  
  public int ip_connect_log_create(IpConnectVO ipconnectVO);
  
  public int count_ip_connect_in_limit_time(HashMap<String, Object> map);
  
  public int ip_address_to_ip_id(String ip);
  
  public int update_ip_enable_to_blocked(int ip_id);
  
  public int create_member_connect(MemberConnectVO memberconnectVO);
  
  public int check_member_connect_exists(HashMap<String, Object> map);
  
  public int member_id_to_member_uuid(String member_id);

}
