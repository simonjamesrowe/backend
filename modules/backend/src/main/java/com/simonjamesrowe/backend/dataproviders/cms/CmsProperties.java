package com.simonjamesrowe.backend.dataproviders.cms;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("cms")
public record CmsProperties(
    String url
) {
}