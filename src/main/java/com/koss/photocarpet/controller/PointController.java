package com.koss.photocarpet.controller;

import com.koss.photocarpet.controller.dto.request.PointRequest;
import com.koss.photocarpet.service.PointRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/point")
@RequiredArgsConstructor
public class PointController {
    private final PointRecordService pointRecordService;
    @PostMapping
    public ResponseEntity<?> chargePoint(@Valid @RequestBody PointRequest pointRequest){
        pointRecordService.chargePoint(pointRequest);
        return ResponseEntity.ok("charge complete");
    }
}
