package com.koss.photocarpet.service;

import com.koss.photocarpet.domain.customMood.CustomMood;
import com.koss.photocarpet.domain.customMood.CustomMoodRepository;
import com.koss.photocarpet.domain.exhibition.Exhibition;
import com.koss.photocarpet.domain.exhibition.ExhibitionRepository;
import com.koss.photocarpet.domain.moodRelation.MoodRelation;
import com.koss.photocarpet.domain.moodRelation.MoodRelationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomMoodService {
    @Autowired
    private CustomMoodRepository customMoodRepository;
    @Autowired
    private ExhibitionRepository exhibitionRepository;
    @Autowired
    private MoodRelationRepository moodRelationRepository;

    public Exhibition setCustomMoods(List<String> customMoodNames, Exhibition exhibition) {
        if(customMoodNames == null) return exhibition;

        List<MoodRelation> moodRelations = new ArrayList<>();
        for(String customMoodName: customMoodNames) {
            CustomMood customMood = customMoodRepository.findByCustomMood(customMoodName);

            if(customMood == null) {
                customMood = CustomMood.builder()
                        .customMood(customMoodName)
                        .build();
                customMood = customMoodRepository.save(customMood);
            }
            MoodRelation moodRelation = MoodRelation.builder()
                    .customMood(customMood)
                    .exhibition(exhibition)
                    .build();
            moodRelationRepository.save(moodRelation);
            moodRelations.add(moodRelation);
        }
        exhibition.setMoodRelations(moodRelations);
        exhibitionRepository.save(exhibition);
        return exhibition;
    }
    public List<String> getContents(List<CustomMood> customMoods){
        List<String> contents = customMoods.stream().map(CustomMood::getCustomMood).collect(Collectors.toList());
        return contents;
    }

}
