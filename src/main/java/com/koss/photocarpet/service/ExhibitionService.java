package com.koss.photocarpet.service;


import com.koss.photocarpet.controller.ImageHandler;
import com.koss.photocarpet.controller.dto.request.ExhibitionRequest;
import com.koss.photocarpet.controller.dto.response.ArtistResponse;
import com.koss.photocarpet.controller.dto.response.ExhibitionResponse;
import com.koss.photocarpet.controller.dto.response.UserInExhibition;
import com.koss.photocarpet.domain.customMood.CustomMood;
import com.koss.photocarpet.domain.exhibition.Exhibition;
import com.koss.photocarpet.domain.exhibition.ExhibitionRepository;
import com.koss.photocarpet.domain.likeExhibition.LikeExhibition;
import com.koss.photocarpet.domain.user.User;
import com.koss.photocarpet.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExhibitionService {
    private final CustomMoodService customMoodService;
    private final ExhibitionRepository exhibitionRepository;

    private final UserRepository userRepository;
    private final ImageHandler imageHandler;
    private final UserService userService;
    private final S3Upload s3Upload;
    private final MoodRelationService moodRelationService;
    //임의의 user 미리 만들어둠.  코드 합칠때 수정예정
    public ExhibitionResponse create(ExhibitionRequest exhibitionRequest, MultipartFile file) throws Exception {
        User getUser = userService.getUser(exhibitionRequest.getUserId());
        invalidArguments(exhibitionRequest);
        Exhibition exhibition = exhibitionRequest.toExhibitionEntity(exhibitionRequest,getUser);
//        String exhibitionImageUrl = imageHandler.pareseFileInfo(file,exhibition.getUser().getUserId());
        String exhibitionImageUrl = s3Upload.upload(file);
        exhibition.updateThumbnailUrl(exhibitionImageUrl);
        exhibitionRepository.save(exhibition);
        exhibition = customMoodService.setCustomMoods(exhibitionRequest.getCustomMoods(),exhibitionRepository.findByExhibitionId(exhibition.getExhibitionId()));

        User user = exhibition.getUser();
        UserInExhibition userInExhibition = UserInExhibition.builder()
                .profilUrl(user.getProfileUrl())
                .nickName(user.getNickname())
                .build();

        Exhibition getExhibition = getExhibition(exhibition.getExhibitionId());
        List<CustomMood> getMoods = moodRelationService.getMoods(getExhibition);
        List<String> moodContents = customMoodService.getContents(getMoods);

        ExhibitionResponse exhibitionResponse = ExhibitionResponse.builder()
                .exhibitId(exhibition.getExhibitionId())
                .title(exhibition.getTitle())
                .content(exhibition.getContent())
                .likeCount(exhibition.getLikeCount())
                .exhibitionDate(exhibition.getExhibitDate())
                .thumbUrl(exhibition.getThumbnailUrl())
                .user(userInExhibition)
                .moodContents(moodContents)
                .build();
        return exhibitionResponse;

    }


    private void invalidArguments(ExhibitionRequest exhibitionRequest){
        if (exhibitionRequest.getTitle().equals("") || exhibitionRequest.getContent().equals("") || exhibitionRequest.getExhibitionDate().equals("")) {
            log.warn("title : " +  exhibitionRequest.getTitle() + ", content : " + exhibitionRequest.getContent() + ", Date : " + exhibitionRequest.getExhibitionDate());
            throw new IllegalArgumentException("빈 string을 입력했습니다");
        }
    }

    public ExhibitionResponse update(ExhibitionRequest exhibitionRequest, MultipartFile file) throws Exception{
        Exhibition getExhibition = getExhibition(exhibitionRequest.getExhibitionId());
        invalidArguments(exhibitionRequest);
//        imageHandler.deleteFile(getExhibition.getThumbnailUrl());
//        s3Upload.fileDelete(getExhibition.getThumbnailUrl());
        String exhibitionImageUrl = s3Upload.upload(file);
        getExhibition.updateTitleContentDate(exhibitionRequest.getTitle(), exhibitionRequest.getContent(),exhibitionRequest.getExhibitionDate());
        getExhibition.updateThumbnailUrl(exhibitionImageUrl);
        getExhibition = customMoodService.setCustomMoods(exhibitionRequest.getCustomMoods(), getExhibition);
        Exhibition savedExhibition = exhibitionRepository.save(getExhibition);
        return ExhibitionResponse.of(savedExhibition.getExhibitionId(),savedExhibition.getTitle(),savedExhibition.getContent(),savedExhibition.getUser().getNickname(),savedExhibition.getUser().getProfileUrl(),savedExhibition.getLikeCount(),savedExhibition.getExhibitDate(), savedExhibition.getThumbnailUrl());
    }
    public Exhibition getExhibition(Long exhibitionId){
        Exhibition exhibition = exhibitionRepository.findByExhibitionId(exhibitionId);
        if (exhibition ==null) throw new RuntimeException("없는 전시회를 가져올 수 없습니다!");
        return exhibition;
    }

    public void delete(Long exhibitionId) throws Exception{
        Exhibition getExhibition = getExhibition(exhibitionId);
//        imageHandler.deleteFile(getExhibition.getThumbnailUrl());
//        s3Upload.fileDelete(getExhibition.getThumbnailUrl());
        exhibitionRepository.delete( getExhibition);
    }

    public void saveImage(MultipartFile file,Long exhibitionId) throws Exception {
        Exhibition getExhibiton = getExhibition(exhibitionId);
//        String url = imageHandler.pareseFileInfo(files,getExhibiton.getUser().getUserId());
        String url =  s3Upload.upload(file);
        getExhibiton.updateThumbnailUrl(url);
        exhibitionRepository.save(getExhibiton);
    }

    public List<ExhibitionResponse> morelike() {
        Sort likeRecent = Sort.by(
                Sort.Order.desc("likeCount"),
                Sort.Order.desc("createDate")
        );
        List<Exhibition> arrayLikeToRecent = exhibitionRepository.findAll(likeRecent);
        return arrayLikeToRecent.stream().map(ExhibitionResponse::new).collect(Collectors.toList());
    }
    public List<ExhibitionResponse> recent(){
        List<Exhibition> recentExhibitions = exhibitionRepository.findAllByOrderByCreateDateDesc();
        return recentExhibitions.stream().map(ExhibitionResponse::new).collect(Collectors.toList());

    }
    public void uploadLocalImage(ExhibitionRequest exhibitionRequest, MultipartFile file) throws Exception {
        User getUser = userService.getUser(exhibitionRequest.getUserId());
        invalidArguments(exhibitionRequest);
        Exhibition exhibition = exhibitionRequest.toExhibitionEntity(exhibitionRequest,getUser);
        String exhibitionImageUrl = imageHandler.pareseFileInfo(file,exhibition.getUser().getUserId());
        exhibition.updateThumbnailUrl(exhibitionImageUrl);
        exhibitionRepository.save(exhibition);
    }

    public ExhibitionResponse getExhibitionMain(Long exhibitionId) {
        Exhibition getExhibition = getExhibition(exhibitionId);
        return ExhibitionResponse.of(getExhibition.getExhibitionId(),getExhibition.getTitle(), getExhibition.getContent(), getExhibition.getUser().getNickname(),getExhibition.getUser().getProfileUrl(), getExhibition.getLikeCount(),getExhibition.getExhibitDate(), getExhibition.getThumbnailUrl());

    }
    public ExhibitionResponse getExhibitionInfo(Long exhibitionId){
        Exhibition getExhibition = getExhibition(exhibitionId);
        List<CustomMood> getMoods = moodRelationService.getMoods(getExhibition);
        List<String> moodContents = customMoodService.getContents(getMoods);
        return ExhibitionResponse.withHashTages(getExhibition.getExhibitionId(), getExhibition.getTitle(), getExhibition.getContent(), getExhibition.getUser().getNickname(), getExhibition.getUser().getProfileUrl(), getExhibition.getLikeCount(), getExhibition.getExhibitDate(), getExhibition.getThumbnailUrl(), moodContents);
    }

    public ArtistResponse getExhibitionsWithUser(String nickName) {
        User getUser = userService.getUserByNickName(nickName);
        List<Exhibition> exhibitions = exhibitionRepository.findByUser(getUser);
        List<ExhibitionResponse> exhibitionResponseList = exhibitions.stream().map(ExhibitionResponse::new).collect(Collectors.toList());
        return ArtistResponse.of(getUser, exhibitionResponseList);
    }

    public List<ExhibitionResponse> getLikeExhibitionsList(Long userId) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createDate");
        Optional<User> user = userRepository.findByUserId(userId);
        List<LikeExhibition> likeExhibitions = user.get().getLikeExhibitions();
        likeExhibitions.sort((e1, e2) -> e2.getModifiedDate().compareTo(e1.getModifiedDate()));

        List<ExhibitionResponse> likeExhibitionsResult = new ArrayList<>();

        for (LikeExhibition likeExhibition: likeExhibitions) {
            Exhibition exhibition = likeExhibition.getExhibition();

            Exhibition getExhibition = getExhibition(exhibition.getExhibitionId());
            List<CustomMood> getMoods = moodRelationService.getMoods(getExhibition);
            List<String> moodContents = customMoodService.getContents(getMoods);

            ExhibitionResponse exhibitionResponse = ExhibitionResponse.builder()
                    .exhibitId(exhibition.getExhibitionId())
                    .title(exhibition.getTitle())
                    .content(exhibition.getContent())
                    .likeCount(exhibition.getLikeCount())
                    .exhibitionDate(exhibition.getExhibitDate())
                    .thumbUrl(exhibition.getThumbnailUrl())
                    .moodContents(moodContents)
                    .build();

            likeExhibitionsResult.add(exhibitionResponse);
        }
        return likeExhibitionsResult;
    }
}
