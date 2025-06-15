package dev.mvc.ip;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class IpConnectVO {
  
  private String ip_connect_id;
  
  private int ip_id;
  
  private String ip_connect_rdate;
  
  private String ip_is_connected;
  
  private String ip_connect_fail = "";
  
  
}
