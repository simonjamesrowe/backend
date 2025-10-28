package com.simonjamesrowe.apigateway.test.usecase;

import com.simonjamesrowe.apigateway.core.usecase.CompressFileUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class CompressFileUseCaseTest {

    private final CompressFileUseCase compressFileUseCase = new CompressFileUseCase(1000);

    @Test
    void shouldCompressFile() throws Exception {
        var testImage = new ClassPathResource("big-image.jpg").getFile();
        int testImageSize = Files.readAllBytes(testImage.toPath()).length;

        byte[] compressed = compressFileUseCase.compress(testImage, testImageSize);

        assertThat(compressed.length).isLessThan(testImageSize);
    }
}