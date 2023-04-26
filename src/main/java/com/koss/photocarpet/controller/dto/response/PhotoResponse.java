package com.koss.photocarpet.controller.dto.response;

import com.koss.photocarpet.domain.photo.Photo;
import lombok.*;

@ToString
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PhotoResponse {
    private Long exhibitionId;
    private Long photoId;
    private String artUrl;
    private Long price;
    private boolean soldOut;
    private LikePhotoResponse likePhotoResponse;
    public PhotoResponse(Photo photo){
        this.exhibitionId = photo.getExhibition().getExhibitionId();
        this.photoId = photo.getPhotoId();
        this.artUrl = photo.getUrl();
        this.price = photo.getPrice();
        this.soldOut = photo.isSoldOut();
    }
    public static PhotoResponse of(Long exhibitionId, Long photoId,String artUrl, Long price, boolean soldOut) {
        return PhotoResponse.builder()
                .exhibitionId(exhibitionId)
                .photoId(photoId)
                .artUrl(artUrl)
                .price(price)
                .soldOut(soldOut).build();
    }
}