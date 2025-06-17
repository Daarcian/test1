package dev.mvc.ip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import dev.mvc.cate.CateVO;

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

  @Override
  public ArrayList<IpVO> ip_search_paging(String word, Map map, int now_page, int record_per_page) {
    
    int start_num = ((now_page - 1) * record_per_page) + 1;
    int end_num = (start_num + record_per_page) - 1;
    
    map.put("word", word);
    map.put("start_num", start_num);
    map.put("end_num", end_num);
    

    ArrayList<IpVO> list = this.ipDAO.ip_search_paging(map);

    return list;
  }

  @Override
  public String pagingBox(int now_page, Map map, String list_url, int search_count, int record_per_page,
      int page_per_block) {
    
    String params = 
        "&word=" + map.get("word") +
        "&ip_address=" + map.get("ip_address") +
        "&ip_country_name=" + map.get("ip_country_name") +
        "&ip_country_code=" + map.get("ip_country_code") +
        "&ip_region_name=" + map.get("ip_region_name") +
        "&ip_region_code=" + map.get("ip_region_code") +
        "&ip_city_name=" + map.get("ip_city_name") +
        "&ip_isp=" + map.get("ip_isp") +
        "&ip_is_block=" + map.get("ip_is_block") +
        "&ip_is_mobile=" + map.get("ip_is_mobile") +
        "&rdate_start=" + map.get("rdate_start") +
        "&rdate_end=" + map.get("rdate_end") +
        "&mdate_start=" + map.get("mdate_start") +
        "&mdate_end=" + map.get("mdate_end");
    
    int total_page = (int) (Math.ceil((double) search_count / record_per_page));

    int total_grp = (int) (Math.ceil((double) total_page / page_per_block));

    int now_grp = (int) (Math.ceil((double) now_page / page_per_block));

    int start_page = ((now_grp - 1) * page_per_block) + 1; // 특정 그룹의 시작 페이지
    int end_page = (now_grp * page_per_block); // 특정 그룹의 마지막 페이지

    StringBuffer str = new StringBuffer(); // String class 보다 문자열 추가등의 편집시 속도가 빠름
    str.append("<div id='paging'>");

    int _now_page = (now_grp - 1) * page_per_block;
    if (now_grp >= 2) { // 현재 그룹번호가 2이상이면 페이지수가 11페이지 이상임으로 이전 그룹으로 갈수 있는 링크 생성
      str.append("<span class='span_box_1'><a href='" + list_url + "?" + params + "&now_page=" + _now_page
          + "'>이전</a></span>");
    }

    for (int i = start_page; i <= end_page; i++) {
      if (i > total_page) { // 마지막 페이지를 넘어갔다면 페이 출력 종료
        break;
      }

      if (now_page == i) { // 목록에 출력하는 페이지가 현재페이지와 같다면 CSS 강조(차별을 둠)
        str.append("<span class='span_box_2'>" + i + "</span>"); // 현재 페이지, 강조
      } else {

        str.append("<span class='span_box_1'><a href='" + list_url + "?" + params + "&now_page=" + i + "'>" + i
            + "</a></span>");
      }
    }

    if (now_grp < total_grp) {
      str.append("<span class='span_box_1'><a href='" + list_url + "?" + params + "&now_page=" + _now_page
          + "'>다음</a></span>");
    }
    str.append("</div>");

    return str.toString();
  }

  @Override
  public int count_JOIN_IP_RECORD(Map map) {
    /**
     * @Author : soldesk
     * @Date : 2025. 6. 17.
     * @Method : count_JOIN_IP_RECORD
     * @return : return 0;
     */
    int cnt = this.ipDAO.count_JOIN_IP_RECORD(map);
    return cnt;
  }

}
