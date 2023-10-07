package org.anotherclass.colortherock.domain.video.service;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Primary()
public class MockS3Service implements S3Service{

    @Override
    public void setS3Client() {
        // TODO document why this method is empty
    }

    @Override
    public String upload(MultipartFile file, String videoName) {
        return videoName;
    }

    @Override
    public String uploadThumbnail(MultipartFile videoFile, String thumbnailName) {
        return thumbnailName;
    }

    @Override
    public String uploadFromOV(String dir, String videoName) {
        return videoName;
    }

    @Override
    public String uploadThumbnailFromOV(String dir, String thumbnailName) {
        return thumbnailName;
    }

    @Override
    public void deleteFile(String videoName) {
        // TODO document why this method is empty
    }
}
