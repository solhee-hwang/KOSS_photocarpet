package com.koss.photocarpet.service;

import com.koss.photocarpet.controller.dto.request.PointRequest;
import com.koss.photocarpet.domain.pointRecord.PointRecord;
import com.koss.photocarpet.domain.pointRecord.PointRecordRepository;
import com.koss.photocarpet.domain.user.User;
import com.koss.photocarpet.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PointRecordService {
    private final PointRecordRepository pointRecordRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    public void chargePoint(PointRequest pointRequest) {
        User user = userService.getUserByNickName(pointRequest.getNickName());
        user.chargePoint(pointRequest.getPoint());
        userRepository.save(user);
        PointRecord pointRecord = PointRecord.toEntity(pointRequest, user);
        pointRecordRepository.save(pointRecord);
    }
}
