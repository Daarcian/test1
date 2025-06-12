package dev.mvc.log;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LoginLogVO {
  
  int login_log_id;
  
  int memberno;
  
  String login_ip;
  
  String login_log_date = "";
  
  String logout_log_date = "";

}
