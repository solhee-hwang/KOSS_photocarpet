package com.koss.photocarpet.service;

import com.koss.photocarpet.domain.customMood.CustomMood;
import com.koss.photocarpet.domain.exhibition.Exhibition;
import com.koss.photocarpet.domain.moodRelation.MoodRelation;
import com.koss.photocarpet.domain.moodRelation.MoodRelationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MoodRelationService {
    private final MoodRelationRepository moodRelationRepository;
    public List<CustomMood> getMoods(Exhibition exhibition){
        List<CustomMood> customMoods = new ArrayList<>();
        List<MoodRelation> getRelations = moodRelationRepository.findByExhibition(exhibition).orElse(new ArrayList<>());
        for (MoodRelation relation : getRelations) {
            CustomMood customMood = relation.getCustomMood();
            if (customMood != null) {
                customMoods.add(customMood);
            }
        }
        return customMoods;
    }
}
