package dev.mvc.tool;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import dev.mvc.member.MemberProcInter;

@Component
public class SessionTimeoutListener implements HttpSessionListener {
  
  @Autowired
  @Qualifier("dev.mvc.member.MemberProc")  // @Service("dev.mvc.member.MemberProc")
  private MemberProcInter memberProc;

    @Override
    public void sessionCreated(HttpSessionEvent se) {
       System.out.println("세션 생성됨: " + se.getSession().getId());
        
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
      memberProc.session_id_delete((int)se.getSession().getAttribute("memberno"));
      System.out.println("세션 만료됨: " + se.getSession().getId());
    }
}
