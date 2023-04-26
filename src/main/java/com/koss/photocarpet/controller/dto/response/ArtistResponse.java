package com.koss.photocarpet.controller.dto.response;

import com.koss.photocarpet.domain.user.User;
import lombok.*;

import java.util.List;

@Builder
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ArtistResponse {
    private Long userId;
    private String nickName;
    private String profileMessage;
    private String profileUrl;
    private List<ExhibitionResponse> exhibitions;


    public static ArtistResponse of(User getUser, List<ExhibitionResponse> exhibitionResponseList) {
        return ArtistResponse.builder()
                .userId(getUser.getUserId())
                .nickName(getUser.getNickname())
                .profileMessage(getUser.getProfile_message())
                .profileUrl(getUser.getProfileUrl())
                .exhibitions(exhibitionResponseList).build();
    }
}
