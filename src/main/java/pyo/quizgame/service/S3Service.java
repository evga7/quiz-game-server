package pyo.quizgame.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pyo.quizgame.domain.S3FileInfo;
import pyo.quizgame.repository.S3Repository;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Repository s3Repository;


    public S3FileInfo getS3FileInfo(String fileOriginalName) {
        return s3Repository.findByFileOriginalName(fileOriginalName);
    }
    public S3FileInfo getS3FileInfoByFileName(String fileOriginalName) {
        return s3Repository.findByFileName(fileOriginalName);
    }

    public void save(S3FileInfo s3FileInfo) {
        s3Repository.save(s3FileInfo);
    }

    public S3FileInfo getS3FileInfoById(Long id) {
        return s3Repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("S3 파일 정보를 찾을 수 없습니다."));
    }

    public S3FileInfo getDefaultS3FileInfo() {
        return s3Repository.findById(1L) // noImage.png 가 id=1에 저장되어 있다고 가정
                .orElseThrow(() -> new IllegalArgumentException("기본 이미지가 없습니다."));
    }
}
