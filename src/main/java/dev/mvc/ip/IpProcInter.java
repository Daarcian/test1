package dev.mvc.ip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public interface IpProcInter {
  

  public Map<String, Object> getIpInfo(String connection_ip);
  
  public String when_ip_joinned(String ip, String member_id);
  
  public String send_CerificationEmail_to_member(String member_email);
  
  public int check_ip_exists(String ip_address);
  
//  public int check_member_connect_exists(HashMap<String, Object> map);
  
  public int create_member_connect(String member_id, String ip, String cerification, String connect_status);
  
  public ArrayList<IpVO> ip_search_paging(String word, Map map, int  now_page, int record_per_page);
  
  public String pagingBox(int now_page, Map map, String list_url, int search_count, int record_per_page,
      int page_per_block);
  
  public int count_JOIN_IP_RECORD(Map map);
}
