package com.rakesh.elasticsearchdemo.service;

import com.rakesh.elasticsearchdemo.document.CourseDocument;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class SearchService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final RestHighLevelClient restHighLevelClient;

    @Autowired
    public SearchService(ElasticsearchOperations elasticsearchOperations, RestHighLevelClient restHighLevelClient) {
        this.elasticsearchOperations = elasticsearchOperations;
        this.restHighLevelClient = restHighLevelClient;
    }

    public SearchHits<CourseDocument> searchCourses(
            String keyword,
            Integer minAge, Integer maxAge,
            Double minPrice, Double maxPrice,
            String category, String type,
            Instant startDate,
            String sort,
            int page, int size
    ) {
        Criteria criteria = new Criteria();

        if (keyword != null && !keyword.isEmpty()) {
            criteria = criteria.and(new Criteria("title").matches(keyword))
                    .or(new Criteria("description").matches(keyword));
        }

        if (minAge != null || maxAge != null) {
            Criteria ageCriteria = new Criteria("minAge");
            if (minAge != null) ageCriteria = ageCriteria.greaterThanEqual(minAge);
            if (maxAge != null) ageCriteria = ageCriteria.lessThanEqual(maxAge);
            criteria = criteria.and(ageCriteria);
        }

        if (minPrice != null || maxPrice != null) {
            Criteria priceCriteria = new Criteria("price");
            if (minPrice != null) priceCriteria = priceCriteria.greaterThanEqual(minPrice);
            if (maxPrice != null) priceCriteria = priceCriteria.lessThanEqual(maxPrice);
            criteria = criteria.and(priceCriteria);
        }

        if (category != null) {
            criteria = criteria.and(new Criteria("category").is(category));
        }

        if (type != null) {
            criteria = criteria.and(new Criteria("type").is(type));
        }

        if (startDate != null) {
            criteria = criteria.and(new Criteria("nextSessionDate").greaterThanEqual(startDate));
        }

        Sort sortField = Sort.by("nextSessionDate").ascending();
        if ("priceAsc".equalsIgnoreCase(sort)) {
            sortField = Sort.by("price").ascending();
        } else if ("priceDesc".equalsIgnoreCase(sort)) {
            sortField = Sort.by("price").descending();
        }

        PageRequest pageable = PageRequest.of(page, size, sortField);
        CriteriaQuery query = new CriteriaQuery(criteria, pageable);

        return elasticsearchOperations.search(query, CourseDocument.class);
    }

    public List<String> suggestTitles(String prefix) {
        try {
            CompletionSuggestionBuilder completionSuggestionBuilder =
                    SuggestBuilders.completionSuggestion("suggest")
                            .prefix(prefix)
                            .skipDuplicates(true)
                            .size(10);

            SuggestBuilder suggestBuilder = new SuggestBuilder()
                    .addSuggestion("title-suggest", completionSuggestionBuilder);

            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.suggest(suggestBuilder);

            SearchRequest searchRequest = new SearchRequest("courses");
            searchRequest.source(sourceBuilder);

            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            List<String> results = new ArrayList<>();
            Suggest.Suggestion<? extends Suggest.Suggestion.Entry<? extends Suggest.Suggestion.Entry.Option>> suggestion =
                    response.getSuggest().getSuggestion("title-suggest");

            for (Suggest.Suggestion.Entry<? extends Suggest.Suggestion.Entry.Option> entry : suggestion.getEntries()) {
                for (Suggest.Suggestion.Entry.Option option : entry.getOptions()) {
                    Text text = option.getText();
                    results.add(text.string());
                }
            }

            return results;

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.singletonList("Error: " + e.getMessage());
        }
    }
}
