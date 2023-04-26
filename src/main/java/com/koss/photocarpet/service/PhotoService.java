package com.koss.photocarpet.service;

import com.koss.photocarpet.controller.ImageHandler;
import com.koss.photocarpet.controller.dto.request.PhotoRequest;
import com.koss.photocarpet.controller.dto.response.ExhibitionResponse;
import com.koss.photocarpet.controller.dto.response.LikePhotoResponse;
import com.koss.photocarpet.controller.dto.response.PhotoResponse;
import com.koss.photocarpet.domain.customMood.CustomMood;
import com.koss.photocarpet.domain.exhibition.Exhibition;
import com.koss.photocarpet.domain.likeExhibition.LikeExhibition;
import com.koss.photocarpet.domain.likePhoto.LikePhoto;
import com.koss.photocarpet.domain.likePhoto.LikePhotoRepository;
import com.koss.photocarpet.domain.photo.Photo;
import com.koss.photocarpet.domain.photo.PhotoRepository;
import com.koss.photocarpet.domain.user.User;
import com.koss.photocarpet.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PhotoService {
    private final ImageHandler imageHandler;
    private final PhotoRepository photoRepository;
    private final ExhibitionService exhibitionService;
    private final S3Upload s3Upload;
    private final LikePhotoRepository likePhotoRepository;
    private final UserRepository userRepository;

    public void create(PhotoRequest photoRequest, MultipartFile file) throws Exception {
        Exhibition getExhibition = exhibitionService.getExhibition(photoRequest.getExhibitionId());
//        String photoUrl = imageHandler.pareseFileInfo(file,getExhibition.getUser().getUserId());
//        Photo photo = photoRequest.toEntity(photoRequest,getExhibition,photoUrl);
        String S3Url = s3Upload.upload(file);
        Photo photo = photoRequest.toEntity(photoRequest, getExhibition, S3Url);
        photoRepository.save(photo);
    }

    public void update(PhotoRequest photoRequest, MultipartFile file) throws Exception {
        Photo getPhoto = getPhoto(photoRequest.getPhotoId());
//        imageHandler.deleteFile(getPhoto.getUrl());
//        String photoUrl = imageHandler.pareseFileInfo(file, getPhoto.getExhibition().getUser().getUserId());
        String photoUrl = s3Upload.upload(file);
        getPhoto.updateAll(photoUrl, photoRequest.getSoldOut(), photoRequest.getPrice());
        photoRepository.save(getPhoto);
    }

    public Photo getPhoto(Long photoId) {
        return photoRepository.findByPhotoId(photoId)
                .orElseThrow(() -> new IllegalArgumentException("없는 전시물 아이디입니다"));
    }

    public void delete(Long photoId) throws Exception {
        Photo getPhoto = getPhoto(photoId);
        imageHandler.deleteFile(getPhoto.getUrl());
        photoRepository.delete(getPhoto);
    }

    public List<PhotoResponse> getArts(Long exhibitionId) {
        Exhibition getExhibition = exhibitionService.getExhibition(exhibitionId);
        List<Photo> allArts = photoRepository.findByExhibition(getExhibition);
        return allArts.stream().map(PhotoResponse::new).collect(Collectors.toList());
    }

    public PhotoResponse getArt(Long photoId) {
        Photo getPhoto = getPhoto(photoId);
        return PhotoResponse.of(getPhoto.getExhibition().getExhibitionId(), getPhoto.getPhotoId(), getPhoto.getUrl(), getPhoto.getPrice(), getPhoto.isSoldOut());
    }

    public PhotoResponse likePhoto(Long userId, Long photoId) {
        Photo getPhoto = getPhoto(photoId);
        Optional<User> user = userRepository.findByUserId(userId);
        if (user.isPresent()) {
            LikePhoto likePhoto = LikePhoto.builder()
                    .photo(getPhoto)
                    .user(user.get())
                    .build();
            likePhotoRepository.save(likePhoto);
            LikePhotoResponse likePhotoResponse = LikePhotoResponse.builder()
                    .userId(userId)
                    .likePhotoId(likePhoto.getLikePhotoId())
                    .build();
            Photo photo = photoRepository.findByPhotoId(photoId).get();
            List<LikePhoto> likephotos = photo.getLikephotos();
            likephotos.add(likePhoto);
            photo.setLikephotos(likephotos);
            photoRepository.save(photo);

            PhotoResponse photoResponse = PhotoResponse.builder()
                    .likePhotoResponse(likePhotoResponse)
                    .photoId(photoId)
                    .exhibitionId(getPhoto.getExhibition().getExhibitionId())
                    .build();
            return photoResponse;
        }
        else {
            throw new RuntimeException("User not found");
        }
    }

    public List<PhotoResponse> getLikePhotosList(Long userId) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createDate");
        Optional<User> user = userRepository.findByUserId(userId);
        List<LikePhoto> likePhotos = user.get().getLikePhotos();
        likePhotos.sort((e1, e2) -> e2.getModifiedDate().compareTo(e1.getModifiedDate()));

        List<PhotoResponse> likePhotosResult = new ArrayList<>();

        for (LikePhoto likePhoto: likePhotos) {
            Photo photo = likePhoto.getPhoto();
            PhotoResponse photoResponse = PhotoResponse.builder()
                            .exhibitionId(photo.getExhibition().getExhibitionId())
                                    .photoId(photo.getPhotoId())
                                            .artUrl(photo.getUrl())
                                                    .price(photo.getPrice())
                                                            .soldOut(photo.isSoldOut())
                                                                    .build();


            likePhotosResult.add(photoResponse);
        }
        return likePhotosResult;
    }
}