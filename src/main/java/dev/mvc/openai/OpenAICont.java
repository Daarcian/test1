package dev.mvc.openai;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import dev.mvc.cate.CateProcInter;
import dev.mvc.cate.CateVOMenu;
import dev.mvc.member.MemberProcInter;
import dev.mvc.tool.LLMKey;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/openai")
public class OpenAICont {
  @Autowired
  @Qualifier("dev.mvc.member.MemberProc") // @Service("dev.mvc.member.MemberProc")
  private MemberProcInter memberProc;

  @Autowired // Spring이 CateProcInter를 구현한 CateProc 클래스의 객체를 생성하여 할당
  @Qualifier("dev.mvc.cate.CateProc")
  private CateProcInter cateProc;

  private final RestTemplate restTemplate;

  public OpenAICont(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
    System.out.println("-> OpenAICont created.");
    System.out.println("-> this.restTemplate hashCode: " + this.restTemplate.hashCode());
  }

  /**
   * 번역기
   * 
   * @return
   */
  @GetMapping(value = "/translator")
  public String translator(HttpSession session) {
    if (this.memberProc.isAdmin(session)) {
      return "openai/translator"; // /templates/openai/translator.html
    } else {
      return "redirect:/member/login_cookie_need?url=/openai/translator"; // redirect
    }

  }

  /**
   * 번역기
   * 
   * @return
   */
  @PostMapping(value = "/translator")
  @ResponseBody
  public String translator_proc(HttpSession session, @RequestBody String json_src) {
    System.out.println("-> json_src: " + json_src); // json_src: {"current_passwd":"1234"}
    JSONObject src = new JSONObject(json_src); // String -> JSON
    // String current_passwd = (String)src.get("current_passwd"); // 값 가져오기
    // System.out.println("-> current_passwd: " + current_passwd);

    if (this.memberProc.isAdmin(session)) {
      // FastAPI 서버 URL (포트는 환경에 맞게 조정)
      String url = "http://localhost:8000/translator";

      // HTTP 헤더 설정 (JSON)
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);

      // 요청 바디에 담을 데이터
      Map<String, Object> body = new HashMap<>();
      body.put("SpringBoot_FastAPI_KEY", new LLMKey().getSpringBoot_FastAPI_KEY());
      body.put("sentence", src.get("sentence"));
      body.put("language", src.get("language"));
      body.put("age", Integer.parseInt(src.get("age").toString()));

      // HttpEntity로 헤더 + 바디 묶기
      HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

      // POST 요청 보내고, 결과를 String으로 받기
      String response = restTemplate.postForObject(url, requestEntity, String.class);
      System.out.println("-> response: " + response);

      return response;
    } else {
      return "redirect:/member/login_cookie_need?url=/openai/translator"; // redirect
    }

  }

  /**
   * 영화추천
   * 
   * @return
   */
  @PostMapping(value = "/moviegood")
  @ResponseBody
  public String moviegood_proc(HttpSession session, @RequestBody String json_src) {
    System.out.println("-> json_src: " + json_src); // json_src: {"current_passwd":"1234"}
    JSONObject src = new JSONObject(json_src); // String -> JSON
    // String current_passwd = (String)src.get("current_passwd"); // 값 가져오기
    // System.out.println("-> current_passwd: " + current_passwd);

    if (this.memberProc.isAdmin(session)) {
      // FastAPI 서버 URL (포트는 환경에 맞게 조정)
      String url = "http://localhost:8000/moviegood";

      // HTTP 헤더 설정 (JSON)
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);

      // 요청 바디에 담을 데이터
      Map<String, Object> body = new HashMap<>();
      body.put("SpringBoot_FastAPI_KEY", new LLMKey().getSpringBoot_FastAPI_KEY());
      body.put("movie", src.get("movie"));
      System.out.println("movie data ==> " + src.get("movie"));

      // HttpEntity로 헤더 + 바디 묶기
      HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

      // POST 요청 보내고, 결과를 String으로 받기
      String response = restTemplate.postForObject(url, requestEntity, String.class);
      System.out.println("-> response: " + response);

      return response;
    } else {
      return "redirect:/member/login_cookie_need?url=/openai/moviegood"; // redirect
    }

  }

  /**
   * 영화 추천
   * 
   * @return
   */
  @GetMapping(value = "/moviegood")
  public String movie(HttpSession session, Model model) {
    if (this.memberProc.isAdmin(session)) {

      ArrayList<Integer> filenamelist = new ArrayList<>();
      for (int i = 1; i < 26; i++) {
        filenamelist.add(i);
      }

      model.addAttribute("filenames", filenamelist);

      return "openai/moviegood"; // /templates/openai/movie.html
    } else {
      return "redirect:/member/login_cookie_need?url=/openai/movie"; // redirect
    }

  }

  /**
   * 나의 관심 분야 알기
   * 
   * @return
   */
  @GetMapping(value = "/analyzemovie")
  public String genre(HttpSession session, Model model) {
    if (this.memberProc.isAdmin(session)) {

      ArrayList<Integer> filenamelist = new ArrayList<>();
      for (int i = 1; i < 26; i++) {
        filenamelist.add(i);
      }

      model.addAttribute("filenames", filenamelist);

      return "openai/movieAnalyze"; // /templates/openai/movie.html
    } else {
      return "redirect:/member/login_cookie_need?url=/openai/genre"; // redirect
    }

  }

  /**
   * 번역기
   * 
   * @return
   */
  @PostMapping(value = "/analyzemovie")
  @ResponseBody
  public String analyzemovie_proc(HttpSession session, @RequestBody String json_src) {
    System.out.println("-> json_src: " + json_src); // json_src: {"current_passwd":"1234"}
    JSONObject src = new JSONObject(json_src); // String -> JSON
    // String current_passwd = (String)src.get("current_passwd"); // 값 가져오기
    // System.out.println("-> current_passwd: " + current_passwd);

    if (this.memberProc.isAdmin(session)) {
      // FastAPI 서버 URL (포트는 환경에 맞게 조정)
      String url = "http://localhost:8000/analyzemovie";

      // HTTP 헤더 설정 (JSON)
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);

      // 요청 바디에 담을 데이터
      Map<String, Object> body = new HashMap<>();
      body.put("SpringBoot_FastAPI_KEY", new LLMKey().getSpringBoot_FastAPI_KEY());
      body.put("movie", src.get("movie"));

      // HttpEntity로 헤더 + 바디 묶기
      HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

      // POST 요청 보내고, 결과를 String으로 받기
      String response = restTemplate.postForObject(url, requestEntity, String.class);
      System.out.println("-> response: " + response);

      return response;
    } else {
      return "redirect:/member/login_cookie_need?url=/openai/moviegood"; // redirect
    }

  }

  @GetMapping(value = "/summary")
  public String summary(HttpSession session, Model model) {
    if (this.memberProc.isAdmin(session)) {

      ArrayList<CateVOMenu> menu = this.cateProc.menu();
      model.addAttribute("menu", menu);

      return "openai/summary"; // /templates/openai/movie.html
    } else {
      return "redirect:/member/login_cookie_need?url=/openai/summary"; // redirect
    }

  }

  @PostMapping(value = "/summary")
  @ResponseBody
  public String summary_proc(HttpSession session, @RequestBody String json_src) {
    System.out.println("-> json_src: " + json_src); // json_src: {"current_passwd":"1234"}
    JSONObject src = new JSONObject(json_src); // String -> JSON
    // String current_passwd = (String)src.get("current_passwd"); // 값 가져오기
    // System.out.println("-> current_passwd: " + current_passwd);

    if (this.memberProc.isAdmin(session)) {
      // FastAPI 서버 URL (포트는 환경에 맞게 조정)
      String url = "http://localhost:8000/summary";

      // HTTP 헤더 설정 (JSON)
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);

      // 요청 바디에 담을 데이터
      Map<String, Object> body = new HashMap<>();
      body.put("SpringBoot_FastAPI_KEY", new LLMKey().getSpringBoot_FastAPI_KEY());
      body.put("article", src.get("article"));

      // HttpEntity로 헤더 + 바디 묶기
      HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

      // POST 요청 보내고, 결과를 String으로 받기
      String response = restTemplate.postForObject(url, requestEntity, String.class);
      System.out.println("-> response: " + response);

      return response;
    } else {
      return "redirect:/member/login_cookie_need?url=/openai/summary"; // redirect
    }

  }

  @GetMapping(value = "/emotion")
  public String emtion(HttpSession session, Model model) {
    if (this.memberProc.isAdmin(session)) {

      ArrayList<CateVOMenu> menu = this.cateProc.menu();
      model.addAttribute("menu", menu);

      return "openai/emotion"; // /templates/openai/movie.html
    } else {
      return "redirect:/member/login_cookie_need?url=/openai/emtion"; // redirect
    }

  }

  @PostMapping(value = "/emotion")
  @ResponseBody
  public String emtion_proc(HttpSession session, @RequestBody String json_src) {
    System.out.println("-> json_src: " + json_src); // json_src: {"current_passwd":"1234"}
    JSONObject src = new JSONObject(json_src); // String -> JSON
    // String current_passwd = (String)src.get("current_passwd"); // 값 가져오기
    // System.out.println("-> current_passwd: " + current_passwd);

    if (this.memberProc.isAdmin(session)) {
      // FastAPI 서버 URL (포트는 환경에 맞게 조정)
      String url = "http://localhost:8000/emotion";

      // HTTP 헤더 설정 (JSON)
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);

      // 요청 바디에 담을 데이터
      Map<String, Object> body = new HashMap<>();
      body.put("SpringBoot_FastAPI_KEY", new LLMKey().getSpringBoot_FastAPI_KEY());
      body.put("article", src.get("article"));

      // HttpEntity로 헤더 + 바디 묶기
      HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

      // POST 요청 보내고, 결과를 String으로 받기
      String response = restTemplate.postForObject(url, requestEntity, String.class);
      System.out.println("-> response: " + response);

      return response;
    } else {
      return "redirect:/member/login_cookie_need?url=/openai/emtion"; // redirect
    }

  }

  @GetMapping(value = "/img_generate")
  public String img_generate(HttpSession session, Model model) {
    if (this.memberProc.isAdmin(session)) {

      ArrayList<CateVOMenu> menu = this.cateProc.menu();
      model.addAttribute("menu", menu);

      return "openai/img_generate"; // /templates/openai/movie.html
    } else {
      return "redirect:/member/login_cookie_need?url=/openai/img_generate"; // redirect
    }

  }

  @PostMapping(value = "/img_generate")
  @ResponseBody
  public String img_generate(HttpSession session, @RequestBody String json_src) {
    System.out.println("-> json_src: " + json_src); // json_src: {"current_passwd":"1234"}
    JSONObject src = new JSONObject(json_src); // String -> JSON
    // String current_passwd = (String)src.get("current_passwd"); // 값 가져오기
    // System.out.println("-> current_passwd: " + current_passwd);

    if (this.memberProc.isAdmin(session)) {
      // FastAPI 서버 URL (포트는 환경에 맞게 조정)
      System.out.println("img_generate passed");
      String url = "http://localhost:8000/img_generate";

      // HTTP 헤더 설정 (JSON)
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);

      // 요청 바디에 담을 데이터
      Map<String, Object> body = new HashMap<>();
      body.put("SpringBoot_FastAPI_KEY", new LLMKey().getSpringBoot_FastAPI_KEY());
      body.put("prompt", src.get("prompt"));

      // HttpEntity로 헤더 + 바디 묶기
      HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

      // POST 요청 보내고, 결과를 String으로 받기
      String response = restTemplate.postForObject(url, requestEntity, String.class);
      System.out.println("-> response: " + response);

      return response;
    } else {
      return "redirect:/member/login_cookie_need?url=/openai/img_generate"; // redirect
    }

  }

  @GetMapping(value = "/fileupload")
  public String fileupload(HttpSession session, Model model) {
    if (this.memberProc.isAdmin(session)) {

      ArrayList<CateVOMenu> menu = this.cateProc.menu();
      model.addAttribute("menu", menu);

      return "openai/fileupload"; // /templates/openai/movie.html
    } else {
      return "redirect:/member/login_cookie_need?url=/openai/fileupload"; // redirect
    }

  }

  @PostMapping("/fileupload")
  @ResponseBody
  public String handleFileUpload(@RequestParam("file") MultipartFile file, @RequestParam("name") String name)
      throws IOException {
    System.out.println("passed");
    String url = "http://localhost:8000/fileupload";

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

    // MultipartFile을 InputStreamFileResource로 변환해서 추가
    body.add("file",
        new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename(), file.getSize()));

    System.out.println("passed2");
    body.add("name", name);

    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

    RestTemplate restTemplate = new RestTemplate();
    
    System.out.println("passed3");
    String response = restTemplate.postForObject(url, requestEntity, String.class);

    System.out.println(response);

    return response;
  }

}