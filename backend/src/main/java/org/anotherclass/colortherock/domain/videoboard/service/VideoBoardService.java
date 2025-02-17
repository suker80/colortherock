package org.anotherclass.colortherock.domain.videoboard.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.anotherclass.colortherock.domain.member.entity.Member;
import org.anotherclass.colortherock.domain.member.repository.MemberRepository;
import org.anotherclass.colortherock.domain.video.entity.Video;
import org.anotherclass.colortherock.domain.video.exception.VideoNotFoundException;
import org.anotherclass.colortherock.domain.video.exception.VideoUserMismatchException;
import org.anotherclass.colortherock.domain.video.repository.VideoRepository;
import org.anotherclass.colortherock.domain.videoboard.entity.VideoBoard;
import org.anotherclass.colortherock.domain.videoboard.exception.PostNotFoundException;
import org.anotherclass.colortherock.domain.videoboard.exception.WriterMismatchException;
import org.anotherclass.colortherock.domain.videoboard.repository.VideoBoardReadRepository;
import org.anotherclass.colortherock.domain.videoboard.repository.VideoBoardRepository;
import org.anotherclass.colortherock.domain.videoboard.request.SuccessPostUpdateRequest;
import org.anotherclass.colortherock.domain.videoboard.request.SuccessVideoUploadRequest;
import org.anotherclass.colortherock.domain.videoboard.request.VideoBoardSearchRequest;
import org.anotherclass.colortherock.domain.videoboard.response.VideoBoardDetailResponse;
import org.anotherclass.colortherock.domain.videoboard.response.VideoBoardSummaryResponse;
import org.anotherclass.colortherock.global.error.GlobalBaseException;
import org.anotherclass.colortherock.global.error.GlobalErrorCode;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoBoardService {
    private final VideoRepository videoRepository;
    private final MemberRepository memberRepository;
    private final VideoBoardRepository videoBoardRepository;
    private final VideoBoardReadRepository videoBoardReadRepository;
    private static final Integer PAGE_SIZE = 16;


    /**
     * 성공 영상 게시판에서 조건에 맞는 영상 가져오기
     *
     * @param condition {@link VideoBoardSearchRequest}
     * @return
     */
    @Transactional(readOnly = true)
    public List<VideoBoardSummaryResponse> getSuccessVideos(VideoBoardSearchRequest condition) {
        Pageable pageable = Pageable.ofSize(PAGE_SIZE);

        Slice<VideoBoardSummaryResponse> slices = videoBoardReadRepository.searchByCond(condition, pageable);

        if (slices.isEmpty()) {
            return new ArrayList<>();
        }

        return slices.toList();
    }

    /**
     * 내가 성공한 영상 게시판에 올리기
     *
     * @param memberId                  멤버 id
     * @param successVideoUploadRequest {@link SuccessVideoUploadRequest}
     * @return
     */
    @Transactional
    public Long uploadMySuccessVideoPost(Long memberId, SuccessVideoUploadRequest successVideoUploadRequest) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GlobalBaseException(GlobalErrorCode.USER_NOT_FOUND));
        Video video = videoRepository.findById(successVideoUploadRequest.getVideoId())
                .orElseThrow(() -> new VideoNotFoundException(GlobalErrorCode.VIDEO_NOT_FOUND));
        if (!video.getMember().getId().equals(memberId)) {
            throw new VideoUserMismatchException(GlobalErrorCode.NOT_VIDEO_OWNER);
        }

        VideoBoard newVideoBoard = videoBoardRepository.save(VideoBoard.builder()
                .title(successVideoUploadRequest.getTitle())
                .isHidden(false)
                .video(video)
                .member(member)
                .build());

        return newVideoBoard.getId();
    }

    /**
     * 완등 영상 게시글 상세 조회
     *
     * @param videoBoardId 게시글 id
     * @return
     */
    @Transactional(readOnly = true)
    public VideoBoardDetailResponse getVideoDetail(Long videoBoardId) {
        VideoBoard vb = videoBoardRepository.findById(videoBoardId)
                .orElseThrow(() -> new PostNotFoundException(GlobalErrorCode.POST_NOT_FOUND));
        if (Boolean.TRUE.equals(vb.getIsHidden())) return null;
        return VideoBoardDetailResponse.builder()
                .videoBoardId(vb.getId())
                .nickname(vb.getMember().getNickname())
                .title(vb.getTitle())
                .s3URL(vb.getVideo().getS3URL())
                .createdDate(vb.getCreatedDate().toLocalDate())
                .build();
    }


    /**
     * 완등 영상 게시글 수정
     *
     * @param memberId 멤버 id
     * @param request  {@link SuccessPostUpdateRequest}
     */
    @Transactional
    public void updateSuccessPost(Long memberId, SuccessPostUpdateRequest request) {
        VideoBoard vb = videoBoardRepository.findById(request.getVideoBoardId())
                .orElseThrow(() -> new PostNotFoundException(GlobalErrorCode.POST_NOT_FOUND));
        checkAuth(memberId, vb);
        vb.update(request.getTitle());
        vb.getVideo().update(request.getLevel(), request.getGymName(), request.getColor());
    }

    /**
     * 완등 영상 게시글 삭제
     *
     * @param videoBoardId 비디오 게시글 id
     * @param memberId     사용자 id
     */
    @Transactional
    public void deleteSuccessPost(Long memberId, Long videoBoardId) {
        VideoBoard vb = videoBoardRepository.findById(videoBoardId)
                .orElseThrow(() -> new PostNotFoundException(GlobalErrorCode.POST_NOT_FOUND));
        checkAuth(memberId, vb);
        // 영상의 isPosted 삭제
        vb.getVideo().postDeleted();
        videoBoardRepository.delete(vb);
    }

    /**
     * 내가 작성한 완등 게시글 조회
     *
     * @param memberId 멤버 id
     * @param storeId  no offset 방식 이전 PK
     */
    @Transactional(readOnly = true)
    public List<VideoBoardSummaryResponse> getMySuccessVideoPosts(Long memberId, Long storeId) {
        Pageable pageable = Pageable.ofSize(8);

        Slice<VideoBoard> slices = videoBoardReadRepository.getMySuccessPosts(memberId, storeId, pageable);

        if (slices.isEmpty()) {
            return new ArrayList<>();
        }

        return slices.toList().stream()
                .map(vb -> VideoBoardSummaryResponse.builder()
                        .videoBoardId(vb.getId())
                        .title(vb.getTitle())
                        .thumbnailURL(vb.getVideo().getThumbnailURL())
                        .color(vb.getVideo().getColor())
                        .createdDate(vb.getCreatedDate().toLocalDate())
                        .build()).collect(Collectors.toList());
    }


    // 받은 멤버가 수정권한이 있는지 확인하는 메서드
    private void checkAuth(Long memberId, VideoBoard videoBoard) {
        if (!videoBoard.getMember().getId().equals(memberId)) {
            throw new WriterMismatchException(GlobalErrorCode.NOT_WRITER);
        }
    }

}
