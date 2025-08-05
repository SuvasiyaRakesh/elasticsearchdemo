package com.rakesh.elasticsearchdemo.repository;

import com.rakesh.elasticsearchdemo.document.CourseDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends ElasticsearchRepository<CourseDocument, String> {
}
