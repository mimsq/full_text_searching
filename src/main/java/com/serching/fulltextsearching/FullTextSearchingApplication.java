package com.serching.fulltextsearching;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@SpringBootApplication
@EnableElasticsearchRepositories(basePackages = "com.serching.fulltextsearching.repository")
public class FullTextSearchingApplication {

    public static void main(String[] args) {
        SpringApplication.run(FullTextSearchingApplication.class, args);
    }

}