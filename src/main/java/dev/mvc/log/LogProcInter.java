package dev.mvc.log;

public interface LogProcInter {
  
  public void LoggedIn(LoginLogVO loginlogVO);
  
  public int LoggedOut(int login_log_id);

}
