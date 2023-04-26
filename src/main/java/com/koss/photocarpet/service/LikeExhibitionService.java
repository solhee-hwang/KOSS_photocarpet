package com.koss.photocarpet.service;

import com.koss.photocarpet.controller.dto.request.LikeExhibitionRequest;
import com.koss.photocarpet.domain.exhibition.Exhibition;
import com.koss.photocarpet.domain.exhibition.ExhibitionRepository;
import com.koss.photocarpet.domain.likeExhibition.LikeExhibition;
import com.koss.photocarpet.domain.likeExhibition.LikeExhibitionRepository;
import com.koss.photocarpet.domain.user.User;
import com.koss.photocarpet.domain.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class LikeExhibitionService {
    @Autowired
    private ExhibitionRepository exhibitionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LikeExhibitionRepository likeExhibitionRepository;
    public void push_like(final Long userId, final Long exhibitionId) {
        Exhibition exhibition = exhibitionRepository.findByExhibitionId(exhibitionId);
        LikeExhibition likeExhibition = LikeExhibition.builder().user(userRepository.findByUserId(userId).get())
                .exhibition(exhibition)
                .build();

        List<LikeExhibition> likeExhibitions = exhibition.getLikeExhibitions();
        likeExhibitions.add(likeExhibition);
        exhibition.setLikeExhibitions(likeExhibitions);
        exhibitionRepository.save(exhibition);

        likeExhibitionRepository.save(likeExhibition);
    }

    public void delete_like(final Long userId, final Long exhibitionId) {
        User user = userRepository.findByUserId(userId).get();
        Exhibition exhibition = exhibitionRepository.findByExhibitionId(exhibitionId);
        LikeExhibition likeExhibition = likeExhibitionRepository.findByExhibitionAndUser(exhibition, user);
        likeExhibitionRepository.delete(likeExhibition);
    }

    public long count_like(final Long exhibitionId) {
        Exhibition exhibition = exhibitionRepository.findByExhibitionId(exhibitionId);
        List<LikeExhibition> likes = likeExhibitionRepository.findByExhibition(exhibition);
        return likes.size();
    }
}
