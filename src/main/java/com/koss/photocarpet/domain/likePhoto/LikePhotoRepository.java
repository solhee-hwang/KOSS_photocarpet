package com.koss.photocarpet.domain.likePhoto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikePhotoRepository extends JpaRepository<LikePhoto,Long> {
}
