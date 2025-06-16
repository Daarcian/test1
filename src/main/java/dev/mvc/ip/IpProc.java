package dev.mvc.ip;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component("dev.mvc.ip.IpProc")
public class IpProc implements IpProcInter {

  // 접속 제한 시간 (분 단위)
  private static final int limit_time = 10;

  // 제한 시간 내 접속 허용 횟수
  private static final int limit_count = 5;

  private final RestTemplate restTemplate;
  private final EmailCerification emailCerification;

  public IpProc(RestTemplate restTemplate, EmailCerification emailCerification) {
    this.restTemplate = restTemplate;
    this.emailCerification = emailCerification;
  }

  @Autowired
  private IpDAOInter ipDAO;

  /**
   * IP 차단 여부 확인
   */
  public boolean checkIpBlocked(Map map) {
    Object blocked = map.get("ip_is_block");
    if ((Boolean) blocked == true) {
      return true;
    } else if ((Boolean) blocked == false) {
      return false;
    } else {
      System.out.println("dev-mvc-ip IpProc 오류 발생");
      return true;
    }
  }

  /**
   * 모바일 접속 여부 확인
   */
  public boolean checkIpMobile(Map map) {
    Object mobile = map.get("ip_is_mobile");
    if ((Boolean) mobile == true) {
      return true;
    } else if ((Boolean) mobile == false) {
      return false;
    } else {
      System.out.println("dev-mvc-ip IpProc 오류 발생");
      return true;
    }
  }

  /**
   * 사용자가 접속할 때 IP 관련 처리
   */
  @Override
  public String when_ip_joinned(String ip, String member_id) {
    ip = "121.78.128.113";
    int check_ip_is_exists = this.ipDAO.check_ip_exists(ip);

    if (check_ip_is_exists == 1) {
      // 접속 이력이 있는 IP
      int ip_id = this.ipDAO.ip_address_to_ip_id(ip);
      return when_ip_not_first_joinned(ip_id, member_id);
    } else if (check_ip_is_exists == 0) {
      // 처음 접속하는 IP
      return when_ip_first_joinned(ip);
    } else {
      return "ERROR";
    }
  }

  /**
   * 이전에 접속한 적 있는 IP 처리
   */
  public String when_ip_not_first_joinned(int ip_id, String member_id) {
    String ip_block = this.ipDAO.ip_is_block(ip_id);

    if (ip_block.toUpperCase() == "Y" || ip_block.toUpperCase().equals("Y")) {
      this.ip_connect_failed(ip_id, "아이피 블락 상태");
      return "BLOCK";
    }

    System.out.println("passed------");
    HashMap<String, Object> map = new HashMap<>();
    map.put("ip_id", ip_id);
    map.put("limit_time", limit_time);

    if (this.ipDAO.count_ip_connect_in_limit_time(map) >= limit_count) {
      System.out.println("passed------");
      this.ipDAO.update_ip_enable_to_blocked(ip_id);
      this.ip_connect_failed(ip_id, "다중 접속 의심");
      return "BLOCK";
    }

    HashMap<String, Object> check_map = new HashMap<>();
    check_map.put("ip_id", ip_id);
    check_map.put("member_uuid", this.ipDAO.member_id_to_member_uuid(member_id));

    int check_member_connect_exists = this.ipDAO.check_member_connect_exists(check_map);

    if (check_member_connect_exists == 1) {
      this.ip_connect_success(ip_id);
      return "PASS";
    }

    if (check_member_connect_exists == 0) {
      this.ip_connect_failed(ip_id, "인증 필요");
      return "EMAIL";
    }

    return "ERROR";
  }

  /**
   * 처음 접속한 IP 처리
   */
  public String when_ip_first_joinned(String ip) {
    IpVO ipVO = new IpVO();
    Map<String, Object> map = getIpInfo(ip);
    ipVO.setIp_address((String) map.get("query"));
    ipVO.setIp_country_name((String) map.get("country"));
    ipVO.setIp_country_code((String) map.get("countryCode"));
    ipVO.setIp_region_name((String) map.get("regionName"));
    ipVO.setIp_region_code((String) map.get("region"));
    ipVO.setIp_city_name((String) map.get("city"));
    ipVO.setIp_isp((String) map.get("isp"));

    System.out.println("ipVO : " + ipVO.toString());

    ipDAO.create(ipVO);

    return "EMAIL";
  }

  /**
   * IP 접속 성공 로그 기록
   */
  public void ip_connect_success(int ip_id) {
    IpConnectVO ipconnectVO = new IpConnectVO();
    ipconnectVO.setIp_id(ip_id);
    ipconnectVO.setIp_is_connected("Y");
    this.ipDAO.ip_connect_log_create(ipconnectVO);
  }

  /**
   * IP 접속 실패 로그 기록
   */
  public void ip_connect_failed(int ip_id, String fail_reason) {
    IpConnectVO ipconnectVO = new IpConnectVO();
    ipconnectVO.setIp_id(ip_id);
    ipconnectVO.setIp_connect_fail(fail_reason);
    ipconnectVO.setIp_is_connected("N");
    this.ipDAO.ip_connect_log_create(ipconnectVO);
  }

  /**
   * 인증 이메일 전송
   */
  
  @Override
  public String send_CerificationEmail_to_member(String member_email) {
    String code = emailCerification.generateAuthCode();
    this.emailCerification.sendVerificationEmail(member_email, code);
    return code;
  }

  /**
   * 외부 API를 통해 IP 정보 가져오기
   */
  @SuppressWarnings("unchecked")
  @Override
  public Map<String, Object> getIpInfo(String connection_ip) {
    String url = "http://ip-api.com/json/" + connection_ip
        + "?fields=status,country,countryCode,region,regionName,city,isp,mobile,query";
    ResponseEntity<Map> responseEntity = restTemplate.getForEntity(url, Map.class);
    System.out.println("responseEntitiy : " + responseEntity);
    return (Map<String, Object>) responseEntity.getBody();
  }

  /**
   * 해당 IP 존재 여부 확인
   */
  @Override
  public int check_ip_exists(String ip_address) {
    int cnt = this.ipDAO.check_ip_exists(ip_address);
    return cnt;
  }

  /**
   * 사용자의 접속 기록 생성
   */
  @Override
  public int create_member_connect(String member_id, String ip_address, String cerification, String connect_status) {
    MemberConnectVO memberConnectVO = new MemberConnectVO();
    memberConnectVO.setMember_uuid(this.ipDAO.member_id_to_member_uuid(member_id));
    memberConnectVO.setIp_id(this.ipDAO.ip_address_to_ip_id(ip_address));
    memberConnectVO.setCertification(cerification);
    memberConnectVO.setMember_connect_status(connect_status);
    int cnt = this.ipDAO.create_member_connect(memberConnectVO);
    return cnt;
  }
}
