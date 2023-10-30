package org.anotherclass.colortherock.domain.videoboard.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.anotherclass.colortherock.domain.videoboard.entity.VideoBoard;
import org.anotherclass.colortherock.domain.videoboard.request.VideoBoardSearchRequest;
import org.anotherclass.colortherock.domain.videoboard.response.QVideoBoardSummaryResponse;
import org.anotherclass.colortherock.domain.videoboard.response.VideoBoardSummaryResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static org.anotherclass.colortherock.domain.member.entity.QMember.member;
import static org.anotherclass.colortherock.domain.videoboard.entity.QVideoBoard.videoBoard;
import static org.anotherclass.colortherock.domain.video.entity.QVideo.video;

@Repository
public class VideoBoardReadRepository {


    private final JPAQueryFactory query;

    public VideoBoardReadRepository(EntityManager em) {
        this.query = new JPAQueryFactory(em);
    }


    public Slice<VideoBoardSummaryResponse> searchByCond(VideoBoardSearchRequest condition, Pageable pageable) {

        Long lastStoreId = condition.getStoreId();
        String gymNameCond = condition.getGymName();
        String colorCond = condition.getColor();

        List<VideoBoardSummaryResponse> fetch = query.select(new QVideoBoardSummaryResponse(
                        videoBoard.id,
                        videoBoard.title,
                        video.thumbnailURL,
                        video.color,
                        video.gymName,
                        videoBoard.createdDate
                ))
                .from(videoBoard)
                .join(video)
                .on(videoBoard.id.eq(video.id))
                .where(
                        // 숨김처리 되어 있지 않은 영상만 가져오기
                        videoBoard.isHidden.eq(false),
                        // no-offset 페이징 처리
                        checkStoreId(lastStoreId),
                        // 암장 검색
                        checkGymName(gymNameCond),
                        // 색상 검색
                        checkColor(colorCond)
                )
                .orderBy(videoBoard.id.desc())
                .limit(pageable.getPageSize() + 1L)
                .fetch();

        boolean hasNext = false;

        // 조회한 결과 개수가 요청한 페이지 사이즈보다 크면 뒤에 더 있음, next = true
        if (fetch.size() > pageable.getPageSize()) {
            hasNext = true;
            fetch.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(fetch, pageable, hasNext);
        // 무한 스크롤 처리
    }

    public Slice<VideoBoard> getMySuccessPosts(Long memberId, Long storeId, Pageable pageable) {

        List<VideoBoard> results = query.selectFrom(videoBoard)
                .join(videoBoard.member, member)
                .fetchJoin()
                .where(
                        // 숨김처리 되어 있지 않은 영상만 가져오기
                        videoBoard.isHidden.eq(false),
                        // no-offset 페이징 처리
                        checkStoreId(storeId),
                        // 유저 검색
                        videoBoard.member.id.eq(memberId)
                )
                .orderBy(videoBoard.id.desc())
                .limit(pageable.getPageSize() + 1L)
                .fetch();

        return checkLastPage(pageable, results);
    }

    // no-offset 방식 처리하는 메서드 (storeId가 없을 경우, 있을 경우)
    private BooleanExpression checkStoreId(Long storeId) {
        if (storeId == null || storeId == -1L) {
            return null;
        }

        return videoBoard.id.lt(storeId);
    }

    // 암장 검색을 처리하는 메서드
    private BooleanExpression checkGymName(String gymNameCond) {
        if (gymNameCond == null || gymNameCond.isBlank()) {
            return null;
        }
        return videoBoard.video.gymName.contains(gymNameCond);
    }

    // 레벨 검색을 처리하는 메서드
    private BooleanExpression checkColor(String colorCond) {
        if (colorCond == null || colorCond.isBlank()) {
            return null;
        }
        return videoBoard.video.color.eq(colorCond);
    }

    // 무한 스크롤 방식 처리하는 메서드
    private Slice<VideoBoard> checkLastPage(Pageable pageable, List<VideoBoard> results) {

        boolean hasNext = false;

        // 조회한 결과 개수가 요청한 페이지 사이즈보다 크면 뒤에 더 있음, next = true
        if (results.size() > pageable.getPageSize()) {
            hasNext = true;
            results.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(results, pageable, hasNext);
    }


}
