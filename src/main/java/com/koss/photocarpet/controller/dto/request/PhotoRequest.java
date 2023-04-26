package com.koss.photocarpet.controller.dto.request;

import com.koss.photocarpet.domain.exhibition.Exhibition;
import com.koss.photocarpet.domain.photo.Photo;
import lombok.*;

import javax.validation.constraints.NotNull;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class PhotoRequest {
    private Long photoId;
    @NotNull
    private Long exhibitionId;
    private Boolean soldOut;

    private Long price;

    public Photo toEntity(PhotoRequest photoRequest, Exhibition getExhibition, String photoUrl) {
        return Photo.builder()
                .exhibition(getExhibition)
                .soldOut(photoRequest.getSoldOut())
                .price(photoRequest.getPrice())
                .url(photoUrl)
                .build();
    }
}