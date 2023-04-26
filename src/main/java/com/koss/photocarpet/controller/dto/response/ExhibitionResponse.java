package com.koss.photocarpet.controller.dto.response;


import com.koss.photocarpet.domain.exhibition.Exhibition;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@ToString
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ExhibitionResponse {
    private Long exhibitId;
    private String title;
    private String content;

    private Long likeCount;
    private Date exhibitionDate;
    private String thumbUrl;
    private UserInExhibition user;
    private List<String> moodContents;




    public ExhibitionResponse(Exhibition exhibition) {
        this.user = UserInExhibition.of(exhibition.getUser().getNickname(), exhibition.getUser().getProfileUrl());
        this.exhibitId = exhibition.getExhibitionId();
        this.content = exhibition.getContent();
        this.title = exhibition.getTitle();
        this.likeCount = exhibition.getLikeCount();
        this.exhibitionDate = exhibition.getExhibitDate();
        this.thumbUrl =exhibition.getThumbnailUrl();
    }


    public static ExhibitionResponse of(Long exhibitId, String title, String content,String userNicName,String imageUrl, Long likeCount, Date exhibitionDate,String thumbUrl) {
        UserInExhibition user = UserInExhibition.of(userNicName, imageUrl);
        return ExhibitionResponse.builder()
                .exhibitId(exhibitId)
                .user(user)
                .title(title)
                .content(content)
                .likeCount(likeCount)
                .thumbUrl(thumbUrl)
                .exhibitionDate(exhibitionDate).build();
    }
    public static ExhibitionResponse withHashTages(Long exhibitId, String title, String content,String userNicName,String imageUrl, Long likeCount, Date exhibitionDate,String thumbUrl,List<String> moodContents) {
        UserInExhibition user = UserInExhibition.of(userNicName, imageUrl);
        return ExhibitionResponse.builder()
                .exhibitId(exhibitId)
                .user(user)
                .title(title)
                .content(content)
                .likeCount(likeCount)
                .thumbUrl(thumbUrl)
                .moodContents(moodContents)
                .exhibitionDate(exhibitionDate).build();
    }

}

