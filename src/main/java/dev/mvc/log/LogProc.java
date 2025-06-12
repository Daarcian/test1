package dev.mvc.log;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dev.mvc.member.MemberDAOInter;

@Component("dev.mvc.log.LogProc")
public class LogProc implements LogProcInter {
  
  @Autowired
  private LogDAOInter logDAOInter;

  @Override
  public void LoggedIn(LoginLogVO loginlogVO) {
    
    logDAOInter.LoggedIn(loginlogVO);
  }

  @Override
  public int LoggedOut(int login_log_id) {
    
    int cnt = logDAOInter.LoggedOut(login_log_id);
    return cnt;
  }
  
  

}
