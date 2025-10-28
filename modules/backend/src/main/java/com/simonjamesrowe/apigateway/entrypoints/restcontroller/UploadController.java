package com.simonjamesrowe.apigateway.entrypoints.restcontroller;

import com.simonjamesrowe.apigateway.core.usecase.ICompressFileUseCase;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@Slf4j
public class UploadController {

    private static final ConcurrentHashMap<String, byte[]> IMAGE_CACHE = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, HttpHeaders> IMAGE_HEADERS_CACHE = new ConcurrentHashMap<>();
    private static final String TMP_DIR = System.getProperty("java.io.tmpdir");

    private final String cmsUrl;
    private final ICompressFileUseCase compressFileUseCase;
    private final RestClient restClient;

    public UploadController(@Value("${cms.url}") String cmsUrl,
                          ICompressFileUseCase compressFileUseCase,
                          RestClient restClient) {
        this.cmsUrl = cmsUrl;
        this.compressFileUseCase = compressFileUseCase;
        this.restClient = restClient;
    }

    @GetMapping("/uploads/{file}")
    public ResponseEntity<byte[]> proxy(
            @PathVariable String file,
            @RequestHeader HttpHeaders headers) {

        String fileUrl = cmsUrl + "/uploads/" + file;

        if (!isImage(file)) {
            return restClient.get()
                    .uri(fileUrl)
                    .retrieve()
                    .toEntity(byte[].class);
        }

        log.debug("Request for file has been made {}", file);
        log.debug("headers are {}", headers);

        if (IMAGE_CACHE.containsKey(file)) {
            log.debug("Image is in the cache!");
            return new ResponseEntity<>(IMAGE_CACHE.get(file), IMAGE_HEADERS_CACHE.get(file), HttpStatus.OK);
        }

        ResponseEntity<byte[]> response = restClient.get()
                .uri(fileUrl)
                .retrieve()
                .toEntity(byte[].class);
        return processAndCacheImage(file, response);
    }

    private ResponseEntity<byte[]> processAndCacheImage(String file, ResponseEntity<byte[]> response) {
        try {
            File tmpFile = new File(TMP_DIR, file);
            IOUtils.copyLarge(new ByteArrayInputStream(response.getBody()), new FileOutputStream(tmpFile));

            byte[] compressedBytes = compressFileUseCase.compress(tmpFile, response.getBody().length);

            IMAGE_CACHE.put(file, compressedBytes);
            IMAGE_HEADERS_CACHE.put(file, response.getHeaders());
            return new ResponseEntity<>(compressedBytes, response.getHeaders(), HttpStatus.OK);

        } catch (IOException e) {
            log.error("Error processing image file: {}", file, e);
            return response;
        }
    }

    private boolean isImage(String file) {
        return file.endsWith(".jpg") || file.endsWith(".png");
    }
}