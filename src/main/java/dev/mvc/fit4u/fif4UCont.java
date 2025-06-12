package dev.mvc.fit4u;

import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping(value = "/fit4u")
@Controller
public class fif4UCont {
  
  
  
  @GetMapping(value= "/send_message")
  public String send_message_form() {
    return "message/message_test1";
  }
  
  
  @PostMapping(value = "/send_message")
  @ResponseBody
  public String send_message(@RequestBody String json_src) {
    
    JSONObject src = new JSONObject(json_src);
    
    JSONObject response = new JSONObject();
    response.put("res", src.get("content") + " + responded");
    
    
    return response.toString();
    
  }

}
