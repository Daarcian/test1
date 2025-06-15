package dev.mvc.ip;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component("dev.mvc.ip.IpProc")
public class IpProc implements IpProcInter {
  
  
   /**
    * @Author : soldesk
    * @Date : 2025. 6. 13.
    * @Field : limit_time
    * @use :  해당 IP에 대해 limit_time(분 단위) 동안 limit_count 보다 더 많이
    *            더 많이 접속 했는지 확인 하기 위한 변수
   */
  private static final int limit_time = 10;
  
   /**
    * @Author : soldesk
    * @Date : 2025. 6. 13.
    * @Field : limit_count
    * @use :  해당 IP에 대해 limit_time(분 단위) 동안 limit_count 보다 더 많이
    *            더 많이 접속 했는지 확인 하기 위한 변수
   */
  private static final int limit_count = 5;
  
  
  

  private final RestTemplate restTemplate;

  public IpProc(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Autowired
  private IpDAOInter ipDAO;
  
  
  public boolean checkIpBlocked(Map map) {
    Object blocked = map.get("ip_is_block");
    
    if ((Boolean) blocked == true) {
      return true;
    }
    
    else if ((Boolean) blocked == false) {
      return false;
    }
    
    else {
      System.out.println("dev-mvc-ip IpProc 오류 발생");
      return true;
    }
  }
  
  public boolean checkIpMobile(Map map) {
    Object mobile = map.get("ip_is_mobile");
    
    if ((Boolean) mobile == true) {
      return true;
    }
    
    else if ((Boolean) mobile == false) {
      return false;
    }
    
    else {
      System.out.println("dev-mvc-ip IpProc 오류 발생");
      return true;
    }
  }
  
   /**
    * @Author : 김두교
    * @Date : 2025. 6. 13.
    * @Method : when_ip_joinned
    * @param : ip(String)
    * @use : 사용자가 접속 할 때 아이피에 대한 처리를 담당
    * @return : return 0;
   */
  @Override
  public boolean when_ip_joinned(String ip) {
    
    ip = "121.78.128.112";
    int check_ip_is_exists = this.ipDAO.check_ip_exists(ip);
    System.out.println("check_ip_is_exists : " +  check_ip_is_exists);
    
    if(check_ip_is_exists == 1) {
      //해결 필요 
      int ip_id = this.ipDAO.ip_address_to_ip_id("121.78.128.112");
      System.out.println("ip_id : " + ip_id);
      if(when_ip_not_first_joinned(ip_id)) {
        return true;
      }
      
      else {
        return false;
      }
      
      
    }
    
    else if (check_ip_is_exists == 0) {
      when_ip_first_joinned(ip);
    }
    
    return true;
  }
  

   /**
    * @Author : 김두교
    * @Date : 2025. 6. 13.
    * @Method : when_ip_not_first_joinned
    * @use :  해당 아이피가 처음 접속 하지 않은 상태 일 때
    * @return : 블락 처리 되어 있거나, 다중 접속 상태 의심 시 true 리턴, 다른 경우 false 리턴;
   */
  public boolean when_ip_not_first_joinned(int ip_id) {
    String ip_block = this.ipDAO.ip_is_block(ip_id);
    
    // 해당 아이피가 블락 되어 있는지 확인
    if(ip_block.toUpperCase() == "Y" || ip_block.toUpperCase().equals("Y")) {
      this.ip_connect_failed(ip_id, "아이피 블락 상태");
      return true;
    }
    System.out.println("passed------");
    HashMap<String, Object> map = new HashMap<>();
    map.put("ip_id", ip_id);
    map.put("limit_time", limit_time);
    
    // 해당 아이피가 limit_time(분 단위) 동안 limit_count 이상 접속 하였는지 확인
    if(this.ipDAO.count_ip_connect_in_limit_time(map) >= limit_count) {
      System.out.println("passed------");
      this.ipDAO.update_ip_enable_to_blocked(ip_id);
      this.ip_connect_failed(ip_id, "다중 접속 의심");
      return true;
    }
    
    
    
    
    
    else {
      this.ip_connect_success(ip_id);
      
      return false;
    }
  } 
  
  
  //사용자가 웹페이지에 처음 접속
  public void when_ip_first_joinned(String ip) {
    IpVO ipVO = new IpVO();
    Map<String, Object> map = getIpInfo(ip);
    ipVO.setIp_address((String)map.get("query"));
    ipVO.setIp_country_name((String)map.get("country"));
    ipVO.setIp_country_code((String)map.get("countryCode"));
    ipVO.setIp_region_name((String)map.get("regionName"));
    ipVO.setIp_region_code((String)map.get("region"));
    ipVO.setIp_city_name((String)map.get("city"));
    ipVO.setIp_isp((String)map.get("isp"));
    
    System.out.println(ipVO.toString());
    
    ipDAO.create(ipVO);
  }
  
   /**
    * @Author : 김두교
    * @Date : 2025. 6. 13.
    * @Method : ip_connect_success
    * @param  : ip_id(int)
    * @use :  해당 아이피가 block이 되어 있지 않고 다중 접속 상태가 아닐 때 사용
    * @return :
   */
  public void ip_connect_success(int ip_id) {
    IpConnectVO ipconnectVO = new IpConnectVO();
    ipconnectVO.setIp_id(ip_id);
    ipconnectVO.setIp_is_connected("Y");
    this.ipDAO.ip_connect_log_create(ipconnectVO);
  }
  
   /**
    * @Author : 김두교
    * @Date : 2025. 6. 13.
    * @Method : ip_connect_failed
    * @param  : ip_id(int), 실패 이유(String)
    * @use :  해당 아이피가 block 되어 있거나 다중 접속 상태 일 때 사용
    * @return : 
   */
  public void ip_connect_failed(int ip_id, String fail_reason) {
    IpConnectVO ipconnectVO = new IpConnectVO();
    ipconnectVO.setIp_id(ip_id);
    ipconnectVO.setIp_connect_fail(fail_reason);
    ipconnectVO.setIp_is_connected("N");
    this.ipDAO.ip_connect_log_create(ipconnectVO);
  }

  @SuppressWarnings("unchecked")
  @Override
  public Map<String, Object> getIpInfo(String connection_ip) {
    String url = "http://ip-api.com/json/" + "121.78.128.112"
        + "?fields=status,country,countryCode,region,regionName,city,isp,mobile,query";

    // GET 요청하여 Map으로 응답 받기
    ResponseEntity<Map> responseEntity = restTemplate.getForEntity(url, Map.class);
    
    System.out.println("responseEntitiy : " + responseEntity);

    // 타입 안전을 위해 형변환
    return (Map<String, Object>) responseEntity.getBody();
  }





}
