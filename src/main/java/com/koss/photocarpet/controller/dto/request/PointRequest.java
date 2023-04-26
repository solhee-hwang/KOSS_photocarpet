package com.koss.photocarpet.controller.dto.request;

import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class PointRequest {
    @NotNull
    private String nickName;
    @NotNull
    private Long point;
}
