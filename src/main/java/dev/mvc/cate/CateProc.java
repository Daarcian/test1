package dev.mvc.cate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// Service, Process, Manager: DAO 호출 및 알고리즘 구현
@Service("dev.mvc.cate.CateProc")
public class CateProc implements CateProcInter {
  @Autowired
  private CateDAOInter cateDAO;
  
  @Override
  public int create(CateVO cateVO) {
    int cnt = this.cateDAO.create(cateVO);
    return cnt;
  }

  @Override
  public ArrayList<CateVO> list_all() {
    ArrayList<CateVO> list = this.cateDAO.list_all();
    return list;
  }

  @Override
  public CateVO read(int cateno) {
    CateVO cateVO = this.cateDAO.read(cateno);
    return cateVO;
  }

  @Override
  public int update(CateVO cateVO) {
    int cnt = this.cateDAO.update(cateVO);
    return cnt;
  }

  @Override
  public int delete(int cateno) {
    int cnt = this.cateDAO.delete(cateno);
    return cnt;
  }

  @Override
  public int update_seqno_forward(int cateno) {
    int cnt = this.cateDAO.update_seqno_forward(cateno);
    return cnt;
  }

  @Override
  public int update_seqno_backward(int cateno) {
    int cnt = this.cateDAO.update_seqno_backward(cateno);
    return cnt;
  }

  @Override
  public int update_visible_y(int cateno) {
    int cnt = this.cateDAO.update_visible_y(cateno);
    return cnt;
  }

  @Override
  public int update_visible_n(int cateno) {
    int cnt = this.cateDAO.update_visible_n(cateno);
    return cnt;
  }

  @Override
  public ArrayList<CateVO> list_all_grp_y() {
    ArrayList<CateVO> list = this.cateDAO.list_all_grp_y();
    return list;
  }

  @Override
  public ArrayList<CateVO> list_all_name_y(String grp) {
    ArrayList<CateVO> list = this.cateDAO.list_all_name_y(grp);
    return list;
  }

  @Override
  public ArrayList<CateVOMenu> menu() {
    ArrayList<CateVOMenu> menu = new ArrayList<CateVOMenu>();
    ArrayList<CateVO> grps = this.cateDAO.list_all_grp_y(); // 공개된 대분류 목록
    
    for(CateVO cateVO: grps) {
      CateVOMenu cateVOMenu = new CateVOMenu();
      cateVOMenu.setGrp(cateVO.getGrp()); // 대분류 이름 저장
      
      // 특정 대분류에 해당하는 공개된 중분류 추출
      ArrayList<CateVO> list_name = this.cateDAO.list_all_name_y(cateVO.getGrp());
      cateVOMenu.setList_name(list_name);
      
      menu.add(cateVOMenu); // 하나의 그룹에 해당하는 중분류 메뉴 객체 저장
    }
    
    return menu;
  }

  @Override
  public ArrayList<String> grpset() {
    ArrayList<String> grpset = this.cateDAO.grpset();
    return grpset;
  }

  @Override
  public ArrayList<CateVO> list_search(String word) {
    ArrayList<CateVO> list = this.cateDAO.list_search(word);
    return list;
  }

  @Override
  public int list_search_count(String word) {
    int cnt = this.cateDAO.list_search_count(word);
    return cnt;
  }

  @Override
  /**
   * 검색 + 페이징 목록
   * select id="list_search_paging" resultType="dev.mvc.cate.CateVO" parameterType="Map" 
   * @param word 검색어
   * @param now_page 현재 페이지, 시작 페이지 번호: 1 ★
   * @param record_per_page 페이지당 출력할 레코드 수
   * @return
   */
  public ArrayList<CateVO> list_search_paging(String word, int now_page, int record_per_page) {

    int start_num = ((now_page - 1) * record_per_page) + 1;
    int end_num=(start_num + record_per_page) - 1;

    // System.out.println("WHERE r >= "+start_num+" AND r <= " + end_num);
    
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("word", word);
    map.put("start_num", start_num);
    map.put("end_num", end_num);
    
    ArrayList<CateVO> list = this.cateDAO.list_search_paging(map);
    // System.out.println("-> " + list.size());
    
    return list;
  }

  /** 
   * SPAN태그를 이용한 박스 모델의 지원, 1 페이지부터 시작 
   * 현재 페이지: 11 / 22   [이전] 11 12 13 14 15 16 17 18 19 20 [다음] 
   *
   * @param now_page  현재 페이지
   * @param word 검색어
   * @param list_url 목록 파일명, @GetMapping(value="/list_search")
   * @param search_count 검색 레코드수   
   * @param record_per_page 페이지당 레코드 수
   * @param page_per_block 블럭당 페이지 수
   * @return 페이징 생성 문자열
   */ 
  @Override
  public String pagingBox(int now_page, String word, String list_url, int search_count, 
                                      int record_per_page, int page_per_block){    
    // 전체 페이지 수: (double)1/10 -> 0.1 -> 1 페이지, (double)12/10 -> 1.2 페이지 -> 2 페이지
    int total_page = (int)(Math.ceil((double)search_count / record_per_page));
    
    // 전체 그룹  수: (double)1/10 -> 0.1 -> 1 그룹, (double)12/10 -> 1.2 그룹-> 2 그룹
    int total_grp = (int)(Math.ceil((double)total_page / page_per_block)); 
    
    // 현재 그룹 번호: (double)13/10 -> 1.3 -> 2 그룹
    int now_grp = (int)(Math.ceil((double)now_page / page_per_block));  
    
    // 1 group: 1, 2, 3 ... 9, 10
    // 2 group: 11, 12 ... 19, 20
    // 3 group: 21, 22 ... 29, 30
    int start_page = ((now_grp - 1) * page_per_block) + 1; // 특정 그룹의 시작 페이지  
    int end_page = (now_grp * page_per_block);               // 특정 그룹의 마지막 페이지   
     
    StringBuffer str = new StringBuffer(); // String class 보다 문자열 추가등의 편집시 속도가 빠름 
    str.append("<div id='paging'>"); 
//    str.append("현재 페이지: " + nowPage + " / " + totalPage + "  "); 
 
    // 이전 10개 페이지로 이동
    // now_grp: 1 (1 ~ 10 page)
    // now_grp: 2 (11 ~ 20 page)
    // now_grp: 3 (21 ~ 30 page) 
    // 현재 2그룹일 경우: (2 - 1) * 10 = 1그룹의 마지막 페이지 10
    // 현재 3그룹일 경우: (3 - 1) * 10 = 2그룹의 마지막 페이지 20
    int _now_page = (now_grp - 1) * page_per_block;  
    if (now_grp >= 2){ // 현재 그룹번호가 2이상이면 페이지수가 11페이지 이상임으로 이전 그룹으로 갈수 있는 링크 생성 
      str.append("<span class='span_box_1'><a href='"+list_url+"?&word="+word+"&now_page="+_now_page+"'>이전</a></span>"); 
    } 
 
    // 중앙의 페이지 목록
    for(int i=start_page; i<=end_page; i++){ 
      if (i > total_page){ // 마지막 페이지를 넘어갔다면 페이 출력 종료
        break; 
      } 
  
      if (now_page == i){ // 목록에 출력하는 페이지가 현재페이지와 같다면 CSS 강조(차별을 둠)
        str.append("<span class='span_box_2'>"+i+"</span>"); // 현재 페이지, 강조 
      }else{
        // 현재 페이지가 아닌 페이지는 이동이 가능하도록 링크를 설정
        str.append("<span class='span_box_1'><a href='"+list_url+"?word="+word+"&now_page="+i+"'>"+i+"</a></span>");   
      } 
    } 
 
    // 10개 다음 페이지로 이동
    // nowGrp: 1 (1 ~ 10 page),  nowGrp: 2 (11 ~ 20 page),  nowGrp: 3 (21 ~ 30 page) 
    // 현재 페이지 5일경우 -> 현재 1그룹: (1 * 10) + 1 = 2그룹의 시작페이지 11
    // 현재 페이지 15일경우 -> 현재 2그룹: (2 * 10) + 1 = 3그룹의 시작페이지 21
    // 현재 페이지 25일경우 -> 현재 3그룹: (3 * 10) + 1 = 4그룹의 시작페이지 31
    _now_page = (now_grp * page_per_block)+1; //  최대 페이지수 + 1 
    if (now_grp < total_grp){ 
      str.append("<span class='span_box_1'><a href='"+list_url+"?&word="+word+"&now_page="+_now_page+"'>다음</a></span>"); 
    } 
    str.append("</div>"); 
     
    return str.toString(); 
  }

  @Override
  public int update_cnt_by_cateno(Map<String, Object> map) {

    return 0;
  }


}

