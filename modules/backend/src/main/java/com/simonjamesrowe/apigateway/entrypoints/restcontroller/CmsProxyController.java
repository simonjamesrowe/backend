package com.simonjamesrowe.apigateway.entrypoints.restcontroller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
@RequiredArgsConstructor
public class CmsProxyController {

    private final RestClient restClient;

    @Value("${cms.url}")
    private String cmsUrl;

    @GetMapping({"/jobs", "/profiles", "/tags", "/blogs", "/skills-groups", "/social-medias", "/tour-steps", "/skills"})
    public ResponseEntity<String> proxyCmsRequest(HttpServletRequest request) {
        String path = request.getRequestURI();
        String queryString = request.getQueryString();
        String fullUrl = cmsUrl + path + (queryString != null ? "?" + queryString : "");

        return restClient.get()
                .uri(fullUrl)
                .retrieve()
                .toEntity(String.class);
    }

    @GetMapping("/blogs/{id}")
    public ResponseEntity<String> proxyCmsBlogRequest(@PathVariable String id, HttpServletRequest request) {
        String queryString = request.getQueryString();
        String fullUrl = cmsUrl + "/blogs/" + id + (queryString != null ? "?" + queryString : "");

        return restClient.get()
                .uri(fullUrl)
                .retrieve()
                .toEntity(String.class);
    }
}
