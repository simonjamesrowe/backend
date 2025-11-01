package com.simonjamesrowe.backend;

import com.simonjamesrowe.backend.config.ElasticSearchIndexProperties;
import com.simonjamesrowe.backend.dataproviders.cms.CmsProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties({
    ElasticSearchIndexProperties.class,
    CmsProperties.class
})
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }
}
