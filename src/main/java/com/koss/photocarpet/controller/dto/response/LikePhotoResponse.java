package com.koss.photocarpet.controller.dto.response;

import com.koss.photocarpet.domain.photo.Photo;
import com.koss.photocarpet.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LikePhotoResponse {
    private Long likePhotoId;
    private Long userId;

    private Long photoId;
}
