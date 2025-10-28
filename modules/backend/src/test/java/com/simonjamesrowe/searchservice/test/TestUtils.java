package com.simonjamesrowe.searchservice.test;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.simonjamesrowe.model.cms.dto.ImageResponseDTO;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.stream.Collectors;

public final class TestUtils {

    private TestUtils() {
        // Utility class
    }

    public static ImageResponseDTO image(String image, int size) {
        return new ImageResponseDTO(
            "uploads/" + image + ".jpg",
            "image1",
            size,
            size * 10,
            size * 10,
            "jpg",
            new ImageResponseDTO.ImageFormats(
                new ImageResponseDTO(
                    "uploads/" + image + "-thumb.jpg",
                    image + "-thumb",
                    size * 2,
                    size * 2,
                    size * 2,
                    "jpg",
                    null
                ),
                new ImageResponseDTO(
                    "uploads/" + image + "-sml.jpg",
                    image + "-sml",
                    size * 3,
                    size * 3,
                    size * 3,
                    "jpg",
                    null
                ),
                new ImageResponseDTO(
                    "uploads/" + image + "-med.jpg",
                    image + "-mde",
                    size * 4,
                    size * 4,
                    size * 4,
                    "jpg",
                    null
                ),
                new ImageResponseDTO(
                    "uploads/" + image + "-lg.jpg",
                    image + "-lg",
                    size * 5,
                    size * 5,
                    size * 5,
                    "jpg",
                    null
                )
            )
        );
    }

    public static void mockGet(WireMockServer wireMockServer, String uri, String responseBodyFile) {
        try {
            String responseBody = Files.lines(
                new ClassPathResource(responseBodyFile).getFile().toPath()
            ).collect(Collectors.joining(System.lineSeparator()));

            wireMockServer.addStubMapping(
                WireMock.stubFor(
                    WireMock.get(WireMock.urlEqualTo(uri))
                        .willReturn(
                            WireMock.aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(responseBody)
                        )
                )
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to read response body file: " + responseBodyFile, e);
        }
    }

    public static <T> T randomObject(Class<T> clazz, Map<String, Object> args) {
        EasyRandomParameters parameters = new EasyRandomParameters();
        args.forEach((key, value) ->
            parameters.randomize(field -> field.getName().equals(key), () -> value)
        );

        return new EasyRandom(parameters).nextObject(clazz);
    }

    public static <T> T randomObject(Class<T> clazz) {
        return new EasyRandom().nextObject(clazz);
    }
}