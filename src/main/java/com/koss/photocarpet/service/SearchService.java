package com.koss.photocarpet.service;

import com.koss.photocarpet.controller.dto.response.ExhibitionResponse;
import com.koss.photocarpet.controller.dto.response.SocialUserResponse;
import com.koss.photocarpet.controller.dto.response.UserInExhibition;
import com.koss.photocarpet.domain.customMood.CustomMood;
import com.koss.photocarpet.domain.customMood.CustomMoodRepository;
import com.koss.photocarpet.domain.exhibition.Exhibition;
import com.koss.photocarpet.domain.exhibition.ExhibitionRepository;
import com.koss.photocarpet.domain.moodRelation.MoodRelationRepository;
import com.koss.photocarpet.domain.user.User;
import com.koss.photocarpet.domain.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

@Service
@Slf4j
public class SearchService {
    @Autowired
    private CustomMoodRepository customMoodRepository;
    @Autowired
    private ExhibitionRepository exhibitionRepository;
    @Autowired
    private MoodRelationRepository moodRelationRepository;
    @Autowired
    private UserRepository userRepository;

    Map<Long, Integer> search_exhibition_score_record;
    Map<Long, Integer> search_artist_score_record;
    public LinkedHashSet<ExhibitionResponse> search_exhibition(String keyword) {
        search_exhibition_score_record = new HashMap<Long, Integer>();
        Pattern pattern = Pattern.compile("\\s+");

        String[] splitKeywordBySpace = pattern.split(keyword);
        List<String> splitKeywordBySpaceList = Arrays.asList(splitKeywordBySpace);

        for(String splitKeyword: splitKeywordBySpaceList) {
            searchByCustomMood(splitKeyword);
            searchByExhibitionName(splitKeyword);
            searchByUsername(splitKeyword);
            searchByExhibitionContent(splitKeyword);
        }

        List<Long> keySet = new ArrayList<>(search_exhibition_score_record.keySet());
        keySet.sort((o1,o2) -> search_exhibition_score_record.get(o2).compareTo(search_exhibition_score_record.get(o1)));

        System.out.println(search_exhibition_score_record);

        return setResult(keySet);
    }

    private LinkedHashSet<ExhibitionResponse> setResult(List<Long> keySet) {
        LinkedHashSet<ExhibitionResponse> result = new LinkedHashSet<>();
        for (Long exhibition_id :  keySet) {
            Exhibition exhibition = exhibitionRepository.findByExhibitionId(exhibition_id);
            User user = exhibition.getUser();
            UserInExhibition userInExhibition = UserInExhibition.builder()
                    .nickName(user.getNickname())
                    .profilUrl(user.getProfileUrl())
                    .build();
            ExhibitionResponse exhibitionResponse = ExhibitionResponse.builder()
                    .exhibitId(exhibition.getExhibitionId())
                    .title(exhibition.getTitle())
                    .content(exhibition.getContent())
                    .exhibitionDate(exhibition.getExhibitDate())
                    .user(userInExhibition)
                    .build();
            result.add(exhibitionResponse);
        }
        return result;
    }

    private String searchByCustomMood(String keyword) {
        CustomMood customMood = customMoodRepository.findByCustomMood(keyword);
        if(customMood != null) {
            List<Exhibition> exhibitions = moodRelationRepository.findByCustomMoodContaining(customMood);
            for(Exhibition exhibition: exhibitions) {
                long exhibition_id = exhibition.getExhibitionId();
                if (search_exhibition_score_record.containsKey(exhibition_id))
                    search_exhibition_score_record.put(exhibition_id,search_exhibition_score_record.get(exhibition_id) + 2);
                else{
                    search_exhibition_score_record.put(exhibition_id,2);}
            }
        }
        return "customMood ok";
    }

    private void searchByExhibitionName(String keyword) {
        Optional<List<Exhibition>> exhibitions = Optional.ofNullable(exhibitionRepository.findByTitleContaining(keyword));

        if (exhibitions.isPresent()) {
            for (Exhibition exhibition: exhibitions.get()) {
                long exhibition_id = exhibition.getExhibitionId();
                if (search_exhibition_score_record.containsKey(exhibition_id))
                    search_exhibition_score_record.put(exhibition_id,search_exhibition_score_record.get(exhibition_id) + 5);
                else{
                    search_exhibition_score_record.put(exhibition_id,5);}
            }
        }

    }

    private void searchByExhibitionContent(String keyword) {
        List<Exhibition> exhibitions = exhibitionRepository.findByContentContaining(keyword);

        if (exhibitions != null) {
            for (Exhibition exhibition: exhibitions) {
                long exhibition_id = exhibition.getExhibitionId();
                if (search_exhibition_score_record.containsKey(exhibition_id))
                    search_exhibition_score_record.put(exhibition_id,search_exhibition_score_record.get(exhibition_id) + 3);
                else{
                    search_exhibition_score_record.put(exhibition_id,3);}
            }
        }

    }

    private void searchByUsername(String keyword) {
        List<User> users = userRepository.findByNicknameContaining(keyword);
        if(users != null) {
            for(User user: users) {
                List<Exhibition> exhibitions = exhibitionRepository.findByUser(user);
                if (exhibitions != null) {
                    for (Exhibition exhibition: exhibitions) {
                        long exhibition_id = exhibition.getExhibitionId();
                        if (search_exhibition_score_record.containsKey(exhibition_id))
                            search_exhibition_score_record.put(exhibition_id,search_exhibition_score_record.get(exhibition_id) + 4);
                        else{
                            search_exhibition_score_record.put(exhibition_id,4);}
                    }
                }

            }
        }
    }

    public LinkedHashSet<SocialUserResponse> search_artist(String keyword) {
        search_artist_score_record = new HashMap<>();
        Pattern pattern = Pattern.compile("\\s+");

        String[] splitKeywordBySpace = pattern.split(keyword);
        List<String> splitKeywordBySpaceList = Arrays.asList(splitKeywordBySpace);

        for(String splitKeyword: splitKeywordBySpaceList) {
            System.out.println("검색어: " +splitKeyword);
            artistSearchByUsername(splitKeyword);
            artistSearchByExhibition(splitKeyword);
        }
        LinkedHashSet<Long> searchResult = new LinkedHashSet<>();

        List<Long> keySet = new ArrayList<>(search_artist_score_record.keySet());
        keySet.sort((o1,o2) -> search_artist_score_record.get(o2).compareTo(search_artist_score_record.get(o1)));


        return setArtistResult(keySet);
    }

    private LinkedHashSet<SocialUserResponse> setArtistResult(List<Long> keySet) {
        LinkedHashSet<SocialUserResponse> result = new LinkedHashSet<>();
        for (Long user_id :  keySet) {
            Optional<User> user = userRepository.findByUserId(user_id);
            if (user.isPresent()) {
                SocialUserResponse userResponse = SocialUserResponse.builder()
                        .userId(user.get().getUserId())
                        .nickname(user.get().getNickname())
                        .profileUrl(user.get().getProfileUrl())
                        .build();
                result.add(userResponse);
            }
        }
        System.out.println("결과: " + result);
        return result;
    }

    public void artistSearchByUsername(String keyword) {
        List<User> users = userRepository.findByNicknameContaining(keyword);
        for(User user: users) {
            long user_id = user.getUserId();
            if (search_artist_score_record.containsKey(user_id)) {
                search_artist_score_record.put(user_id, search_artist_score_record.get(user_id) + 5);
            }
            else {
                search_artist_score_record.put(user_id, 5);
            }
        }
    }

    public void artistSearchByExhibition(String keyword) {
        List <Exhibition> exhibitions = exhibitionRepository.findByTitleContaining(keyword);
        if(exhibitions != null) {
            for (Exhibition exhibition : exhibitions) {
                long user_id =exhibition.getUser().getUserId();
                if (search_artist_score_record.containsKey(user_id)) {
                    search_artist_score_record.put(user_id, search_artist_score_record.get(user_id) + 2);
                }
                else search_artist_score_record.put(user_id, 2);
            }
        }
    }


}
