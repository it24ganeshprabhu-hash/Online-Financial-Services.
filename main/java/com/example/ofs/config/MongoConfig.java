package com.example.ofs.config;
import com.example.ofs.model.mongodb.OtpEntry;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;

import java.util.concurrent.TimeUnit;

@Configuration
public class MongoConfig {

    @Bean
    public CommandLineRunner initIndexes(MongoTemplate mongoTemplate) {
        return args -> {
            IndexOperations indexOps = mongoTemplate.indexOps(OtpEntry.class);
            Index index = new Index()
                    .on("expiryTime", Sort.Direction.ASC)
                    .expire(300, TimeUnit.SECONDS);
            indexOps.createIndex(index);
        };
    }
}