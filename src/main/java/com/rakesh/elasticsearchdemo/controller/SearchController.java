package com.rakesh.elasticsearchdemo.controller;

import com.rakesh.elasticsearchdemo.document.CourseDocument;
import com.rakesh.elasticsearchdemo.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping
    public Map<String, Object> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @RequestParam(defaultValue = "upcoming") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        SearchHits<CourseDocument> hits = searchService.searchCourses(
                q, minAge, maxAge, minPrice, maxPrice,
                category, type, startDate, sort, page, size
        );

        List<CourseDocument> results = new ArrayList<>();
        for (SearchHit<CourseDocument> hit : hits) {
            results.add(hit.getContent());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("total", hits.getTotalHits());
        response.put("courses", results);
        return response;
    }

    @GetMapping("/suggest")
    public List<String> suggest(@RequestParam String prefix) {
        return searchService.suggestTitles(prefix);
    }
}
