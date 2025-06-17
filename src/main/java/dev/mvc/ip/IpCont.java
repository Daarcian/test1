package dev.mvc.ip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/ip")
public class IpCont {

  @Autowired
  @Qualifier("dev.mvc.ip.IpProc")
  private IpProcInter ipProc;

  /** 페이지당 출력할 레코드 갯수, nowPage는 1부터 시작 */
  public int record_per_page = 7;

  /** 블럭당 페이지 수, 하나의 블럭은 10개의 페이지로 구성됨 */
  public int page_per_block = 10;

  /** 페이징 목록 주소, @GetMapping(value="/list_search") */
  private String list_url = "/ip/list_search";

  public IpCont() {
    System.out.println("-> IpCont created.");
  }

  @GetMapping(value = "/list_search")
  public String list_search_paging(HttpSession session, Model model,
      @RequestParam(name = "word", defaultValue = "") String word,
      @RequestParam(name = "ip_address", defaultValue = "") String ip_address,
      @RequestParam(name = "ip_country_name", defaultValue = "") String ip_country_name,
      @RequestParam(name = "ip_country_code", defaultValue = "") String ip_country_code,
      @RequestParam(name = "ip_region_name", defaultValue = "") String ip_region_name,
      @RequestParam(name = "ip_region_code", defaultValue = "") String ip_region_code,
      @RequestParam(name = "ip_city_name", defaultValue = "") String ip_city_name,
      @RequestParam(name = "ip_isp", defaultValue = "") String ip_isp,
      @RequestParam(name = "ip_is_block", defaultValue = "") String ip_is_block,
      @RequestParam(name = "ip_is_mobile", defaultValue = "") String ip_is_mobile,
      @RequestParam(name = "rdate_start", defaultValue = "") String rdate_start,
      @RequestParam(name = "rdate_end", defaultValue = "") String rdate_end,
      @RequestParam(name = "mdate_start", defaultValue = "") String mdate_start,
      @RequestParam(name = "mdate_end", defaultValue = "") String mdate_end,
      @RequestParam(name = "now_page", defaultValue = "1") int now_page) {
    
    Map<String, Object> map = new HashMap<>();
    map.put("ip_address", ip_address);
    map.put("ip_country_name", ip_country_name);
    map.put("ip_country_code", ip_country_code);
    map.put("ip_region_name", ip_region_name);
    map.put("ip_region_code", ip_region_code);
    map.put("ip_city_name", ip_city_name);
    map.put("ip_isp", ip_isp);
    map.put("ip_is_block", ip_is_block);
    map.put("ip_is_mobile", ip_is_mobile);  
    map.put("rdate_start", rdate_start);
    map.put("rdate_end", rdate_end);
    map.put("mdate_start", mdate_start);
    map.put("mdate_end", mdate_end);
    
    ArrayList<IpVO> list = this.ipProc.ip_search_paging(word, map , now_page, this.record_per_page);
    model.addAttribute("list", list);
    
    map.put("word", word);
    
    int search_count = this.ipProc.count_JOIN_IP_RECORD(map);
    
    String paging = this.ipProc.pagingBox(now_page, map, mdate_end, search_count, now_page, this.page_per_block);
    model.addAttribute("paging", paging);
    model.addAttribute("now_page", now_page);
    
    int no = search_count - ((now_page - 1) * this.record_per_page);
    model.addAttribute("no", no);
    
   
    return "ip/JOIN_IP_list_search";
    

  }

}
