package com.simonjamesrowe.apigateway.test.restcontroller;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.simonjamesrowe.apigateway.config.SecurityConfig;
import com.simonjamesrowe.apigateway.config.RestClientConfig;
import com.simonjamesrowe.apigateway.core.usecase.ICompressFileUseCase;
import com.simonjamesrowe.apigateway.entrypoints.restcontroller.UploadController;
import com.simonjamesrowe.apigateway.test.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

@WebMvcTest(controllers = UploadController.class)
@Import({RestClientConfig.class, SecurityConfig.class})
@AutoConfigureWireMock(port = 0)
@DirtiesContext
class UploadControllerTest {

    @MockBean
    private ICompressFileUseCase compressFileUseCase;

    @Autowired
    private WireMockServer wireMockServer;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void before() {
        TestUtils.mockGetJpg(wireMockServer, "/uploads/image.png", "image.png");
    }

    @Test
    void uploadedFileIsCompressedAndCached() throws Exception {
        byte[] compressedBytes = "Compressed".getBytes();
        when(compressFileUseCase.compress(any(), any())).thenReturn(compressedBytes);

        byte[] actual = mockMvc.perform(get("/uploads/image.png"))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Type", MediaType.IMAGE_PNG_VALUE))
            .andReturn()
            .getResponse()
            .getContentAsByteArray();

        assertThat(actual).isEqualTo(compressedBytes);

        byte[] cached = mockMvc.perform(get("/uploads/image.png"))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Type", MediaType.IMAGE_PNG_VALUE))
            .andReturn()
            .getResponse()
            .getContentAsByteArray();

        assertThat(cached).isEqualTo(compressedBytes);

        verify(compressFileUseCase, times(1)).compress(any(), any());
    }
}