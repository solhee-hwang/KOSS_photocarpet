package com.koss.photocarpet.domain.pointRecord;
import com.koss.photocarpet.controller.dto.request.PointRequest;
import com.koss.photocarpet.domain.BaseTimeEntity;
import com.koss.photocarpet.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Entity(name="point_record")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointRecord extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pointRecordId;

    @Column
    private Long amount;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public static PointRecord toEntity(PointRequest pointRequest, User user) {
        return PointRecord.builder().amount(pointRequest.getPoint())
                .user(user).build();

    }
}