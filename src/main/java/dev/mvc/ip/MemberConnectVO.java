package dev.mvc.ip;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter @Getter @ToString
public class MemberConnectVO {
  
  private int member_connect_id;
  
  private int member_uuid;
  
  private int ip_id;
  
  private String connection_date;
  
  private String cerification;
  
  private String member_connect_status;

}
