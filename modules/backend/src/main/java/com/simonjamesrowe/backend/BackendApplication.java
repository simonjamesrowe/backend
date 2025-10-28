package com.simonjamesrowe.backend;

import com.simonjamesrowe.searchservice.config.ElasticSearchIndexProperties;
import com.simonjamesrowe.searchservice.dataproviders.cms.CmsProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties({
    ElasticSearchIndexProperties.class,
    CmsProperties.class
})
@ComponentScan(basePackages = {
    "com.simonjamesrowe.apigateway",
    "com.simonjamesrowe.searchservice"
})
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }
}
