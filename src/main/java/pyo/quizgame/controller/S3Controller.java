package pyo.quizgame.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import pyo.quizgame.infra.s3.S3Uploader;

import java.io.IOException;


@Slf4j
@RequiredArgsConstructor
@Controller
public class S3Controller {
    private final S3Uploader s3Uploader;

    //addQuiz 에서 Js로 실행함
    //static(dirname) 폴더에 들어감
    @Operation(summary = "S3 이미지 업로드 API")
    @PostMapping("/quiz/image-upload")
    @ResponseBody
    public String upload(@RequestParam("data") MultipartFile multipartFile) throws IOException {
        return s3Uploader.upload(multipartFile, "static");
    }
}
