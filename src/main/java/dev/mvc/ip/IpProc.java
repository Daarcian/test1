package dev.mvc.ip;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Component("dev.mvc.ip.IpProc")
public class IpProc implements IpProcInter {

  private final RestTemplate restTemplate;

  public IpProc(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Autowired
  private IpDAOInter ipDAO;

  @Override
  public int create(IpVO ipVO) {
    /**
     * @Author : soldesk
     * @Date : 2025. 6. 12.
     * @Method : create
     * @return : return 0;
     */
    int cnt = ipDAO.create(ipVO);
    return cnt;
  }
  
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
  
  
  //사용자가 웹페이지에 처음 접속
  public void when_ip_joinned(String ip) {
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

  @SuppressWarnings("unchecked")
  public Map<String, Object> getIpInfo(String connection_ip) {
    String url = "http://ip-api.com/json/" + connection_ip
        + "?fields=status,country,countryCode,region,regionName,city,isp,mobile,query";

    // GET 요청하여 Map으로 응답 받기
    ResponseEntity<Map> responseEntity = restTemplate.getForEntity(url, Map.class);
    
    System.out.println("responseEntitiy : " + responseEntity);

    // 타입 안전을 위해 형변환
    return (Map<String, Object>) responseEntity.getBody();
  }

}
