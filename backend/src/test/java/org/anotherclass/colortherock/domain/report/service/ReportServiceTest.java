package org.anotherclass.colortherock.domain.report.service;

import org.anotherclass.colortherock.domain.member.entity.Member;
import org.anotherclass.colortherock.domain.member.repository.MemberRepository;
import org.anotherclass.colortherock.domain.report.entity.Report;
import org.anotherclass.colortherock.domain.report.exception.ReportOneselfException;
import org.anotherclass.colortherock.domain.report.repository.ReportReadRepository;
import org.anotherclass.colortherock.domain.report.repository.ReportRepository;
import org.anotherclass.colortherock.domain.report.request.PostReportRequest;
import org.anotherclass.colortherock.domain.video.entity.Video;
import org.anotherclass.colortherock.domain.video.repository.VideoRepository;
import org.anotherclass.colortherock.domain.videoboard.entity.VideoBoard;
import org.anotherclass.colortherock.domain.videoboard.exception.PostNotFoundException;
import org.anotherclass.colortherock.domain.videoboard.repository.VideoBoardRepository;
import org.anotherclass.colortherock.global.error.GlobalBaseException;
import org.anotherclass.colortherock.global.error.GlobalErrorCode;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ReportServiceTest {
    @Autowired
    EntityManager entityManager;

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    private VideoBoardRepository videoBoardRepository;
    @Autowired
    private ReportService reportService;
    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private ReportReadRepository reportReadRepository;

    private ArrayList<Long> memberIds;
    private ArrayList<Long> videoBoardIds;

    @BeforeEach
    void setData() {
        memberIds = new ArrayList<>();
        videoBoardIds = new ArrayList<>();
        // Member, Video, VideoBoard 생성
        for (int i = 0; i < 10; i++) {
            Member member = new Member(i + "@rock.com", i + "user", Member.RegistrationId.kakao);
            memberRepository.save(member);
            memberIds.add(member.getId());
            Video video = Video.builder()
                    .shootingDate(LocalDate.parse("2022-01-30"))
                    .level(1)
                    .gymName("더클라이밍")
                    .s3URL("url")
                    .videoName("name")
                    .isSuccess(true)
                    .thumbnailURL("url")
                    .thumbnailName("name")
                    .color("초록")
                    .member(member)
                    .build();
            videoRepository.save(video);
            VideoBoard videoBoard = VideoBoard.builder()
                    .title("나 성공함")
                    .isHidden(false)
                    .video(video)
                    .member(member)
                    .build();
            videoBoardRepository.save(videoBoard);
            videoBoardIds.add(videoBoard.getId());
        }

        // 신고 생성
        for (int i = 1; i < 3; i++) {
            VideoBoard videoBoard = videoBoardRepository.findById(videoBoardIds.get(0))
                    .orElseThrow(() -> new PostNotFoundException(GlobalErrorCode.POST_NOT_FOUND));
            Member member = memberRepository.findById(memberIds.get(i))
                    .orElseThrow(() -> new GlobalBaseException(GlobalErrorCode.USER_NOT_FOUND));
            for (int j = 0; j < 2; j++) {
                Report report = new Report("TYPE_A", member, videoBoard);
                reportRepository.save(report);
            }
        }
    }

    @Test
    @DisplayName("게시물 신고 하기")
    void reportPost() {
        // given
        Member member = memberRepository.findById(memberIds.get(3))
                .orElseThrow(() -> new GlobalBaseException(GlobalErrorCode.USER_NOT_FOUND));
        Long videoBoardId = videoBoardIds.get(0);
        String categoryName = "TYPE_B";
        PostReportRequest request = new PostReportRequest(videoBoardId, categoryName);
        // when
        reportService.reportPost(member, request);
        // then
        Long cnt = reportReadRepository.countReport(videoBoardId);
        assertEquals(3, cnt);
    }

    @Test
    @DisplayName("신고 5회 초과시 해당 비디오 hidden 처리")
    void hideReportPost() {
        // when
        VideoBoard videoBoard = videoBoardRepository.findById(videoBoardIds.get(0))
                .orElseThrow(() -> new PostNotFoundException(GlobalErrorCode.POST_NOT_FOUND));
        for (int i = 3; i < 6; i++) {
            Member member = memberRepository.findById(memberIds.get(i))
                    .orElseThrow(() -> new GlobalBaseException(GlobalErrorCode.USER_NOT_FOUND));
            PostReportRequest request = new PostReportRequest(videoBoardIds.get(0), "TYPE_A");
            reportService.reportPost(member, request);
        }
        // then
        Long cnt = reportReadRepository.countReport(videoBoard.getId());
        assertEquals(5, cnt);
        assertTrue(videoBoard.getIsHidden());
    }

    @Test
    @DisplayName("신고 5회 미만일시에는 해당 비디오 숨김처리 되지 않음")
    void notEnoughReport() {
        // when
        VideoBoard videoBoard = videoBoardRepository.findById(videoBoardIds.get(0))
                .orElseThrow(() -> new PostNotFoundException(GlobalErrorCode.POST_NOT_FOUND));
        for (int i = 3; i < 5; i++) {
            Member member = memberRepository.findById(memberIds.get(i))
                    .orElseThrow(() -> new GlobalBaseException(GlobalErrorCode.USER_NOT_FOUND));
            PostReportRequest request = new PostReportRequest(videoBoardIds.get(0), "TYPE_A");
            reportService.reportPost(member, request);
        }
        // then
        Long cnt = reportReadRepository.countReport(videoBoard.getId());
        assertEquals(4, cnt);
        assertFalse(videoBoard.getIsHidden());
    }

    @Test
    @DisplayName("스스로를 신고할 경우 예외 발생")
    void reportOneself() {
        // when
        Member member = memberRepository.findById(memberIds.get(0))
                .orElseThrow(() -> new PostNotFoundException(GlobalErrorCode.POST_NOT_FOUND));
        PostReportRequest request = new PostReportRequest(videoBoardIds.get(0), "TYPE_A");
        // then
        try {
            reportService.reportPost(member, request);
            fail("Expected MemberReportException");
        } catch (ReportOneselfException e) {
            // Assert that the exception is the expected exception
            Assertions.assertThat(e.getMessage()).isEqualTo("본인 스스로를 신고할 수 없습니다.");
        }


    }

    @Test
    @DisplayName("신고 동시성 테스트")
    @Rollback
    void q3reportConcurrencyTest() throws InterruptedException {

        ExecutorService executor = Executors.newFixedThreadPool(3);
        CountDownLatch countDownLatch = new CountDownLatch(3);

        Member member1 = memberRepository.findById(memberIds.get(5))
                .orElseThrow(() -> new GlobalBaseException(GlobalErrorCode.USER_NOT_FOUND));
        Member member2 = memberRepository.findById(memberIds.get(6))
                .orElseThrow(() -> new GlobalBaseException(GlobalErrorCode.USER_NOT_FOUND));
        Member member3 = memberRepository.findById(memberIds.get(7))
                .orElseThrow(() -> new GlobalBaseException(GlobalErrorCode.USER_NOT_FOUND));
        PostReportRequest request = new PostReportRequest(videoBoardIds.get(0), "TYPE_A");
        executor.execute(() -> {
            reportService.reportPost(member1, request);
            countDownLatch.countDown();
        });
        executor.execute(() -> {
            reportService.reportPost(member2, request);
            countDownLatch.countDown();

        });
        executor.execute(() -> {
            reportService.reportPost(member3, request);
            countDownLatch.countDown();

        });
        countDownLatch.await();
        executor.shutdown();
        Long count = reportService.checkReportNum(videoBoardIds.get(0));
        VideoBoard vb1 = videoBoardRepository.findById(videoBoardIds.get(0)).orElseThrow();
        Boolean isHidden = vb1.getIsHidden();
        System.out.println("isHidden = " + isHidden);
        assertEquals(5, count);
        assertTrue(isHidden);


    }
}