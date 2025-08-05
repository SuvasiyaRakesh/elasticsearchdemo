package com.rakesh.elasticsearchdemo.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rakesh.elasticsearchdemo.document.CourseDocument;
import com.rakesh.elasticsearchdemo.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.data.elasticsearch.core.suggest.Completion;


import java.io.InputStream;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private CourseRepository courseRepository;

    @Override
    public void run(String... args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        InputStream inputStream = new ClassPathResource("sample-courses.json").getInputStream();

        /*List<CourseDocument> courses = mapper.readValue(inputStream, new TypeReference<List<CourseDocument>>() {});

        // 🔍 Debug: Print the number of courses loaded
        System.out.println("📦 Loaded courses from JSON: " + courses.size());

        for (CourseDocument course : courses) {
            System.out.println("➡️  Title: " + course.getTitle() + " | ID: " + course.getId());
        }*/

        List<CourseDocument> courses = mapper.readValue(inputStream, new TypeReference<List<CourseDocument>>() {});

        courses.forEach(course -> {
            course.setSuggest(new Completion(new String[]{course.getTitle()}));
        });
        courseRepository.saveAll(courses);

        System.out.println("✅ Indexed " + courses.size() + " courses into Elasticsearch");
    }

}
