package dev.mvc.ip;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter 
@Setter 
@ToString 
@AllArgsConstructor 
@NoArgsConstructor
public class IpVO {

  private int ip_id = 0;

  // 접속자의 실제 IP (후보키)
  private String ip_address;

  // 접속 아이피 국가명
  private String ip_country_name;

  // 접속 아이피 국가 코드
  private String ip_country_code;

  // 접속자의 지역명
  private String ip_region_name;

  // 접속자의 지역 코드
  private String ip_region_code;

  // 접속자의 도시명
  private String ip_city_name;

  // 접속자의 ISP(LG U+, KT...등)
  private String ip_isp;

  // 아이피 생성일 (등록일)
  private String ip_rdate = "";

  // 아이피 수정일 (변경일)
  private String ip_mdate = "";

  // 차단 여부 ('Y'/'N')
  private String ip_is_block = "";

  // 모바일 여부 ('Y'/'N')
  private String ip_is_mobile = "";

}

