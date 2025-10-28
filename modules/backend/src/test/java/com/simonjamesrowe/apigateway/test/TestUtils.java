package com.simonjamesrowe.apigateway.test;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
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

    public static void mockGet(WireMockServer wireMockServer, String uri, String responseBodyFile) {
        try {
            wireMockServer.addStubMapping(
                WireMock.stubFor(
                    WireMock.get(WireMock.urlEqualTo(uri))
                        .willReturn(
                            WireMock.aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(
                                    Files.lines(
                                        new ClassPathResource(responseBodyFile).getFile().toPath()
                                    ).collect(Collectors.joining(System.lineSeparator()))
                                )
                        )
                )
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void mockGetJpg(WireMockServer wireMockServer, String uri, String responseBodyFile) {
        try {
            wireMockServer.addStubMapping(
                WireMock.stubFor(
                    WireMock.get(WireMock.urlEqualTo(uri))
                        .willReturn(
                            WireMock.aResponse()
                                .withHeader("Content-Type", "image/png")
                                .withBody(
                                    Files.readAllBytes(
                                        new ClassPathResource(responseBodyFile).getFile().toPath()
                                    )
                                )
                        )
                )
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T randomObject(Class<T> clazz) {
        return randomObject(clazz, Map.of());
    }

    public static <T> T randomObject(Class<T> clazz, Map<String, Object> args) {
        EasyRandomParameters parameters = new EasyRandomParameters();
        args.forEach((key, value) ->
            parameters.randomize(field -> field.getName().equals(key), () -> value)
        );

        return new EasyRandom(parameters).nextObject(clazz);
    }
}