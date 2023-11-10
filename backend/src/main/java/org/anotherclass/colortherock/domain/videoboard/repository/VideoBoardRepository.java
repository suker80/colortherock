package org.anotherclass.colortherock.domain.videoboard.repository;

import org.anotherclass.colortherock.domain.videoboard.entity.VideoBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface VideoBoardRepository extends JpaRepository<VideoBoard, Long> {

    List<VideoBoard> findAllByIsHiddenTrue();

    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query(value = " update video_board vb set vb.is_hidden = :state where vb.id = :id and (select count(*) from report r where r.video_board_id = :id) >=5", nativeQuery = true)
    int changeHiddenState(@Param("id") Long id, @Param("state") boolean state);


}
