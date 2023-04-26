package com.koss.photocarpet.controller;

import com.koss.photocarpet.controller.dto.request.ExhibitionRequest;
import com.koss.photocarpet.controller.dto.response.ArtistResponse;
import com.koss.photocarpet.controller.dto.response.ExhibitionResponse;
import com.koss.photocarpet.domain.exhibition.Exhibition;
import com.koss.photocarpet.domain.moodRelation.MoodRelationRepository;
import com.koss.photocarpet.service.ExhibitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/exhibition")
@RequiredArgsConstructor
public class ExhibitionController {
    private final ExhibitionService exhibitionService;
    private final MoodRelationRepository moodRelationRepository;
    @PostMapping(value = "/create",consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> create (@Valid @RequestPart ExhibitionRequest exhibitionRequest, @RequestPart MultipartFile file) throws Exception{
//        Long USERID = Long.parseLong(userId);
        ExhibitionResponse exhibitionResponse = exhibitionService.create(exhibitionRequest,file);
        return ResponseEntity.ok(exhibitionResponse);
    }
    @PutMapping("/update")
    public ResponseEntity<?> update(final @RequestPart ExhibitionRequest exhibitionRequest, @RequestPart MultipartFile file) throws Exception{
        ExhibitionResponse updateExhibition = exhibitionService.update(exhibitionRequest,file);
        return ResponseEntity.ok(updateExhibition);
    }
    @DeleteMapping("/{exhibitionId}")
    public String delete(@PathVariable Long exhibitionId) throws Exception{
        exhibitionService.delete(exhibitionId);
        return "delete : " + exhibitionId;
    }
    @PutMapping(value = "/image")
    public ResponseEntity<?> saveImage(@RequestParam MultipartFile file, @RequestParam Long exhibitionId) throws Exception{
        exhibitionService.saveImage(file, exhibitionId);
        return ResponseEntity.ok("ok");
    }
    @GetMapping("/morelike")
    public ResponseEntity<?> morelike(){
        List<ExhibitionResponse> likeToRecentexhibitions = exhibitionService.morelike();
        return ResponseEntity.ok(likeToRecentexhibitions);
    }
    @GetMapping("/recent")
    public ResponseEntity<?> recent(){
        List<ExhibitionResponse> recentExhibitions = exhibitionService.recent();
        return ResponseEntity.ok(recentExhibitions);
    }
    //local upload controller
    @PostMapping(value = "/uploadImage",consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> localImageUpload (@Valid @RequestPart ExhibitionRequest exhibitionRequest, @RequestPart MultipartFile file) throws Exception{
        exhibitionService.create(exhibitionRequest,file);
        return ResponseEntity.ok("ok");
    }
    //전시회 1개 조회 해시태그 미포함
    @GetMapping("/{exhibitionId}")
    public ResponseEntity<?> getExhibitionMain(@PathVariable Long exhibitionId) {
        ExhibitionResponse exhibitionResponse = exhibitionService.getExhibitionMain(exhibitionId);
        return ResponseEntity.ok(exhibitionResponse);

    }
    //전시회 1개 조회 해시태그 포함
    @GetMapping("/withhashtag/{exhibitionId}")
    public ResponseEntity<?> getExhibitionInfo(@PathVariable Long exhibitionId){
        ExhibitionResponse exhibitionResponse = exhibitionService.getExhibitionInfo(exhibitionId);
        return ResponseEntity.ok(exhibitionResponse);
    }
    @GetMapping("/artist/{nickName}")
    public ResponseEntity<?> getExhibitions(@PathVariable String nickName) {
        ArtistResponse artistResponse = exhibitionService.getExhibitionsWithUser(nickName);
        return ResponseEntity.ok(artistResponse);
    }

    @GetMapping("/{userId}/likeExhibitions")
    public ResponseEntity<?> getLikeExhibitionList(@PathVariable Long userId) {
        List<ExhibitionResponse> exhibitionResponseList = exhibitionService.getLikeExhibitionsList(userId);
        return ResponseEntity.ok(exhibitionResponseList);
    }


}
