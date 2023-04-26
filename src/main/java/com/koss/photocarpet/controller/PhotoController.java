package com.koss.photocarpet.controller;

import com.koss.photocarpet.controller.dto.request.PhotoRequest;
import com.koss.photocarpet.controller.dto.response.ExhibitionResponse;
import com.koss.photocarpet.controller.dto.response.PhotoResponse;
import com.koss.photocarpet.service.PhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.config.RepositoryNameSpaceHandler;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/photo")
@RequiredArgsConstructor
public class PhotoController {
    private final PhotoService photoService;

    @PostMapping(value = "/create", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> create(@Valid @RequestPart PhotoRequest photoRequest, @RequestPart MultipartFile file) throws Exception {
        photoService.create(photoRequest, file);
        return ResponseEntity.ok("ok");
    }

    @PutMapping(value = "/update", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> update(@RequestPart PhotoRequest photoRequest, @RequestPart MultipartFile file) throws Exception {
        photoService.update(photoRequest, file);
        return ResponseEntity.ok("update");
    }
    @DeleteMapping("/{photoId}")
    public ResponseEntity<?> delete(@PathVariable Long photoId) throws Exception{
        photoService.delete(photoId);
        return ResponseEntity.ok("delete");
    }
    @GetMapping("/{exhibitionId}")
    public ResponseEntity<?> getArts(@PathVariable Long exhibitionId) throws Exception{
        List<PhotoResponse> allArts =  photoService.getArts(exhibitionId);
        return ResponseEntity.ok(allArts);

    }
    @GetMapping("/getArt/{photoId}")
    public ResponseEntity<?> getPhoto(@PathVariable Long photoId){
        PhotoResponse getPhotoResponse = photoService.getArt(photoId);
        return ResponseEntity.ok(getPhotoResponse);
    }

    @PostMapping("/like/{userId}/{photoId}")
    public ResponseEntity<?> likePhoto(@PathVariable Long userId, @PathVariable Long photoId) {
        PhotoResponse photoResponse = photoService.likePhoto(userId,photoId);
        return ResponseEntity.ok(photoResponse);
    }

    @GetMapping("/{userId}/likePhotos")
    public ResponseEntity<?> getLikePhotosList(@PathVariable Long userId) {
        List<PhotoResponse> photoResponseList = photoService.getLikePhotosList(userId);
        return ResponseEntity.ok(photoResponseList);
    }
}
