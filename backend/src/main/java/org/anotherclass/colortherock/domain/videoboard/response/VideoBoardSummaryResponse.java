package org.anotherclass.colortherock.domain.videoboard.response;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "게시글 요약 응답")
public class VideoBoardSummaryResponse {

    @Schema(description = "게시글 id")
    private Long videoBoardId;
    @Schema(description = "제목")
    private String title;

    @Schema(description = "섬네일 url")
    private String thumbnailURL;
    @Schema(description = "색깔")
    private String color;
    @Schema(description = "색상 코드")
    private String colorCode;
    @Schema(description = "암장 이름")
    private String gymName;
    @Schema(description = "생성 시간")
    private LocalDate createdDate;

    @QueryProjection
    public VideoBoardSummaryResponse(Long videoBoardId, String title, String thumbnailURL, String color, String gymName, LocalDateTime createdDate) {
        this.videoBoardId = videoBoardId;
        this.title = title;
        this.thumbnailURL = thumbnailURL;
        this.color = color;
        this.colorCode = ColorCodeKorean.getColor(color);
        this.gymName = gymName;
        this.createdDate = createdDate.toLocalDate();
    }
}
