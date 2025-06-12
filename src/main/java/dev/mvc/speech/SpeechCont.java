package dev.mvc.speech;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Path;

@Controller
@RequestMapping("/speech")
public class SpeechCont {
  
  
  @GetMapping(value = "/test1")
  public String form(Model model) {
    return "/speech/speechtest"; // /templates/mail/form.html
  }
  
  @GetMapping(value = "/test2")
  public String form2(Model model) {
    return "/speech/speechtest2"; // /templates/mail/form.html
  }
  
  @PostMapping("/upload")
  public String upload(@RequestParam("file") MultipartFile file) throws IOException {
      if (file.isEmpty()) {
          return "파일이 없습니다.";
      }

      // 저장할 위치
      String uploadDir = "C:/upload/voice"; // 윈도우 기준
      Files.createDirectories(Paths.get(uploadDir)); // 디렉토리 생성

      // 파일 저장
      java.nio.file.Path filePath = Paths.get(uploadDir, file.getOriginalFilename());
      Files.write(filePath, file.getBytes());

      return "업로드 완료";
  }

}
