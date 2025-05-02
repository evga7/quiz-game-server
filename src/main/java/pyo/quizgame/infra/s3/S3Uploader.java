package pyo.quizgame.infra.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import pyo.quizgame.domain.S3FileInfo;
import pyo.quizgame.service.S3Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3Uploader {

    private final AmazonS3Client amazonS3Client;
    private final S3Service s3Service;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.bucket.url}")
    private String s3Url;

    @Value("${cloud.aws.region.static}")
    private String region;

    // 전환
    public String upload(MultipartFile multipartFile, String dirName) throws IOException {
        File uploadFile = null;
        try {
            uploadFile = convert(multipartFile)
                    .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File로 전환이 실패했습니다."));
            return upload(uploadFile, dirName);
        } catch (Exception e) {
            log.error("파일 업로드 중 에러 발생: {}", e.getMessage());
            throw e; // 예외를 다시 던집니다.
        } finally {
            // 변환된 파일이 있다면 삭제
            if (uploadFile != null && uploadFile.exists()) {
                removeNewFile(uploadFile);
            }
        }
    }


    //랜덤 UUID로 파일명 변경
    private String upload(File uploadFile, String dirName) {
        String uuid = UUID.randomUUID().toString(); // 🔵 fileName은 UUID로 계속 간다
        String transFileName = dirName + "/" + uuid; // s3에는 uuid 기반으로 저장

        String uploadImageUrl = putS3(uploadFile, transFileName);

        // ✅ fileOriginalName만 현재시간_원래파일이름 형태로 저장
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String originalFilename = uploadFile.getName();
        String newOriginalFileName = timestamp + "_" + originalFilename;

        S3FileInfo s3FileInfo = new S3FileInfo();
        s3FileInfo.setFileOriginalName(newOriginalFileName); // 🔵 여기만 변형
        s3FileInfo.setFileName(uuid); // 🔵 여기는 UUID 그대로
        s3FileInfo.setFilePath(s3Url + transFileName); // 🔵 경로도 그대로

        s3Service.save(s3FileInfo);

        return uploadImageUrl;
    }



    //실질적인 s3에 올리는 코드
    private String putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("파일이 삭제되었습니다.");
        } else {
            log.info("파일이 삭제되지 못했습니다.");
        }
    }

    // 멀티파트 파일 파일로 변환
    private Optional<File> convert(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IllegalArgumentException("파일 이름이 유효하지 않습니다.");
        }

        File convertFile = new File(System.getProperty("java.io.tmpdir") + "/" + originalFilename);
        if (convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }
        return Optional.empty();
    }
}