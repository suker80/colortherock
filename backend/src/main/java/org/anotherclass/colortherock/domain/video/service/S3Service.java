package org.anotherclass.colortherock.domain.video.service;

import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;

public interface S3Service {
    @PostConstruct
    void setS3Client();

    String upload(MultipartFile file, String videoName);

    String uploadThumbnail(MultipartFile videoFile, String thumbnailName);

    String uploadFromOV(String dir, String videoName);

    String uploadThumbnailFromOV(String dir, String thumbnailName);

    void deleteFile(String videoName);
}
