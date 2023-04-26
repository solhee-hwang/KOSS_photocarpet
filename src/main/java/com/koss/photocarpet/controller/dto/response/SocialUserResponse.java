package com.koss.photocarpet.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.koss.photocarpet.domain.user.User;
import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialUserResponse {
    private Long userId;
    private String nickname;
    private String email;

    private String profileUrl;

    private String jwtToken;

    private String accessToken;

    private String profileMessage;

    private boolean validate_check;

    public SocialUserResponse(Long id, String nickname, String email){
        this.userId = id;
        this.nickname = nickname;
        this.email = email;
    }

    public SocialUserResponse(User user) {
        this.userId = user.getUserId();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.profileUrl = user.getProfileUrl();
    }
}
