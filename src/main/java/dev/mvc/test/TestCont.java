package dev.mvc.test;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/testhtml")
@Controller
public class TestCont {
  
  public TestCont() {
    System.out.println("-> TestCont created.");
  }
  
  @GetMapping(value = "/test1") // http://localhost:9091/member/checkID?id=admin
  public String test1() {

    return "test/test1";
  }
  
  @GetMapping(value = "/test2") // http://localhost:9091/member/checkID?id=admin
  public String test2() {

    return "test/test2";
  }

}
