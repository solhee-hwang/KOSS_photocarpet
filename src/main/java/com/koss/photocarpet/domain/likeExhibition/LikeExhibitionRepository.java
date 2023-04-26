package com.koss.photocarpet.domain.likeExhibition;

import com.koss.photocarpet.domain.exhibition.Exhibition;
import com.koss.photocarpet.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikeExhibitionRepository extends JpaRepository<LikeExhibition,Long> {

    LikeExhibition findByLikeExhibitionId(Long likeExhibitionId);

    LikeExhibition findByExhibitionAndUser(Exhibition exhibition, User user);

    List<LikeExhibition> findByExhibition(Exhibition exhibition);
}
