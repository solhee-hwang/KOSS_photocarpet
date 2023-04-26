package com.koss.photocarpet.controller;

import com.koss.photocarpet.controller.dto.response.ExhibitionResponse;
import com.koss.photocarpet.controller.dto.response.SearchResultResponse;

import com.koss.photocarpet.controller.dto.response.SocialUserResponse;
import com.koss.photocarpet.domain.exhibition.Exhibition;
import com.koss.photocarpet.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashSet;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class SearchController {
    @Autowired
    private final SearchService searchService;

    @GetMapping("/search/exhibition/{keyword}")
    public ResponseEntity<?> searchExhibitionResult(@PathVariable String keyword) {
        try {
            LinkedHashSet<ExhibitionResponse> result = searchService.search_exhibition(keyword);
            SearchResultResponse searchResultResponse = SearchResultResponse.builder().result(result).build();
            return ResponseEntity.ok().body(searchResultResponse);
        }
        catch (Exception e) {
            SearchResultResponse searchResultResponse = SearchResultResponse.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(searchResultResponse);
        }
    }

    @GetMapping("/search/artist/{keyword}")
    public ResponseEntity<?> searchArtistResult(@PathVariable String keyword) {
        try {
            LinkedHashSet<SocialUserResponse> result = searchService.search_artist(keyword);
            SearchResultResponse searchResultResponse = SearchResultResponse.builder().result(result).build();
            return ResponseEntity.ok().body(searchResultResponse);
        }
        catch (Exception e) {
            SearchResultResponse searchResultResponse = SearchResultResponse.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(searchResultResponse);
        }
    }
}
